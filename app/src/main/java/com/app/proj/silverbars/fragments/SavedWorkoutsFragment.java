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

import org.lucasr.twowayview.widget.TwoWayView;


public class SavedWorkoutsFragment extends Fragment {


    private TwoWayView mLocalWorkouts;
    private  LinearLayout mEmpyStateSavedWorkout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_workouts, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        

        mLocalWorkouts = (TwoWayView) view.findViewById(R.id.recycler_saved_workouts);
        mEmpyStateSavedWorkout = (LinearLayout) view.findViewById(R.id.empty_state_saved_workouts);

        Button explore = (Button) view.findViewById(R.id.explore);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        
        


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }



}
