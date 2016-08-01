package com.app.project.silverbars;

//import android.support.design.widget.TabLayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import org.lucasr.twowayview.widget.TwoWayView;

public class WorkoutsFragment extends Fragment {

    View rootView, mainView;
    SearchView mSearchView;
    TabWidget tabs;
    TabHost tabHost;



    public static JsonWorkout[] ParsedWorkouts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_my_workouts, container, false);

        tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec myworkouts = tabHost.newTabSpec(getResources().getString(R.string.tab_my_workouts));
        TabHost.TabSpec savedworkout = tabHost.newTabSpec(getResources().getString(R.string.tab_saved));

        myworkouts.setIndicator(getResources().getString(R.string.tab_my_workouts));
        myworkouts.setContent(R.id.tab1);

        savedworkout.setIndicator(getResources().getString(R.string.tab_saved));
        savedworkout.setContent(R.id.tab2);

        tabHost.addTab(savedworkout);
        tabHost.addTab(myworkouts);

        TwoWayView recyclerView = (TwoWayView) rootView.findViewById(R.id.list);
        LinearLayout EmpyState = (LinearLayout) rootView.findViewById(R.id.empty_state);

        MySQLiteHelper database = new MySQLiteHelper(getContext());

        if (database.getWorkouts(1) != null){

            ParsedWorkouts = database.getWorkouts(1);
            recyclerView.removeAllViews();
            recyclerView.removeAllViewsInLayout();
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(new savedWorkoutAdapter(getActivity()));

        }else {
            EmpyState.setVisibility(View.VISIBLE);
        }
        //Log.v("Saved Workouts",String.valueOf(database.getWorkouts(1).length));

        return rootView;
    }

}
