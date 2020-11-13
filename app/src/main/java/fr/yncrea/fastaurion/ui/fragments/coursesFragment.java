package fr.yncrea.fastaurion.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.List;

import fr.yncrea.fastaurion.R;
import fr.yncrea.fastaurion.adapters.CoursesAdapter;
import fr.yncrea.fastaurion.utils.Course;
import fr.yncrea.fastaurion.utils.PreferenceUtils;

public class coursesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;

    public coursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_courses_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.coursesListview);

        mListView.setOnItemClickListener(this);

        return rootView;
    }

    public void onCoursesRetrieved(List<Course> planning) {
        if(null != planning){
            final CoursesAdapter adapter = new CoursesAdapter(planning);
            mListView.setAdapter(adapter);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Do nothing for now, may show details of clicked course later
    }
}
