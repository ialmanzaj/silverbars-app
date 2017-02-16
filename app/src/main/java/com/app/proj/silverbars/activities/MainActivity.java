package com.app.proj.silverbars.activities;


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

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.fragments.MainWorkoutsFragment;
import com.app.proj.silverbars.fragments.MyWorkoutsFragment;
import com.app.proj.silverbars.fragments.ProfileFragment;

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



       /* Button_filter.setClickable(true);
        Button_filter.setOnClickListener(v ->

                new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.filtertitle)
                .items(R.array.filter_items_text)
                .itemsCallbackSingleChoice(-1, (dialog, view, indice, text) -> {
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    if (indice != -1) {

                        //Log.v(TAG, String.valueOf(indice));
                        String[] Muscles = getResources().getStringArray(R.array.filter_items);

                        //Log.v(TAG, Muscles[indice]);
                        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);


                       *//* if (currentFragment instanceof MainWorkoutsFragment) {
                            ((MainWorkoutsFragment) currentFragment).filterWorkouts(Muscles[indice]);
                        }*//*


                    }
                    return true;
                })
                .positiveText(R.string.choose)
                .positiveColor(getResources().getColor(R.color.white))
                .show());
*/


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
                extras.putString("Muscle",muscle);
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
