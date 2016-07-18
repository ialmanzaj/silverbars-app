package com.app.project.silverbars;

//import android.support.design.widget.TabLayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabWidget;

public class WorkoutsFragment extends Fragment {

    View rootView, mainView;
    SearchView mSearchView;
    TabWidget tabs;
    TabHost tabHost;

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

        return rootView;
    }
}
