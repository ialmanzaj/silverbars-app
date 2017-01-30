package com.app.proj.silverbars.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;

import butterknife.BindView;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
   
    private RecyclerView list;
    
    private RecyclerView.Adapter adapter;


    @BindView(R.id.myTeam)RelativeLayout myTeam;
    @BindView(R.id.Progression)RelativeLayout Progression;
    @BindView(R.id.skillsProgression)RelativeLayout skillsProgression;
    @BindView(R.id.sharedWorkouts)RelativeLayout sharedWorkouts;
    @BindView(R.id.history)RelativeLayout history;

    @BindView(R.id.Profile_name) TextView mProfileName;
    @BindView(R.id.profile_image) ImageView profile_image;

    private Utilities utilities;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }




}
