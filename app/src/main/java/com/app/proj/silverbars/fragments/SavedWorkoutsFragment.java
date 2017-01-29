package com.app.proj.silverbars.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.presenters.BasePresenter;

import org.lucasr.twowayview.widget.TwoWayView;


public class SavedWorkoutsFragment extends BaseFragment {


    private TwoWayView mLocalWorkouts;
    private  LinearLayout mEmpyStateSavedWorkout;



    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_saved_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }



}
