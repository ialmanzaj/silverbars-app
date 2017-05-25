package com.app.app.silverbarsapp.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
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

import com.andretietz.retroauth.AuthAccountManager;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerMainComponent;
import com.app.app.silverbarsapp.fragments.MyWorkoutsFragment;
import com.app.app.silverbarsapp.fragments.ProfileFragment;
import com.app.app.silverbarsapp.fragments.ProgressFragment;
import com.app.app.silverbarsapp.modules.MainModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.MainPresenter;
import com.app.app.silverbarsapp.utils.OnItemSelectedListener;
import com.app.app.silverbarsapp.viewsets.MainView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;

public class MainActivity extends BaseActivity implements MainView,BottomNavigationView.OnNavigationItemSelectedListener,OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.fab) FloatingActionButton mButtonCreateWorkout;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    private String muscle = "ALL";

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

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            selectItem(0);
        }

        AuthAccountManager authAccountManager = new AuthAccountManager();
        Account activeAccount = authAccountManager.getActiveAccount(getString(R.string.authentication_ACCOUNT));

        String id =  AccountManager.get(this).getUserData(activeAccount, getString(R.string.authentication_ID));
        mixpanelEvent(id);
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

    private void mixpanelEvent(String id){
        MixpanelAPI mixpanel =
                MixpanelAPI.getInstance(this, MIX_PANEL_TOKEN);

        mixpanel.getPeople().identify(id);
    }

    @Override
    public void onChangeNavigation(int position) {
        selectItem(position);
    }


    @OnClick(R.id.fab)
    public void createButton(){
        startActivity(new Intent(this,CreateWorkoutActivity.class));
    }

    /**
     *
     *
     *
     *
     *     UI events
     *<p>
     *
     *
     *
     *
     *
     */
    private void createWorkoutbuttonOn(){
        mButtonCreateWorkout.setVisibility(View.VISIBLE);
    }

    private void createWorkoutbuttonff(){
        mButtonCreateWorkout.setVisibility(View.GONE);
    }

}
