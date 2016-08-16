package com.app.proj.silverbars;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.facebook.Profile.getCurrentProfile;

public class ProfileFragment extends Fragment {

    View rootView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private RelativeLayout myTeam, Progression, skillsProgression, sharedWorkouts, history;
    private TextView profile_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_name = (TextView) rootView.findViewById(R.id.Profile_name);

        String name = getCurrentProfile().getFirstName()+" "+getCurrentProfile().getLastName();
        profile_name.setText(name);

        float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (7*scale + 0.5f);
        myTeam = (RelativeLayout) rootView.findViewById(R.id.myTeam);
        myTeam.setClickable(true);
        myTeam.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        myTeam.setBackgroundColor(getResources().getColor(R.color.onTouch));
                        myTeam.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    case MotionEvent.ACTION_UP:
                        myTeam.setBackground(getResources().getDrawable(R.drawable.custom_border));
                        myTeam.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        Progression = (RelativeLayout) rootView.findViewById(R.id.Progression);
        Progression.setClickable(true);
        Progression.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Progression.setBackgroundColor(getResources().getColor(R.color.onTouch,null));
                        }else {
                            Progression.setBackgroundColor(getResources().getColor(R.color.onTouch));
                        }
                        Progression.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Progression.setBackground(getResources().getDrawable(R.drawable.custom_border,null));
                        }else {
                            Progression.setBackground(getResources().getDrawable(R.drawable.custom_border));
                        }
                        Progression.setPadding(0,dpAsPixels,0,dpAsPixels);

                        startActivity(new Intent(getActivity(),ProgressionActivity.class));
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        skillsProgression = (RelativeLayout) rootView.findViewById(R.id.skillsProgression);
        skillsProgression.setClickable(true);
        skillsProgression.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        skillsProgression.setBackgroundColor(getResources().getColor(R.color.onTouch));
                        skillsProgression.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    case MotionEvent.ACTION_UP:
                        skillsProgression.setBackground(getResources().getDrawable(R.drawable.custom_border));
                        skillsProgression.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        sharedWorkouts = (RelativeLayout) rootView.findViewById(R.id.sharedWorkouts);
        sharedWorkouts.setClickable(true);
        sharedWorkouts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sharedWorkouts.setBackgroundColor(getResources().getColor(R.color.onTouch));
                        sharedWorkouts.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    case MotionEvent.ACTION_UP:
                        sharedWorkouts.setBackground(getResources().getDrawable(R.drawable.custom_border));
                        sharedWorkouts.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        history = (RelativeLayout) rootView.findViewById(R.id.history);
        history.setClickable(true);
        history.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        history.setBackgroundColor(getResources().getColor(R.color.onTouch));
                        history.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    case MotionEvent.ACTION_UP:
                        history.setBackground(getResources().getDrawable(R.drawable.custom_border));
                        history.setPadding(0,dpAsPixels,0,dpAsPixels);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        return rootView;
    }

}
