package fr.yncrea.scorpion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.api.Aurion;
import fr.yncrea.scorpion.database.ScorpionDatabase;
import fr.yncrea.scorpion.model.Planning;
import fr.yncrea.scorpion.ui.fragments.CoursesFragment;
import fr.yncrea.scorpion.utils.Constants;
import fr.yncrea.scorpion.utils.Course;
import fr.yncrea.scorpion.utils.PreferenceUtils;
import fr.yncrea.scorpion.utils.UtilsMethods;
import okhttp3.internal.Util;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Aurion mAurion = new Aurion();
    private List<Course> mCourses;
    private CoursesFragment mCoursesFragment = new CoursesFragment();
    private Toast mToast;
    private GestureDetectorCompat mDetector;
    private ScorpionDatabase db;
    private int weekIndex = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle extras = intent.getExtras();
            if ((null != extras) && (extras.containsKey(Constants.Login.EXTRA_LOGIN))) {
                getSupportActionBar().setSubtitle (extras.getString(Constants.Preferences.PREF_NAME));
            }
        }
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);

        mExecutor.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), ScorpionDatabase.class, "Scorpion.db").build();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mCoursesFragment, "planning").commit();
            requestPlanning(false);
        });
    }

    public void requestPlanning(boolean forceRequest) {
        Planning planning = db.aurionPlanningDao().getPlanningById(weekIndex);
        if(planning != null) {
            mCourses = UtilsMethods.planningFromString(planning.toString());
        } else {
            mCourses = new ArrayList<>();
        }
        if(mCourses.size() != 0 && forceRequest == false){
            runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(mCourses));
        }
        else{
            Planning toInsert = new Planning();
            String[] response = mAurion.getCalendarAsXML(PreferenceUtils.getSessionId(), weekIndex);
            String xml;
            if(response[0] == "success") {
                xml = response[1];
            }
            else if(response[0].contains("authentication") && connect()) {
                requestPlanning(forceRequest);
                return;
            }
            else {
                runOnUiThread(() -> showToast(ScorpionApplication.getContext(), response[0], Toast.LENGTH_LONG));
                if(mCourses.size() != 0){
                    runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(mCourses));
                }
                return;
            }
            JSONArray planningJSON = UtilsMethods.XMLToJSONArray(xml);
            try {
                mCourses = UtilsMethods.JSONArrayToCourseList(planningJSON);
                toInsert.id = weekIndex;
                toInsert.planningString = UtilsMethods.planningToString(mCourses);
                if(planning == null) {
                    db.aurionPlanningDao().insertPlanning(toInsert);
                }
                else {
                    db.aurionPlanningDao().updatePlanning(toInsert);
                }
                runOnUiThread(()-> mCoursesFragment.onCoursesRetrieved(mCourses));
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
        requestPlanning(true);
        Log.d("REFRESH", "Finish refreshing...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scorpion, menu);
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
                runOnUiThread(() -> mCoursesFragment.setRefreshing(true));
                refresh();
                runOnUiThread(() -> mCoursesFragment.setRefreshing(false));
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

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // To the left
        if(Math.abs(velocityY) > 3000) return false;
        if(velocityX > 3000) {
            Log.d("FLING", "To the left !");
            weekIndex--;
            mExecutor.execute(() -> {
                runOnUiThread(() -> mCoursesFragment.setRefreshing(true));
                requestPlanning(false);
                runOnUiThread(() -> mCoursesFragment.setRefreshing(false));
            });
        }
        else if(velocityX < -3000) {
            Log.d("FLING", "To the right !");
            weekIndex++;
            mExecutor.execute(() -> {
                runOnUiThread(() -> mCoursesFragment.setRefreshing(true));
                requestPlanning(false);
                runOnUiThread(() -> mCoursesFragment.setRefreshing(false));
            });
        }
        return true;
    }
}