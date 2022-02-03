package fr.yncrea.scorpion;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.api.Aurion;
import fr.yncrea.scorpion.api.GithubService;
import fr.yncrea.scorpion.database.ScorpionDatabase;
import fr.yncrea.scorpion.model.AurionResponse;
import fr.yncrea.scorpion.model.Grade;
import fr.yncrea.scorpion.ui.fragments.GradesFragment;
import fr.yncrea.scorpion.utils.PreferenceUtils;
import fr.yncrea.scorpion.utils.UtilsMethods;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GradesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private final Executor mExecutorGit = Executors.newSingleThreadExecutor();
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Aurion mAurion = new Aurion();
    private GithubService mGithubService;
    private List<Grade> mGrades = new ArrayList<>();
    private GradesFragment mGradesFragment = new GradesFragment();
    private Toast mToast;
    private ScorpionDatabase db;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public int clickedItem;
    private AlertDialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        getSupportActionBar().setSubtitle(PreferenceUtils.getName());
        //getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " " + getString(R.string.app_version));

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.grades_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(clickedItem == R.id.nav_planning) {
                    mExecutor.execute(() -> {
                        startActivity(getPlanningIntent());
                        overridePendingTransition(0, 0);
                        finish();
                    });
                }
            }
        };

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.grades_navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        /*mExecutorFling.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), ScorpionDatabase.class, "Scorpion.db").build();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mCoursesFragment, "planning").commit();
            runOnUiThread(() -> mCoursesFragment.setRefreshing(true));
            requestPlanning(weekIndex, false, true);
            runOnUiThread(() -> mCoursesFragment.setRefreshing(false));
        });*/

        mGithubService = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GithubService.class);

        mExecutor.execute(() -> {
            mGradesFragment = new GradesFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.gradesContainer, mGradesFragment, "grades")
                    .commit();
            runOnUiThread(() -> mGradesFragment.onGradesRetrieved(mGrades));

            showGrades();
        });
    }

    @Override
    protected void onStop() {
        if (updateDialog != null) updateDialog.dismiss();
        super.onStop();
    }

    public void tryRequestUpdate() {
        mExecutorGit.execute(() -> {
            Long lastTime = PreferenceUtils.getUpdateTime();
            long now = Calendar.getInstance().getTimeInMillis();

            if(now < lastTime + Long.parseLong(getString(R.string.update_timeout))) {
                return;
            }
            else {
                PreferenceUtils.setUpdateTime(now);
            }

            try {
                Response<JsonArray> releases = mGithubService.getReleases().execute();
                if(releases.body() == null) return;
                String latestVersion = releases.body().get(0).getAsJsonObject().get("tag_name").getAsString();
                if(!latestVersion.equals(getString(R.string.app_version))) {
                    String latestVersionDesc = releases.body().get(0).getAsJsonObject().get("body").getAsString();
                    //String latestVersionLink = releases.body().get(0).getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    runOnUiThread(() -> {
                        updateDialog = new AlertDialog.Builder(this)
                                .setTitle("Update available !")
                                .setMessage("A new version of Scorpion is available !\n\n"
                                        + "Version : " + latestVersion + "\n\n"
                                        + "Description : \n" + latestVersionDesc)
                                .setPositiveButton("Update now", (dialog, which) -> {
                                    try {
                                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liamabyss.github.io/Scorpion/"));
                                        startActivity(myIntent);
                                    } catch (ActivityNotFoundException e) {
                                        //runOnUiThread(() -> showToast(this, "No application can handle this request." + " Please install a web browser",  Toast.LENGTH_LONG));
                                        e.printStackTrace();
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

    public void requestGrades(){
        requestGrades(true);
    }

    public void requestGrades(boolean forceRequest){
        requestGrades(forceRequest, 0);
    }

    public void requestGrades(boolean forceRequest, int retryingState){
        tryRequestUpdate();

        runOnUiThread(() -> mGradesFragment.setRefreshing(true));
        AurionResponse res = mAurion.getGrades();
        if(res.status == AurionResponse.SUCCESS){
            mGrades = UtilsMethods.parseGrades(res.body);
        }
        else if(res.message.contains("Authentication") && retryingState == 0 && connect()){
            requestGrades(forceRequest, retryingState + 1);
            return;
        }
        else{
            runOnUiThread(() -> showToast(this, res.message, Toast.LENGTH_LONG));
            return;
        }
        runOnUiThread(() -> mGradesFragment.setRefreshing(false));
    }

    public void showGrades(){

        requestGrades();

        mGrades = UtilsMethods.sortGradesByDate(mGrades, true);

        mGradesFragment = new GradesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gradesContainer, mGradesFragment, "grades")
                .commit();
        runOnUiThread(() -> mGradesFragment.onGradesRetrieved(mGrades));
    }

    public boolean connect() {
        AurionResponse sessionID = mAurion.connect(PreferenceUtils.getLogin(), PreferenceUtils.getPassword());
        if(sessionID.status == AurionResponse.SUCCESS){
            PreferenceUtils.setSessionId(sessionID.cookie);
            return true;
        }
        else {
            runOnUiThread(()-> showToast(this, sessionID.message, Toast.LENGTH_LONG));
        }
        return false;
    }

    public void refresh() {
        //Refresh the planning
        mExecutor.execute(() -> {
            Log.d("REFRESH", "Refreshing...");
            runOnUiThread(() -> mGradesFragment.setRefreshing(true));
            showGrades();
            runOnUiThread(() -> mGradesFragment.setRefreshing(false));
            Log.d("REFRESH", "Finish refreshing...");
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grades_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if( id == R.id.actionRefresh) {
            mExecutor.execute(this::refresh);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        clickedItem = id;

        if(id == R.id.nav_releases) {
            mExecutor.execute(() -> {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liamabyss.github.io/Scorpion/"));
                startActivity(myIntent);
            });
        }
        else if(id == R.id.nav_feedback) {
            mExecutor.execute(() -> {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LiamAbyss/Scorpion/issues/new/choose"));
                startActivity(myIntent);
            });
        }
        else if(id == R.id.nav_mail) {
            mExecutor.execute(() -> {
                String[] email_address = new String[] {getString(R.string.scorpion_email_address)};
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.scorpion_email_address), null));
                intent.putExtra(Intent.EXTRA_EMAIL, email_address);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_question) + " : " + PreferenceUtils.getName());
                startActivity(Intent.createChooser(intent, "Send Email"));
            });
        }
        else if( id == R.id.nav_logout) {
            mExecutor.execute(() -> {
                PreferenceUtils.setPassword(null);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    @Override
    public void onBackPressed() {
        mExecutor.execute(() -> {
            //runOnUiThread(() -> super.onBackPressed());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private Intent getPlanningIntent()
    {
        return new Intent(this, MainActivity.class);
    }

}
