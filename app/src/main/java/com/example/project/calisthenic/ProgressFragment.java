package com.example.project.calisthenic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment {

    View rootView;

    TabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_progress, container, false);
        // Inflate the layout for this fragment

        tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        tabHost.setup();



        TabHost.TabSpec spec1 = tabHost.newTabSpec("Today");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("This Week");
        TabHost.TabSpec spec3 = tabHost.newTabSpec("This Month");

        spec1.setIndicator("Today");
        spec1.setContent(R.id.tab1);

        spec2.setIndicator("This Week");
        spec2.setContent(R.id.tab2);

        spec3.setIndicator("This Month");
        spec3.setContent(R.id.tab3);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        return rootView;

    }

}
