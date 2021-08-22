package fr.yncrea.scorpion;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.room.Room;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.api.Aurion;
import fr.yncrea.scorpion.api.GithubService;
import fr.yncrea.scorpion.database.ScorpionDatabase;
import fr.yncrea.scorpion.model.Planning;
import fr.yncrea.scorpion.ui.fragments.CoursesFragment;
import fr.yncrea.scorpion.ui.fragments.GradesFragment;
import fr.yncrea.scorpion.utils.Constants;
import fr.yncrea.scorpion.utils.Course;
import fr.yncrea.scorpion.utils.Grade;
import fr.yncrea.scorpion.utils.PreferenceUtils;
import fr.yncrea.scorpion.utils.UtilsMethods;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GradesActivity extends AppCompatActivity {
    private Executor mExecutorGit = Executors.newSingleThreadExecutor();
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Aurion mAurion = new Aurion();
    private GithubService mGithubService;
    private List<Grade> mGrades = new ArrayList<Grade>();
    private GradesFragment mGradesFragment = new GradesFragment();
    private Toast mToast;
    private ScorpionDatabase db;
    private Timer mTimer;
    private AlertDialog confirmWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        UtilsMethods.parseGrades("");

        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle extras = intent.getExtras();
            if ((null != extras) && (extras.containsKey(Constants.Login.EXTRA_LOGIN))) {
                getSupportActionBar().setSubtitle(extras.getString(Constants.Preferences.PREF_NAME));
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " " + getString(R.string.app_version));
            }
        }

        /*mExecutorFling.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), ScorpionDatabase.class, "Scorpion.db").build();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mCoursesFragment, "planning").commit();
            runOnUiThread(() -> mCoursesFragment.setRefreshing(true));
            requestPlanning(weekIndex, false, true);
            runOnUiThread(() -> mCoursesFragment.setRefreshing(false));
        });*/
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mGrades.add(new Grade("date", "code", "libelle", 18f, "reason", "appreciation", "teachers"));
        mExecutor.execute(() -> {
            showGrades();
        });

        mGithubService = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GithubService.class);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(confirmWindow == null || !confirmWindow.isShowing()) requestUpdate();
            }
        }, 0, 300000);
    }

    public void requestUpdate() {
        mExecutorGit.execute(() -> {
            try {
                Response<JsonArray> releases = mGithubService.getReleases().execute();
                String latestVersion = releases.body().get(0).getAsJsonObject().get("tag_name").getAsString();
                if(!latestVersion.equals(getString(R.string.app_version))) {
                    String latestVersionDesc = releases.body().get(0).getAsJsonObject().get("body").getAsString();
                    //String latestVersionLink = releases.body().get(0).getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    runOnUiThread(() -> {
                        confirmWindow = new AlertDialog.Builder(this)
                                .setTitle("Update available !")
                                .setMessage("A new version of Scorpion is available !\n\n"
                                        + "Version : " + latestVersion + "\n\n"
                                        + "Description : \n" + latestVersionDesc)
                                .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liamabyss.github.io/scorpion/"));
                                            startActivity(myIntent);
                                        }
                                        catch(ActivityNotFoundException e) {
                                            //runOnUiThread(() -> showToast(this, "No application can handle this request." + " Please install a web browser",  Toast.LENGTH_LONG));
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("Maybe later", null).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void showGrades(){
        mGradesFragment = new GradesFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.gradesContainer, mGradesFragment, "grades")
                .commit();
        runOnUiThread(() -> mGradesFragment.onGradesRetrieved(mGrades));
    }

    public boolean connect() {
        String[] sessionID = mAurion.connect(PreferenceUtils.getLogin(), PreferenceUtils.getPassword());
        if(sessionID[0].equals("success")){
            PreferenceUtils.setSessionId(sessionID[1]);
            return true;
        }
        if(sessionID[0].contains("connection")){
            runOnUiThread(()-> showToast(ScorpionApplication.getContext(), "Connection error", Toast.LENGTH_LONG));
        }
        else {
            runOnUiThread(() -> showToast(ScorpionApplication.getContext(), "Authentication Failed", Toast.LENGTH_LONG));
        }
        return false;
    }

    public void refresh() {
        //Refresh the planning
        Log.d("REFRESH", "Refreshing...");
        mGradesFragment.setRefreshing(false);
        /*if(isOtherFragment) {
            runOnUiThread(() -> mOtherCoursesFragment.setRefreshing(true));
        }
        else {
            runOnUiThread(() -> mCoursesFragment.setRefreshing(true));
        }

        //DO REQUEST

        if(isOtherFragment) {
            runOnUiThread(() -> mOtherCoursesFragment.setRefreshing(false));
        }
        else {
            runOnUiThread(() -> mCoursesFragment.setRefreshing(false));
        }*/
        Log.d("REFRESH", "Finish refreshing...");
    }


    private void showToast(Context context, int resId, int duration){
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, resId, duration);
        mToast.show();
    }

    private void showToast(Context context, CharSequence text, int duration){
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
