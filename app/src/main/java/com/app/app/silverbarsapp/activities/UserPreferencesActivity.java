package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.OnClick;

public class UserPreferencesActivity extends BaseActivity implements UserPreferencesView {

    private static final String TAG = UserPreferencesActivity.class.getSimpleName();

    @Inject
    UserPreferencesPresenter mUserPreferencesPresenter;

    @BindView(R.id.error_view)LinearLayout mErrorView;
    @BindView(R.id.loading)LinearLayout mLoadingView;
    @BindView(R.id.error_text) TextView mErrorText;

    private Person person;

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

        getMyProfilefromApi();
    }

    private void getMyProfilefromApi(){
        mUserPreferencesPresenter.getMyProfile();
    }

    @Override
    public void onProfileSaved(Person person) {
        this.person = person;
        mUserPreferencesPresenter.getMyWorkouts();
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingOn();
        getMyProfilefromApi();
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
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
        AccountManager.get(this).setUserData(
                activeAccount, getString(R.string.authentication_USER),person.toString());
    }


    /**
     *
     *
     *     UI events
     *
     *
     *
     */


    private void onLoadingOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorViewOn(String error_text){mErrorText.setText(error_text);mErrorView.setVisibility(View.VISIBLE);}

    private void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);
    }
}
