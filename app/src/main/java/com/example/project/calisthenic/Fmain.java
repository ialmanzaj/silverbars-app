package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Fmain extends Fragment {

    View rootView;
    Button songs;
    private String email,name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fmain, container, false);

        Intent intent = this.getActivity().getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");

        songs = (Button) rootView.findViewById(R.id.songs);

        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),Workout.class);
                startActivity(i);
            }
        });

        return rootView;
    }

}
