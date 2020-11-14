package fr.yncrea.fastaurion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private List<Course> mPlanning;
    private coursesFragment mCoursesFragment = new coursesFragment();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(mSwipeRefreshLayout == null) {
            mSwipeRefreshLayout = findViewById(R.id.refreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                mExecutor.execute(() -> {
                    refresh();
                    runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
                });
            });
        }
        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle extras = intent.getExtras();
            if ((null != extras) && (extras.containsKey(Constants.Login.EXTRA_LOGIN))) {
                getSupportActionBar().setSubtitle (extras.getString(Constants.Preferences.PREF_NAME));
            }
        }

        mExecutor.execute(() -> {
            getSupportFragmentManager().beginTransaction().add(R.id.refreshLayout, mCoursesFragment).commit();
            requestPlanning(false);
        });
    }

    public void requestPlanning(boolean forceRequest) {
        mPlanning = PreferenceUtils.getPlanning();
        if(mPlanning.size() != 0 && forceRequest == false){
            runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(mPlanning));
        }
        else{
            String[] response = mAurion.getCalendarAsXML(PreferenceUtils.getSessionId(),0,0);
            String xml;
            if(response[0] == "success") {
                xml = response[1];
            }
            else if(response[0].contains("authentication") && connect()) {
                requestPlanning(forceRequest);
                return;
            }
            else {
                runOnUiThread(() -> showToast(FastAurionApplication.getContext(), response[0], Toast.LENGTH_LONG));
                if(mPlanning.size() != 0){
                    runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(mPlanning));
                }
                return;
            }
            JSONArray planningJSON = UtilsMethods.XMLToJSONArray(xml);
            try {
                mPlanning = UtilsMethods.JSONArrayToCourseList(planningJSON);
                PreferenceUtils.setPlanning(mPlanning);
                runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(mPlanning));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean connect() {
        String[] sessionID = mAurion.connect(PreferenceUtils.getLogin(), PreferenceUtils.getPassword());
        if(sessionID[0].equals("success")){
            PreferenceUtils.setSessionId(sessionID[1]);
            return true;
        }
        if(sessionID[0].contains("connection")){
            runOnUiThread(()-> showToast(FastAurionApplication.getContext(), "Connection error", Toast.LENGTH_LONG));
        }
        else {
            runOnUiThread(() -> showToast(FastAurionApplication.getContext(), "Authentication Failed", Toast.LENGTH_LONG));
        }
        return false;
    }

    public void refresh() {
        //Refresh the planning
        Log.d("REFRESH", "Refreshing...");
        requestPlanning(true);
        Log.d("REFRESH", "Finish refreshing...");
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
        if( id == R.id.actionLogout) {
            PreferenceUtils.setLogin(null);
            finish();
            return true;
        }
        else if( id == R.id.actionRefresh) {
            mExecutor.execute(() -> {
                runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(true));
                refresh();
                runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
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