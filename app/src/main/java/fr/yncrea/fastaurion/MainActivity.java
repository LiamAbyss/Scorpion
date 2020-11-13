package fr.yncrea.fastaurion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.fastaurion.api.Aurion;
import fr.yncrea.fastaurion.ui.fragments.coursesFragment;
import fr.yncrea.fastaurion.utils.Constants;
import fr.yncrea.fastaurion.utils.Course;
import fr.yncrea.fastaurion.utils.PreferenceUtils;
import fr.yncrea.fastaurion.utils.UtilsMethods;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Aurion mAurion = new Aurion();
    private String mUsername = "";
    private String mPassword = "";
    private String mName = "";
    private JSONArray mPlanning;
    private coursesFragment mCoursesFragment = new coursesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle extras = intent.getExtras();
            if ((null != extras) && (extras.containsKey(Constants.Login.EXTRA_LOGIN))) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .followSslRedirects(false)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl("https://aurion.yncrea.fr")
                        .build();


                getSupportActionBar().setSubtitle (extras.getString(Constants.Preferences.PREF_NAME));



            }
        }
        if(savedInstanceState==null)  {
            getSupportFragmentManager().beginTransaction().add(R.id.container, mCoursesFragment).commit();
            mExecutor.execute(()->{
                String xml = mAurion.getCalendarAsXML(PreferenceUtils.getSessionId(),0,0)[1];

                Log.d("TAG", "DONE");
                this.mPlanning = UtilsMethods.XMLToJSONArray(xml);
                try {
                    List<Course> planning = UtilsMethods.JSONArrayToCourseList(this.mPlanning);
                    runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(planning));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fastaurion, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if( id == R.id.actionLogout)
        {
            PreferenceUtils.setLogin(null);
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}