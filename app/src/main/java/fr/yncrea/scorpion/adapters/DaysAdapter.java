package fr.yncrea.scorpion.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import fr.yncrea.scorpion.MainActivity;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.model.CourseDetails;
import fr.yncrea.scorpion.utils.ThemeManager;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {
    private final List<String> daysList;
    private final List<CourseDetails> mCourses;
    private List<List<CourseDetails>> daysCourses;
    private int mPosition;
    private MainActivity parent;

    public DaysAdapter(List<CourseDetails> courses){
        mCourses = courses;
        daysList = extractDaysFromCourses();
    }

    public void addContext(Context context) {
        parent = (MainActivity) context;
    }

    public int getPosition() {
        return mPosition;
    }

    public List<String> extractDaysFromCourses(){
        List<String> newDaysList = new ArrayList<>();
        daysCourses = new ArrayList<>();
        List<CourseDetails> dayCourses = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE);
        Date startDate;
        try {
            startDate = df.parse(mCourses.get(0).longDate);
            Calendar calendar = GregorianCalendar.getInstance(Locale.FRANCE);
            calendar.setTime(startDate);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            for(int i = 1; i<=6; i++){

                String first = df.format(Calendar.getInstance().getTime());
                String second = df.format(calendar.getTime());
                if(first.equals(second)) {
                    mPosition = i - 1;
                }

                newDaysList.add(df.format(calendar.getTime()));
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + i);

                for(CourseDetails course : mCourses){
                    if(df.parse(course.longDate).before(calendar.getTime())){
                        dayCourses.add(course);
                    }
                }
                daysCourses.add(dayCourses);
                for (CourseDetails course : dayCourses){
                    mCourses.remove(course);
                }
                dayCourses = new ArrayList<>();
            }
        } catch (ParseException e) {
            newDaysList.add("");
            daysCourses.add(mCourses);
            e.printStackTrace();
        }
        return newDaysList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.day_listitem, parent, false);
        return new DayViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        if(daysList.get(position) != null){
            holder.day.setText(daysList.get(position));
        }
        final CoursesAdapter adapter = new CoursesAdapter(daysCourses.get(position));
        adapter.addContext(parent);
        holder.coursesRecyclerView.setLayoutManager(new LinearLayoutManager(ScorpionApplication.getContext()));
        holder.coursesRecyclerView.setHasFixedSize(false);
        holder.coursesRecyclerView.setAdapter(adapter);
        holder.coursesRecyclerView.setOnTouchListener((v, event) -> {
            holder.coursesRecyclerView.onTouchEvent(event);
            if(event != null)
                parent.onTouchEvent(event);
            return true;
        });

        if(holder.day.getText().length() == 0) {
            holder.day.setVisibility(View.GONE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE);
        try {
            Date date = dateFormat.parse(daysList.get(position));
            dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE);
            String first = dateFormat.format(Calendar.getInstance().getTime());
            String second = dateFormat.format(date);
            if(TextUtils.equals(first, second)) {
                holder.todayLayout1.setVisibility(View.VISIBLE);
                holder.todayLayout2.setVisibility(View.VISIBLE);
                holder.todayLayout1.setBackgroundColor(parent.getColor(ThemeManager.getColorId(R.attr.colorPrimary)));
                holder.todayLayout2.setBackgroundColor(parent.getColor(ThemeManager.getColorId(R.attr.colorPrimary)));
            }
            else {
                holder.todayLayout1.setVisibility(View.INVISIBLE);
                holder.todayLayout2.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    protected static class DayViewHolder extends RecyclerView.ViewHolder {
        public final TextView day;
        public final RecyclerView coursesRecyclerView;
        public FrameLayout todayLayout1;
        public FrameLayout todayLayout2;

        public DayViewHolder(View view) {
            super(view);
            day = (TextView) view.findViewById(R.id.dayTextView);
            coursesRecyclerView = (RecyclerView) view.findViewById(R.id.courses_recyclerview);
            todayLayout1 = (FrameLayout) view.findViewById(R.id.todayLayout1);
            todayLayout2 = (FrameLayout) view.findViewById(R.id.todayLayout2);
        }
    }
}
