package fr.yncrea.scorpion.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.yncrea.scorpion.MainActivity;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.model.CourseDetails;

public class CoursesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<CourseDetails> mCourses;
    private int mPosition;
    private MainActivity parent;

    public CoursesAdapter(List<CourseDetails> courses) {
        mCourses = courses;

        for(CourseDetails c : mCourses) {
            if(c.course.equals("") || c.dateStart.equals("")) {
                mPosition++;
                continue;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.FRANCE);
            try {
                Date date = dateFormat.parse(c.dateStart);
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
    public int getItemViewType(int mPosition) {
        if(mCourses.get(mPosition) != null) {

            CourseDetails currentCourse = mCourses.get(mPosition);
            if (currentCourse.course.equals("Aucun enregistrement") && currentCourse.dateStart.equals("Aucun enregistrement")) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.holiday_listitem, parent, false);
            return new HolidayViewHolder(view);
        }
        view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.course_listitem, parent, false);
        return new CoursesViewHolder(view);

    }

    public void addContext(Context context) {
        parent = (MainActivity) context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position) {

        if(mCourses.get(position) != null){

            CourseDetails currentCourse = mCourses.get(position);

            if(currentCourse.id == -1 || currentCourse.course.equals("Aucun enregistrement") && currentCourse.dateStart.equals("Aucun enregistrement")) {
                return;
            }

            CoursesViewHolder holder = (CoursesViewHolder)mHolder;
            holder.info.setOnClickListener((View v) -> {
                parent.showDetails(mCourses.get(position).id);
            });

            if(TextUtils.equals(currentCourse.course, "\u200B") || TextUtils.equals(currentCourse.course, "Aucun enregistrement")) {
                holder.title.setVisibility(View.INVISIBLE);
            }
            else {
                holder.title.setText(currentCourse.course);
            }

            if(TextUtils.equals(currentCourse.description, "\u200B") || currentCourse.description.isEmpty()) {
                holder.description.setVisibility(View.INVISIBLE);
            }
            else {
                if(holder.title.getVisibility() == View.INVISIBLE) {
                    holder.title.setVisibility(View.VISIBLE);
                    holder.description.setVisibility(View.INVISIBLE);
                    holder.title.setText(currentCourse.description);
                }
                else
                    holder.description.setText(currentCourse.description);
            }

            if(TextUtils.equals(currentCourse.room, "\u200B")) {
                holder.room.setVisibility(View.GONE);
            }
            else {
                holder.room.setText(currentCourse.room);
            }

            if(currentCourse.teachers.size() == 0 || TextUtils.equals(currentCourse.teachers.get(0).firstName, "\u200B")) {
                holder.teacher.setVisibility(View.GONE);
            }
            else {
                holder.teacher.setText(currentCourse.teachers.get(0).firstName + " " + currentCourse.teachers.get(0).lastName);
            }
            holder.timeStart.setText(currentCourse.timeStart);
            holder.timeEnd.setText(currentCourse.timeEnd);
        }
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public static class HolidayViewHolder extends RecyclerView.ViewHolder{
        public final TextView holiday;

        public HolidayViewHolder(final View view){
            super(view);
            holiday = (TextView) view.findViewById(R.id.holidayTextView);
        }
    }


    public static class CoursesViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final TextView description;
        public final TextView timeStart;
        public final TextView timeEnd;
        public final TextView room;
        public final TextView teacher;
        public final ConstraintLayout dayConstraintLayout;
        public final ConstraintLayout courseConstraintLayout;
        public final View mView;
        public final ImageView info;
        public final MaterialCardView card;

        public CoursesViewHolder(final View view)   {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.courseTitleTextView);
            description = (TextView) view.findViewById(R.id.courseDescriptionTextView);
            timeStart = (TextView) view.findViewById(R.id.courseTimeStartTextView);
            timeEnd = (TextView) view.findViewById(R.id.courseTimeEndTextView);
            room = (TextView) view.findViewById(R.id.courseRoomTextView);
            teacher = (TextView) view.findViewById(R.id.courseTeacherTextView);
            dayConstraintLayout = (ConstraintLayout) view.findViewById(R.id.dayConstraintLayout);
            courseConstraintLayout = (ConstraintLayout) view.findViewById(R.id.courseConstraintLayout);
            info = (ImageView) view.findViewById(R.id.courseInfoImageView);
            card = (MaterialCardView) view.findViewById(R.id.courseMaterialCard);
        }

    }
}

