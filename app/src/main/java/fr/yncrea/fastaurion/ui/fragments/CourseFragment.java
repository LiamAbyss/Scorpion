package fr.yncrea.fastaurion.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import fr.yncrea.fastaurion.R;


public class CourseFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_TITLE = "paramTitle";
    private static final String ARG_PARAM_START = "paramStart";
    private static final String ARG_PARAM_END = "paramEnd";
    private static final String ARG_PARAM_COURSETYPE = "paramCourseType";

    private String mTitle;
    private String mStart;
    private String mEnd;
    private String mCourseType;

    public CourseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param start Parameter 2.
     * @param end Parameter 3.
     * @param courseType Parameter 4.
     * @return A new instance of fragment TweetFragment.
     */

    public static CourseFragment newInstance(String title, String start, String end, String courseType) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_TITLE, title);
        args.putString(ARG_PARAM_START, start);
        args.putString(ARG_PARAM_END, end);
        args.putString(ARG_PARAM_COURSETYPE, courseType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_PARAM_TITLE);
            mStart = getArguments().getString(ARG_PARAM_START);
            mEnd = getArguments().getString(ARG_PARAM_END);
            mCourseType = getArguments().getString(ARG_PARAM_COURSETYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.course_listitem,container,false);
        ((TextView) view.findViewById(R.id.courseTitleTextView)).setText(mTitle);
        ((TextView) view.findViewById(R.id.courseStartTextView)).setText(mStart);
        ((TextView) view.findViewById(R.id.courseEndTextView)).setText(mEnd);
        ((TextView) view.findViewById(R.id.courseTypeTextView)).setText(mCourseType);

        return view;
    }
}