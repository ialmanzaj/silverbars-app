package com.app.proj.silverbars.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.activities.CreateWorkoutActivity;
import com.app.proj.silverbars.presenters.BasePresenter;

import org.lucasr.twowayview.widget.TwoWayView;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends BaseFragment {

    private static final String TAG = UserWorkoutsFragment.class.getSimpleName();

    @BindView(R.id.recycler_my_workouts) TwoWayView mMyWorkoutView;
    @BindView(R.id.empty_state_my_workouts) LinearLayout EmpyStateMyWorkouts;
    @BindView(R.id.create_workout) Button mCreateWorkoutButton;


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_user_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mCreateWorkoutButton.setOnClickListener(view -> startActivity(new Intent(getActivity(),CreateWorkoutActivity.class)));


    }
}
