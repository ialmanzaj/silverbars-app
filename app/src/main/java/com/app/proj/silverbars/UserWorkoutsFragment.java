package com.app.proj.silverbars;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends Fragment {

    private static final String TAG = "UserWorkoutsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View roootview = inflater.inflate(R.layout.fragment_user_workouts, container, false);
        TwoWayView my_workouts = (TwoWayView) roootview.findViewById(R.id.recycler_my_workouts);

        LinearLayout EmpyStateMyWorkouts = (LinearLayout) roootview.findViewById(R.id.empty_state_my_workouts);

        Button create = (Button) roootview.findViewById(R.id.create_workout);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateWorkoutActivity.class));
            }
        });

        /*MySQLiteHelper database = new MySQLiteHelper(getContext());

        if (database.getUserWorkouts(1) != null){
            my_workouts.setVisibility(View.VISIBLE);
            List<Workout> my_workouts_list= new ArrayList<>();

            Workout[] MyWorkouts = database.getUserWorkouts(1);
            Collections.addAll(my_workouts_list, MyWorkouts);
            Log.v(TAG,"my workout size: "+my_workouts_list);


            my_workouts.setAdapter(new SavedWorkoutsAdapter(getActivity(),my_workouts_list,true));

        }else {
            EmpyStateMyWorkouts.setVisibility(View.VISIBLE);
        }*/

        return roootview;
    }




}
