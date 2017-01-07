package com.app.proj.silverbars.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.activities.CreateWorkoutActivity;

import org.lucasr.twowayview.widget.TwoWayView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends Fragment {

    private static final String TAG = UserWorkoutsFragment.class.getSimpleName();

    TwoWayView mMyWorkoutView;
    LinearLayout EmpyStateMyWorkouts;
    Button mCreateWorkoutButton;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_workouts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mMyWorkoutView = (TwoWayView) view.findViewById(R.id.recycler_my_workouts);
        EmpyStateMyWorkouts = (LinearLayout) view.findViewById(R.id.empty_state_my_workouts);
        mCreateWorkoutButton = (Button) view.findViewById(R.id.create_workout);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        mCreateWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateWorkoutActivity.class));
            }
        });



    }
}
