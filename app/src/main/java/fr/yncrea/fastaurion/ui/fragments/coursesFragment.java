package fr.yncrea.fastaurion.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.fastaurion.MainActivity;
import fr.yncrea.fastaurion.R;
import fr.yncrea.fastaurion.adapters.CoursesAdapter;
import fr.yncrea.fastaurion.utils.Course;
import fr.yncrea.fastaurion.utils.PreferenceUtils;

public class coursesFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MainActivity parent;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public coursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_courses_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.coursesListview);

        mListView.setOnItemClickListener(this);
        mListView.setOnTouchListener((v, event) -> {
            mListView.onTouchEvent(event);
            if(event != null)
                parent.onTouchEvent(event);
            return true;
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = getView().findViewById(R.id.refreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        parent = (MainActivity)getActivity();
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

    @Override
    public void onRefresh() {
        mExecutor.execute(() -> {
            parent.refresh();
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    public void setRefreshing(boolean bool) {
        mSwipeRefreshLayout.setRefreshing(bool);
    }
}
