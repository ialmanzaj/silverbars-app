package com.app.app.silverbarsapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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
import butterknife.OnClick;

public class WorkoutsDoneActivity extends BaseActivityExtended implements WorkoutsDoneView {

    private static final String TAG = WorkoutsDoneActivity.class.getSimpleName();

    @Inject
    WorkoutsDonePresenter mWorkoutsDonePresenter;


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.list)RecyclerView mWorkoutsDoneList;

    WorkoutsDoneAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_workouts_done;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mWorkoutsDonePresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerWorkoutsDoneComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .workoutsDoneModule(new WorkoutsDoneModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        setupAdapter();
        mWorkoutsDonePresenter.getWorkoutsDone();
    }

    private void setupAdapter(){
        //list settings
        mWorkoutsDoneList.setLayoutManager(new LinearLayoutManager(this));
        mWorkoutsDoneList.setNestedScrollingEnabled(false);
        mWorkoutsDoneList.setHasFixedSize(false);
        adapter = new WorkoutsDoneAdapter();
        mWorkoutsDoneList.setAdapter(adapter);
    }

    public void setupToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.fragment_profile_workout_done));
        }
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mWorkoutsDonePresenter.getWorkoutsDone();
    }

    @Override
    public void onWorkoutsDone(List<WorkoutDone> workouts) {
        onLoadingViewOff();
        Collections.reverse(workouts);
        adapter.set(workouts);
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
        onErrorViewOn();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
