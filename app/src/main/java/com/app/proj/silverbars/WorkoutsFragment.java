package com.app.proj.silverbars;

//import android.support.design.widget.TabLayout;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutsFragment extends Fragment {

    private static final String TAG = "WorkoutsFragment";
    View rootView, mainView;
    TabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_my_workouts, container, false);

        tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec myworkouts = tabHost.newTabSpec(getResources().getString(R.string.tab_my_workouts));
        TabHost.TabSpec savedworkout = tabHost.newTabSpec(getResources().getString(R.string.tab_saved));

        myworkouts.setIndicator(getResources().getString(R.string.tab_my_workouts));
        myworkouts.setContent(R.id.my_workouts_layout);

        savedworkout.setIndicator(getResources().getString(R.string.tab_saved));
        savedworkout.setContent(R.id.saved_workouts_layout);

        tabHost.addTab(savedworkout);
        tabHost.addTab(myworkouts);



        TwoWayView local_workouts = (TwoWayView) rootView.findViewById(R.id.recycler_saved_workouts);
        TwoWayView my_workouts = (TwoWayView) rootView.findViewById(R.id.recycler_my_workouts);

        LinearLayout EmpyStateSavedWorkout = (LinearLayout) rootView.findViewById(R.id.empty_state_saved_workouts);
        LinearLayout EmpyStateMyWorkouts = (LinearLayout) rootView.findViewById(R.id.empty_state_my_workouts);


        Button explore = (Button) rootView.findViewById(R.id.explore);
        Button create = (Button) rootView.findViewById(R.id.create_workout);


        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateWorkoutActivity.class));
            }
        });

        MySQLiteHelper database = new MySQLiteHelper(getContext());

        if (database.getWorkouts(1) != null){
            local_workouts.setVisibility(View.VISIBLE);
            List<JsonWorkout> workouts = new ArrayList<>();
            JsonWorkout[] ParsedWorkouts = database.getWorkouts(1);
            Collections.addAll(workouts, ParsedWorkouts);


            local_workouts.setAdapter(new savedWorkoutAdapter(getActivity(),workouts,false));

        }else {
            EmpyStateSavedWorkout.setVisibility(View.VISIBLE);
        }


       if (database.getUserWorkouts(1) != null){
            my_workouts.setVisibility(View.VISIBLE);
            List<JsonWorkout> my_workouts_list= new ArrayList<>();

            JsonWorkout[] MyWorkouts = database.getUserWorkouts(1);
            Collections.addAll(my_workouts_list, MyWorkouts);
            Log.v(TAG,"my workout size: "+my_workouts_list);


            my_workouts.setAdapter(new savedWorkoutAdapter(getActivity(),my_workouts_list,true));

        }else {
            EmpyStateMyWorkouts.setVisibility(View.VISIBLE);
        }

        return rootView;
    }







}
