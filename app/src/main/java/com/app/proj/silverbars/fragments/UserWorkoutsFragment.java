package com.app.proj.silverbars.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.activities.CreateWorkoutActivity;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.UserWorkoutsPresenter;
import com.app.proj.silverbars.viewsets.UserWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends BaseFragment implements UserWorkoutsView{

    private static final String TAG = UserWorkoutsFragment.class.getSimpleName();

    @BindView(R.id.list_my_workouts) TwoWayView mMyWorkoutView;
    @BindView(R.id.empty_view) LinearLayout EmpyStateMyWorkouts;
    @BindView(R.id.create) Button mCreateWorkoutButton;


    UserWorkoutsPresenter mUserWorkoutsPresenter;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_user_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mUserWorkoutsPresenter;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mCreateWorkoutButton.setOnClickListener(view -> startActivity(new Intent(getActivity(),CreateWorkoutActivity.class)));
    }


}
