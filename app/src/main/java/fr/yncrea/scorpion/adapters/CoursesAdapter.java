package fr.yncrea.scorpion.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.yncrea.scorpion.GradesActivity;
import fr.yncrea.scorpion.MainActivity;
import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ui.fragments.CoursesFragment;
import fr.yncrea.scorpion.utils.Course;
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder>{

    private List<Course> mCourses;
    private int mPosition;
    private MainActivity parent;

    public CoursesAdapter(List<Course> courses) {
        mCourses = courses;

        for(Course c : mCourses) {
            if(c.getTitle().equals("\u200B") || c.getDate().equals("\u200b")) {
                mPosition++;
                continue;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.FRANCE);
            try {
                Date date = dateFormat.parse(c.getStart());
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
                String first = dateFormat.format(Calendar.getInstance().getTime());
                String second = dateFormat.format(date);
                if(TextUtils.equals(first, second)) {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mPosition++;
        }
        if(mPosition >= courses.size()) mPosition = 0;
    }

    public int getPosition() {
        return mPosition;
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.course_listitem, parent, false);
        return new CoursesViewHolder(view);
    }

    public void addContext(Context context) {
        parent = (MainActivity) context;
    }

    @Override
    public void onBindViewHolder(CoursesViewHolder holder, int position) {
        if(mCourses.get(position) != null){

            /*ConstraintLayout clickableConstraintLayout = (ConstraintLayout) holder.mView.findViewById(R.id.clickable_constraintLayout);
            clickableConstraintLayout.setOnClickListener((View v) -> {
                parent.requestDetailsPlanning(mCourses.get(position).id);
            });*/

            if(mCourses.get(position).getTitle().equals("\u200B")) {
                holder.day.setVisibility(View.GONE);
                holder.holiday.setVisibility(View.VISIBLE);
                holder.courseType.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);
                holder.dayConstraintLayout.setVisibility(View.GONE);
            }
            else if(mCourses.get(position).getDate().equals("\u200B")) {
                holder.day.setVisibility(View.GONE);
                holder.holiday.setVisibility(View.GONE);
                holder.courseType.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.VISIBLE);
                holder.dayConstraintLayout.setVisibility(View.GONE);
            }
            else {
                holder.day.setText(mCourses.get(position).getDate().toUpperCase());
                holder.day.setVisibility(View.VISIBLE);
                holder.holiday.setVisibility(View.GONE);
                holder.courseType.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.VISIBLE);
                holder.dayConstraintLayout.setVisibility(View.VISIBLE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.FRANCE);
                try {
                    Date date = dateFormat.parse(mCourses.get(position).getStart());
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
                    String first = dateFormat.format(Calendar.getInstance().getTime());
                    String second = dateFormat.format(date);
                    if(TextUtils.equals(first, second)) {
                        holder.todayLayout1.setVisibility(View.VISIBLE);
                        holder.todayLayout2.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.todayLayout1.setVisibility(View.INVISIBLE);
                        holder.todayLayout2.setVisibility(View.INVISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.title.setText(mCourses.get(position).getTitle());
            holder.courseType.setText(mCourses.get(position).getCourseType());
        }
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public static class CoursesViewHolder extends RecyclerView.ViewHolder{

        public final TextView day;
        public final TextView title;
        public final TextView courseType;
        public final TextView holiday;
        public final ConstraintLayout dayConstraintLayout;
        public final FrameLayout todayLayout1;
        public final FrameLayout todayLayout2;
        public final View mView;

        public CoursesViewHolder(final View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.courseTitleTextView);
            courseType = (TextView) view.findViewById(R.id.courseTypeTextView);
            day = (TextView) view.findViewById(R.id.dayTextView);
            holiday = (TextView) view.findViewById(R.id.holidayTextView);
            dayConstraintLayout = (ConstraintLayout) view.findViewById(R.id.dayConstraintLayout);
            todayLayout1 = (FrameLayout) view.findViewById(R.id.todayLayout1);
            todayLayout2 = (FrameLayout) view.findViewById(R.id.todayLayout2);
        }

    }
}

