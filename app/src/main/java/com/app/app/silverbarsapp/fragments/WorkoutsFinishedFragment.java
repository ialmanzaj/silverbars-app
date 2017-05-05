package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.WorkoutsDoneAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkoutsDoneComponent;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.modules.WorkoutsDoneModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkoutsDonePresenter;
import com.app.app.silverbarsapp.viewsets.WorkoutsDoneView;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutsFinishedFragment extends BaseFragment implements WorkoutsDoneView {

    private static final String TAG = WorkoutsFinishedFragment.class.getSimpleName();

    @Inject
    WorkoutsDonePresenter mWorkoutsDonePresenter;

    @BindView(R.id.list)RecyclerView mWorkoutsDoneList;

    WorkoutsDoneAdapter adapter;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_workouts_finished;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mWorkoutsDonePresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerWorkoutsDoneComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .workoutsDoneModule(new WorkoutsDoneModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWorkoutsDonePresenter.getWorkoutsDone();
        //setupAdapter();
    }

    /*private void setupAdapter(){
        //mExercisesList settings
        mWorkoutsDoneList.setLayoutManager(new LinearLayoutManager(CONTEXT));
        mWorkoutsDoneList.setNestedScrollingEnabled(false);
        mWorkoutsDoneList.setHasFixedSize(false);
        adapter = new WorkoutsDoneAdapter();
        mWorkoutsDoneList.setAdapter(adapter);
    }*/

    @Override
    public void onWorkoutsDone(List<WorkoutDone> workouts) {
        Collections.reverse(workouts);
        //adapter.set(workouts);
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
    }


}
