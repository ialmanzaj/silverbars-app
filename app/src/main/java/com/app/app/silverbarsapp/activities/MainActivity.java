package com.app.app.silverbarsapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.fragments.MainWorkoutsFragment;
import com.app.app.silverbarsapp.fragments.MyWorkoutsFragment;
import com.app.app.silverbarsapp.fragments.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.fab) FloatingActionButton mButtonCreateWorkout;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    private String muscle = "ALL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            selectItem(0);
        }

    }

    private void selectItem(int position){
        MenuItem menuItem = bottomNavigationView.getMenu().getItem(position);
        onNavigationItemSelected(menuItem);
    }

    public String getMuscle() {
        return muscle;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = null;

        Bundle extras = new Bundle();

        switch (item.getItemId()) {
            case R.id.home:
                currentFragment = new MainWorkoutsFragment();
                extras.putString("muscle",muscle);
                currentFragment.setArguments(extras);
                fabCreateWorkoutbuttonOn();
                break;
            case R.id.workout:
                currentFragment = new MyWorkoutsFragment();
                fabCreateWorkoutbuttonff();
                break;
            case R.id.profile:
                currentFragment = new ProfileFragment();
                fabCreateWorkoutbuttonff();
                break;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, currentFragment);
        transaction.commit();

        return true;
    }


    @OnClick(R.id.fab)
    public void create(){
        startActivity(new Intent(this,CreateWorkoutActivity.class));
    }


    private void fabCreateWorkoutbuttonOn(){
        mButtonCreateWorkout.setVisibility(View.VISIBLE);
    }

    private void fabCreateWorkoutbuttonff(){
        mButtonCreateWorkout.setVisibility(View.GONE);
    }

}
