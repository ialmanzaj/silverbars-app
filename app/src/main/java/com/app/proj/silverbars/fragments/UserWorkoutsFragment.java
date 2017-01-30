package com.app.proj.silverbars.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.viewsets.UserWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends Fragment implements UserWorkoutsView{

    private static final String TAG = UserWorkoutsFragment.class.getSimpleName();

    @BindView(R.id.list_my_workouts) TwoWayView mMyWorkoutView;
    @BindView(R.id.empty_view) LinearLayout EmpyStateMyWorkouts;
    @BindView(R.id.create) Button mCreateWorkoutButton;


    //UserWorkoutsPresenter mUserWorkoutsPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_user_workouts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //mCreateWorkoutButton.setOnClickListener(view -> startActivity(new Intent(getActivity(),CreateWorkoutActivity.class)));
    }


}
