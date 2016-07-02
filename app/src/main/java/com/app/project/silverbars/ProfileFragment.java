package com.app.project.silverbars;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    View rootView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TabHost tabHost3 = (TabHost) rootView.findViewById(R.id.tabHost3);
        tabHost3.setup();

        TabHost.TabSpec data1 = tabHost3.newTabSpec("History");
        TabHost.TabSpec data2 = tabHost3.newTabSpec("Progress");

        data1.setIndicator("History");
        data1.setContent(R.id.HistoryLayout);

        data2.setIndicator("Progress");
        data2.setContent(R.id.ProgressLayout);

        tabHost3.addTab(data1);
        tabHost3.addTab(data2);

        List<ProgressInfo> items = new ArrayList<>();

        items.add(new ProgressInfo("Chest",40));
        items.add(new ProgressInfo("Back",30));
        items.add(new ProgressInfo("Core",66));
        items.add(new ProgressInfo("Shoulders",13));
        items.add(new ProgressInfo("Chest and Back",76));

        // Obtener el Recycler
        recycler = (RecyclerView) rootView.findViewById(R.id.RecyclerProgress);
        if (recycler != null) {
            recycler.setHasFixedSize(true);
        }

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ProgressAdapter(items,getActivity().getApplicationContext());
        recycler.setAdapter(adapter);

        return rootView;
    }


}
