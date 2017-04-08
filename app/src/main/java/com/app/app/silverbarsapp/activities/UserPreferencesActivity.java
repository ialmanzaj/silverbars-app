package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerUserPreferencesComponent;
import com.app.app.silverbarsapp.modules.UserPreferencesModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.UserPreferencesPresenter;
import com.app.app.silverbarsapp.viewsets.UserPreferencesView;

import javax.inject.Inject;

public class UserPreferencesActivity extends BaseActivity implements UserPreferencesView {

    private static final String TAG = UserPreferencesActivity.class.getSimpleName();

    @Inject
    UserPreferencesPresenter mUserPreferencesPresenter;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_preferences;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mUserPreferencesPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerUserPreferencesComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .userPreferencesModule(new UserPreferencesModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"UserPreferencesActivity");

        mUserPreferencesPresenter.getMyProfile();
    }

    @Override
    public void onProfileSaved() {
        mUserPreferencesPresenter.getMyWorkouts();
    }

    @Override
    public void onWorkoutSaved() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
    }


}
