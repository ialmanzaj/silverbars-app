package com.app.project.silverbars;

//import android.support.design.widget.TabLayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import org.lucasr.twowayview.widget.TwoWayView;

public class WorkoutsFragment extends Fragment {

    View rootView, mainView;
    SearchView mSearchView;
    TabWidget tabs;
    TabHost tabHost;
    TwoWayView recyclerView;
    RelativeLayout noConnection;
    SwipeRefreshLayout swipeContainer;
    public static JsonWorkout[] ParsedWorkouts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_my_workouts, container, false);

        tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec(getResources().getString(R.string.tab_my_workouts));
        TabHost.TabSpec spec2 = tabHost.newTabSpec(getResources().getString(R.string.tab_saved));

        spec1.setIndicator(getResources().getString(R.string.tab_my_workouts));
        spec1.setContent(R.id.tab1);

        spec2.setIndicator(getResources().getString(R.string.tab_saved));
        spec2.setContent(R.id.tab2);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        recyclerView = (TwoWayView) rootView.findViewById(R.id.list);
        noConnection = (RelativeLayout) rootView.findViewById(R.id.noConnection);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        MySQLiteHelper database = new MySQLiteHelper(getContext());
        ParsedWorkouts = database.getWorkouts(1);
        recyclerView.removeAllViews();
        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(new savedWorkoutAdapter(getActivity()));
        swipeContainer.setRefreshing(false);
        Log.v("Saved Workouts",String.valueOf(database.getWorkouts(1).length));
        return rootView;
    }
}
