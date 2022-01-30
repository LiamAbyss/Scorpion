package fr.yncrea.scorpion;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.adapters.PersonListAdapter;
import fr.yncrea.scorpion.api.Aurion;
import fr.yncrea.scorpion.api.GithubService;
import fr.yncrea.scorpion.database.ScorpionDatabase;
import fr.yncrea.scorpion.model.AurionResponse;
import fr.yncrea.scorpion.model.CourseDetails;
import fr.yncrea.scorpion.model.Person;
import fr.yncrea.scorpion.model.Planning;
import fr.yncrea.scorpion.ui.fragments.CoursesFragment;
import fr.yncrea.scorpion.utils.PreferenceUtils;
import fr.yncrea.scorpion.utils.UtilsMethods;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, NavigationView.OnNavigationItemSelectedListener {
    private final Executor mExecutorFling = Executors.newSingleThreadExecutor();
    private final Executor mExecutorFling2 = Executors.newSingleThreadExecutor();
    private final Executor mExecutorGit = Executors.newSingleThreadExecutor();
    private final Aurion mAurion = new Aurion();
    private GithubService mGithubService;
    private CoursesFragment mCoursesFragment = new CoursesFragment();
    private CoursesFragment mOtherCoursesFragment = new CoursesFragment();
    private boolean isOtherFragment = false;
    private int animationType = 0;
    private Toast mToast;
    private GestureDetectorCompat mDetector;
    private ScorpionDatabase db;
    private int weekIndex = getCurrentWeekIndex();

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public int clickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setSubtitle(PreferenceUtils.getName());

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.planning_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if(clickedItem == R.id.nav_grades) {
                    mExecutorFling.execute(() -> {
                        startActivity(getGradesIntent());
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

        NavigationView navigationView = findViewById(R.id.planning_navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);

        mGithubService = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GithubService.class);

        mExecutorFling.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), ScorpionDatabase.class, "Scorpion.db").build();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mOtherCoursesFragment, "planning").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mCoursesFragment, "planning").commit();

            if(!PreferenceUtils.getMustReset().equals(getString(R.string.must_reset_id))) {
                db.clearAllTables();
                weekIndex = getCurrentWeekIndex();
                refresh();
                PreferenceUtils.setMustReset(getString(R.string.must_reset_id));
            }
            else {
                List<CourseDetails> courses = getPlanning(weekIndex);
                displayWeek(courses);
            }
        });

        findViewById(R.id.toTheLeftButton).setOnClickListener(v -> toTheLeft());

        findViewById(R.id.toTheRightButton).setOnClickListener(v -> toTheRight());

        findViewById(R.id.todaybutton).setOnClickListener(v -> mExecutorFling2.execute(() -> {

            Executor toUse;
            if(isPlanningInDatabase(getCurrentWeekIndex())) toUse = mExecutorFling2;
            else toUse = mExecutorFling;

            toUse.execute(() -> {
                int todayWeek = getCurrentWeekIndex();
                animationType = Integer.compare(todayWeek, weekIndex);
                List<CourseDetails> courses = getPlanning(todayWeek);
                if(courses == null) {
                    return;
                }
                displayWeek(courses, animationType);
                weekIndex = todayWeek;
            });
        }));
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
                    runOnUiThread(() -> new AlertDialog.Builder(this)
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
                            .setNegativeButton("Maybe later", null).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isPlanningInDatabase(int index) {
        Planning planning = db.aurionPlanningDao().getPlanningById(index);

        return planning != null;
    }

    public List<CourseDetails> getPlanning(int index) {
        return getPlanning(index, false);
    }
    public List<CourseDetails> getPlanning(int index, boolean forceRequest) {
        tryRequestUpdate();

        List<CourseDetails> result;

        //////////////////// Look for planning in database ////////////////////
        if(!forceRequest) {
            Planning planning = db.aurionPlanningDao().getPlanningById(index);

            if (planning != null) {
                result = UtilsMethods.planningFromString(planning.toString());
                return result;
            }
        }

        //////////////////// Planning not found in database ////////////////////

        // Prepare the planning to be inserted in the database
        Planning toInsert = new Planning();

        runOnUiThread(() -> getCurrentCoursesFragment().setRefreshing(true));
        result = requestDetailsPlanning(index);
        runOnUiThread(() -> getCurrentCoursesFragment().setRefreshing(false));
        if(result == null) return null;

        toInsert.id = index;
        toInsert.planningString = UtilsMethods.planningToString(result);
        result = UtilsMethods.planningFromString(toInsert.planningString);
        
        // Save it into database
        if(db.aurionPlanningDao().getPlanningById(index) == null) {
            db.aurionPlanningDao().insertPlanning(toInsert);
        }
        else {
            db.aurionPlanningDao().updatePlanning(toInsert);
        }

        return result;
    }


    public void displayWeek(List<CourseDetails> courses) {
        displayWeek(courses, -1);
    }
    public void displayWeek(List<CourseDetails> courses, int animation) {

        if(animation == 0) {
            runOnUiThread(() -> getCurrentCoursesFragment().onCoursesRetrieved(courses));
        }
        else {
            showCurrentCoursesFragment(courses, animation);
        }

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

    public List<CourseDetails> requestDetailsPlanning(int index) {
        // Get Planning from Aurion
        List<CourseDetails> p = mAurion.getPlanning(index);

        // If there is an error, reconnect then try again
        if(p == null && connect()) p = mAurion.getPlanning(index);

        if(p == null) {
            runOnUiThread(() -> showToast(this, "Connection error", Toast.LENGTH_LONG));
        }

        return p;
    }

    public void showDetails(Long id) {
        mExecutorFling.execute(() -> {
            List<CourseDetails> courses = getPlanning(weekIndex);
            CourseDetails tmpDetails = null;

            if(courses == null) return;

            for (CourseDetails c : courses) {
                if (c.id.equals(id)) {
                    tmpDetails = c;
                    break;
                }
            }

            if (tmpDetails == null) return;

            final CourseDetails details = new CourseDetails(tmpDetails);

            runOnUiThread(() -> {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom)).create();
                PersonListAdapter adapter;
                RecyclerView recyclerView;
                LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());


                View view = getLayoutInflater().inflate(R.layout.fragment_courses_details, null);

                if (details.course.isEmpty()) details.course = "Aucun enregistrement";
                if (details.status.isEmpty()) details.status = "Aucun enregistrement";
                if (details.topic.isEmpty()) details.topic = "Aucun enregistrement";
                if (details.type.isEmpty()) details.type = "Aucun enregistrement";
                if (details.description.isEmpty()) details.description = "Aucun enregistrement";
                if (details.examStatus.isEmpty()) details.examStatus = "Aucun enregistrement";
                if (details.room.isEmpty()) details.room = "Aucun enregistrement";

                ((TextView) view.findViewById(R.id.coursesDetailsTypeTextView)).setText(details.type);
                ((TextView) view.findViewById(R.id.coursesDetailsDescriptionTextView)).setText(details.description);
                ((TextView) view.findViewById(R.id.coursesDetailsIsExamTextView)).setText(details.examStatus);
                ((TextView) view.findViewById(R.id.coursesDetailsCourseTextView)).setText(details.course);

                // Make description text view scrollable
                ((TextView) view.findViewById(R.id.coursesDetailsDescriptionTextView)).setMovementMethod(new ScrollingMovementMethod());

                // TEACHERS
                if(details.teachers.size() <= 1){
                    // don't show teachers list if it is redundant
                    view.findViewById(R.id.textView32).setVisibility(View.GONE);
                    view.findViewById(R.id.teachersScrollView).setVisibility(View.GONE);
                }
                else{
                    recyclerView = (RecyclerView) view.findViewById(R.id.coursesDetailsTeachersRecyclerView);

                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    adapter = new PersonListAdapter(details.teachers);
                    recyclerView.setAdapter(adapter);
                }

                // STUDENTS
                ((TextView) view.findViewById(R.id.textView33)).setText(MessageFormat.format("{0} ({1})", getString(R.string.students), String.valueOf(details.students.size())));
                recyclerView = (RecyclerView) view.findViewById(R.id.coursesDetailsStudentsRecyclerView);

                layoutManager = new LinearLayoutManager(dialog.getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                // Add empty student to end of list for readability
                details.students.add(new Person(" ", " "));

                adapter = new PersonListAdapter(details.students);
                recyclerView.setAdapter(adapter);

                dialog.setView(view);
                view.findViewById(R.id.detailsExitActionButton).setOnClickListener(v -> {
                    dialog.dismiss();
                });
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            });
        });
    }

    public void refresh() {
        //Refresh the planning
        mExecutorFling.execute(() -> {
            Log.d("REFRESH", "Refreshing...");
            int index = weekIndex;
            List<CourseDetails> courses = getPlanning(index, true);
            if (courses != null && index == weekIndex) {
                displayWeek(courses, 0);
            }
            Log.d("REFRESH", "Finish refreshing...");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.planning_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if( id == R.id.actionRefresh) {
            mExecutorFling.execute(this::refresh);
            return true;
        }
        else if( id == R.id.actionReset) {
            mExecutorFling.execute(() -> {
                db.clearAllTables();
                weekIndex = getCurrentWeekIndex();
                refresh();
            });
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
            mExecutorFling.execute(() -> {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liamabyss.github.io/Scorpion/"));
                startActivity(myIntent);
            });
        }
        else if(id == R.id.nav_feedback) {
            mExecutorFling.execute(() -> {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LiamAbyss/Scorpion/issues/new/choose"));
                startActivity(myIntent);
            });
        }
        else if(id == R.id.nav_mail) {
            mExecutorFling.execute(() -> {
                String[] email_address = new String[] {getString(R.string.scorpion_email_address)};
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.scorpion_email_address), null));
                intent.putExtra(Intent.EXTRA_EMAIL, email_address);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_question) + " : " + PreferenceUtils.getName());
                startActivity(Intent.createChooser(intent, "Send Email"));
            });
        }
        else if( id == R.id.nav_logout) {
            mExecutorFling.execute(() -> {
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
        // In an executor to wait for pending requests to finish
        mExecutorFling.execute(() -> runOnUiThread(super::onBackPressed));
    }

    public void showToast(Context context, int resId, int duration){
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, resId, duration);
        mToast.show();
    }

    public void showToast(Context context, CharSequence text, int duration){
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

    public void toTheRight() {
        mExecutorFling2.execute(() -> {
            Executor toUse;
            if(isPlanningInDatabase(weekIndex + 1)) toUse = mExecutorFling2;
            else toUse = mExecutorFling;

            toUse.execute(() -> {
                animationType = 1;
                int index = weekIndex + 1;
                List<CourseDetails> courses = getPlanning(index);
                if(courses != null && index == weekIndex + 1) {
                    displayWeek(courses, animationType);
                    weekIndex = index;
                }
            /*mExecutorFling.execute(() -> {
                getPlanning(weekIndex + 1, false);
                //requestPlanning(weekIndex - 1, false, false);
            });*/
            });
        });
    }

    public void toTheLeft() {
        mExecutorFling2.execute(() -> {
            Executor toUse;
            if(isPlanningInDatabase(weekIndex - 1)) toUse = mExecutorFling2;
            else toUse = mExecutorFling;

            toUse.execute(() -> {
                animationType = -1;
                int index = weekIndex - 1;
                List<CourseDetails> courses = getPlanning(index);
                if(courses != null && index == weekIndex - 1) {
                    displayWeek(courses, animationType);
                    weekIndex = index;
                }
            /*mExecutorFling.execute(() -> {
                //requestPlanning(weekIndex + 1, false, false);
                getPlanning(weekIndex - 1, false);
            });*/
            });
        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // To the left
        if(Math.abs(velocityY) > 4000) return false;
        if(velocityX > 2000) {
            Log.d("FLING", "To the left !");
            toTheLeft();
        }
        else if(velocityX < -2000) {
            Log.d("FLING", "To the right !");
            toTheRight();
        }
        return true;
    }

    public CoursesFragment getCurrentCoursesFragment() {
        if(isOtherFragment) return mOtherCoursesFragment;
        else return mCoursesFragment;
    }

    public void resetCurrentCoursesFragment() {
        if(isOtherFragment) mOtherCoursesFragment = new CoursesFragment();
        else mCoursesFragment = new CoursesFragment();
    }

    public void toggleCoursesFragment() {
        isOtherFragment = !isOtherFragment;
    }

    public void showCurrentCoursesFragment(List<CourseDetails> courses, int animation) {
        resetCurrentCoursesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(animation > 0) {
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_from_right);
        }
        else if(animation < 0) {
            transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
        }
        toggleCoursesFragment();
        transaction.replace(R.id.container, getCurrentCoursesFragment(), "planning")
                .commit();
        runOnUiThread(() -> getCurrentCoursesFragment().onCoursesRetrieved(courses));
    }

    private int getCurrentWeekIndex() {
        Calendar c = Calendar.getInstance();
        if(c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            return c.get(Calendar.WEEK_OF_YEAR);
        else
            return c.get(Calendar.WEEK_OF_YEAR) + 1;
    }

    private Intent getGradesIntent()
    {
        return new Intent(this, GradesActivity.class);
    }

}