package fr.yncrea.fastaurion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.yncrea.fastaurion.FastAurionApplication;
import fr.yncrea.fastaurion.R;
import fr.yncrea.fastaurion.utils.Course;
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder>{

    private List<Course> mCourses;

    public CoursesAdapter(List<Course> courses) {
        mCourses = courses;
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(FastAurionApplication.getContext()).inflate(R.layout.course_listitem, parent, false);
        return new CoursesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CoursesViewHolder holder, int position) {
        if(holder.getTitleTextView() != null){
            holder.getTitleTextView().setText(mCourses.get(position).getTitle());
            holder.getCourseTypeTextView().setText(mCourses.get(position).getCourseType());
        }
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public static class CoursesViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final TextView courseType;

        public CoursesViewHolder(final View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.courseTitleTextView);
            this.courseType = (TextView) view.findViewById(R.id.courseTypeTextView);
        }

        public TextView getTitleTextView() {
            return title;
        }

        public TextView getCourseTypeTextView() {
            return courseType;
        }
    }
}

