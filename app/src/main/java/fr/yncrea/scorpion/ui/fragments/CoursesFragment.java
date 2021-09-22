package fr.yncrea.scorpion.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.MainActivity;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.adapters.DaysAdapter;

import androidx.recyclerview.widget.RecyclerView;

import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.model.CourseDetails;

public class CoursesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private MainActivity parent;
    private final int spanCount = 1;

    public CoursesFragment() {
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.coursesRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(ScorpionApplication.getContext(), spanCount));
        mRecyclerView.setOnTouchListener((v, event) -> {
            mRecyclerView.onTouchEvent(event);
            if(event != null)
                parent.onTouchEvent(event);
            return true;
        });
        return rootView;
    }

    public void onCoursesRetrieved(List<CourseDetails> planning) {
        if(null != planning){
            final DaysAdapter daysAdapter = new DaysAdapter(planning);
            daysAdapter.addContext(parent);

            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new GridLayoutManager(ScorpionApplication.getContext(), spanCount));
            mRecyclerView.setAdapter(daysAdapter);
            mRecyclerView.scrollToPosition(daysAdapter.getPosition());
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = getView().findViewById(R.id.refreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        parent = (MainActivity)getActivity();
    }

    @Override
    public void onRefresh() {
        mExecutor.execute(() -> {
            parent.refresh();
        });
    }

    public void setRefreshing(boolean bool) {
        if(mSwipeRefreshLayout == null) return;
        mSwipeRefreshLayout.setRefreshing(bool);
    }

    public boolean isRefreshing() {
        if(mSwipeRefreshLayout == null) return false;
        return mSwipeRefreshLayout.isRefreshing();
    }
}

