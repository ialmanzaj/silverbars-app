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
import com.app.app.silverbarsapp.fragments.ProgressFragment;
import com.app.app.silverbarsapp.fragments.MyWorkoutsFragment;
import com.app.app.silverbarsapp.fragments.ProfileFragment;
import com.app.app.silverbarsapp.utils.OnItemSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,OnItemSelectedListener {

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
                currentFragment = new MyWorkoutsFragment();
                createWorkoutbuttonOn();
                break;
            case R.id.my_workouts:
                currentFragment = new ProgressFragment();
                createWorkoutbuttonff();
                break;
            case R.id.my_progression:
                currentFragment = new ProfileFragment();
                createWorkoutbuttonff();
                break;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, currentFragment);
        transaction.commit();

        return true;
    }

    @OnClick(R.id.fab)
    public void createButton(){
        startActivity(new Intent(this,CreateWorkoutActivity.class));
    }

    private void createWorkoutbuttonOn(){
        mButtonCreateWorkout.setVisibility(View.VISIBLE);
    }

    private void createWorkoutbuttonff(){
        mButtonCreateWorkout.setVisibility(View.GONE);
    }

    @Override
    public void onChangeNavigation(int position) {
        selectItem(position);
    }


}
