package fr.yncrea.scorpion.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.scorpion.GradesActivity;
import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.adapters.GradesAdapter;
import fr.yncrea.scorpion.model.Grade;

public class GradesFragment  extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private GradesActivity parent;

    public GradesFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_grades_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.gradesRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ScorpionApplication.getContext()));
        mRecyclerView.setOnTouchListener((v, event) -> {
            mRecyclerView.onTouchEvent(event);
            if(event != null)
                parent.onTouchEvent(event);
            return true;
        });
        return rootView;
    }

    public void onGradesRetrieved(List<Grade> grades) {
        if(null != grades && mRecyclerView != null){
            final GradesAdapter adapter = new GradesAdapter(grades);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(ScorpionApplication.getContext()));
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = getView().findViewById(R.id.gradesRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        parent = (GradesActivity)getActivity();
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
