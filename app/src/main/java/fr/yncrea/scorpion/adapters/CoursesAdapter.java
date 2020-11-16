package fr.yncrea.scorpion.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.utils.Course;
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder>{

    private List<Course> mCourses;
    private String[] days;

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
            Log.d("TAG", " " + position + " " + mCourses.get(position).getDay());
            if (position == 0 || days[position] != null ){
                holder.day.setText(mCourses.get(position).getDay().toUpperCase());
                days[position] = mCourses.get(position).getDay();
            }
            else{
                holder.day.setText("");
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

        public CoursesViewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.courseTitleTextView);
            courseType = (TextView) view.findViewById(R.id.courseTypeTextView);
            day = (TextView) view.findViewById(R.id.dayTextView);
        }

    }
}

