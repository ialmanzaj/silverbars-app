package com.app.app.silverbarsapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerMainComponent;
import com.app.app.silverbarsapp.database_models.FbProfile;
import com.app.app.silverbarsapp.fragments.MyWorkoutsFragment;
import com.app.app.silverbarsapp.fragments.ProfileFragment;
import com.app.app.silverbarsapp.fragments.ProgressFragment;
import com.app.app.silverbarsapp.modules.MainModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.MainPresenter;
import com.app.app.silverbarsapp.utils.OnItemSelectedListener;
import com.app.app.silverbarsapp.viewsets.MainView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainView,BottomNavigationView.OnNavigationItemSelectedListener,OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.fab) FloatingActionButton mButtonCreateWorkout;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    private String muscle = "ALL";

    public static  JSONObject USERDATA = null;

    @Override
    protected int getLayout() {
        return R.layout.activity_main_screen;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mMainPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerMainComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .mainModule(new MainModule(this))
                .build().inject(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mMainPresenter.getFbProfile();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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


    @Override
    public void displayProfile(FbProfile profile) {
       if (USERDATA == null) {
            USERDATA = new JSONObject();
            try {

                if (profile.getFirst_name() != null){
                    USERDATA.put("Name", profile.getFirst_name());
                }

                if (profile.getGender() != null){
                    USERDATA.put("Gender", profile.getGender());
                }

                if (profile.getBirthday() != null){

                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                    DateTime dateTime = formatter.parseDateTime(profile.getBirthday());
                    Period period = new Period(dateTime, new DateTime());

                    USERDATA.put("Age",period.getYears());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
