package fr.yncrea.fastaurion.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import fr.yncrea.fastaurion.FastAurionApplication;
import fr.yncrea.fastaurion.R;
import fr.yncrea.fastaurion.utils.Course;
public class CoursesAdapter extends BaseAdapter{


    private List<Course> mCourses;
    private final LayoutInflater m_inflater;


    public CoursesAdapter(List<Course> mCourses) {
        this.mCourses = mCourses;
        this.m_inflater = LayoutInflater.from(FastAurionApplication.getContext());
    }

    @Override
    public int getCount() {
        return null != mCourses ? mCourses.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null != mCourses ? mCourses.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(null == convertView) {
            convertView =  m_inflater.inflate(R.layout.course_listitem, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Course course = (Course) getItem(position);
        Log.d("TAG", course.title);
        holder.title.setText(course.title);
        holder.start.setText(course.start);
        holder.end.setText(course.end);
        holder.course_type.setText(course.course_type);

        return convertView;
    }



    private class ViewHolder{

        public TextView title;
        public TextView start;
        public TextView end;
        public TextView course_type;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.courseTitleTextView);
            start = (TextView) view.findViewById(R.id.courseStartTextView);
            end = (TextView) view.findViewById(R.id.courseEndTextView);
            course_type = (TextView) view.findViewById(R.id.courseTypeTextView);
        }
    }
}

