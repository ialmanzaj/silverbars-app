package com.app.app.silverbarsapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerWorkoutsDoneComponent;
import com.app.app.silverbarsapp.modules.WorkoutsDoneModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkoutsDonePresenter;
import com.app.app.silverbarsapp.viewsets.WorkoutsDoneView;

import javax.inject.Inject;

public class WorkoutsDoneActivity extends BaseActivity implements WorkoutsDoneView {

    @Inject
    WorkoutsDonePresenter mWorkoutsDonePresenter;


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

    }




}
