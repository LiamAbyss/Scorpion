package fr.yncrea.scorpion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.utils.Course;
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder>{

    private List<Course> mCourses;

    public CoursesAdapter(List<Course> courses) {
        mCourses = courses;
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(ScorpionApplication.getContext()).inflate(R.layout.course_listitem, parent, false);
        return new CoursesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CoursesViewHolder holder, int position) {
        if(mCourses.get(position) != null){
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

        public CoursesViewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.courseTitleTextView);
            courseType = (TextView) view.findViewById(R.id.courseTypeTextView);
            day = (TextView) view.findViewById(R.id.dayTextView);
            holiday = (TextView) view.findViewById(R.id.holidayTextView);
            dayConstraintLayout = (ConstraintLayout) view.findViewById(R.id.dayConstraintLayout);
        }

    }
}

