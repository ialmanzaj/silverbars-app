package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.lucasr.twowayview.widget.TwoWayView;

public class MainFragment extends Fragment {

    private String email;
    private String name;

    private TwoWayView recyclerView;
    private Button songs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fmain, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = this.getActivity().getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");

        recyclerView = (TwoWayView) getView().findViewById(R.id.list);
        recyclerView.setAdapter(new WorkoutAdapter(getActivity()));


    }
}
