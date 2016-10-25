package com.app.proj.silverbars;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SavedWorkoutsFragment extends Fragment {


    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootview =  inflater.inflate(R.layout.fragment_saved_workouts, container, false);

        TwoWayView local_workouts = (TwoWayView) rootview.findViewById(R.id.recycler_saved_workouts);
        LinearLayout EmpyStateSavedWorkout = (LinearLayout) rootview.findViewById(R.id.empty_state_saved_workouts);

        Button explore = (Button) rootview.findViewById(R.id.explore);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        MySQLiteHelper database = new MySQLiteHelper(getActivity());

        if (database.getLocalWorkouts() != null){
            local_workouts.setVisibility(View.VISIBLE);

            List<Workout> workouts = new ArrayList<>();
            Workout[] databaseWorkouts = database.getLocalWorkouts();
            Collections.addAll(workouts, databaseWorkouts);



            local_workouts.setAdapter(new SavedWorkoutsAdapter(getActivity(),workouts,false));
        }else {
            EmpyStateSavedWorkout.setVisibility(View.VISIBLE);
        }

        return rootview;
    }

}
