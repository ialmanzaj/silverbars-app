package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.andretietz.retroauth.AuthAccountManager;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerUserPreferencesComponent;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.modules.UserPreferencesModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.UserPreferencesPresenter;
import com.app.app.silverbarsapp.viewsets.UserPreferencesView;

import javax.inject.Inject;

public class UserPreferencesActivity extends BaseActivity implements UserPreferencesView {

    private static final String TAG = UserPreferencesActivity.class.getSimpleName();

    @Inject
    UserPreferencesPresenter mUserPreferencesPresenter;

    Person person;

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
        mUserPreferencesPresenter.getMyProfile();
    }

    @Override
    public void onProfileSaved(Person person) {
        this.person = person;
        mUserPreferencesPresenter.getMyWorkouts();
    }

    @Override
    public void onWorkoutSaved() {
        saveAccount();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveAccount(){
        AuthAccountManager authAccountManager = new AuthAccountManager();
        Account activeAccount = authAccountManager.getActiveAccount(getString(R.string.authentication_ACCOUNT));
        AccountManager.get(UserPreferencesActivity.this).setUserData(
                activeAccount, getString(R.string.authentication_USER),person.toString());
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
