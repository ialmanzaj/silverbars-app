package com.app.proj.silverbars.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.andretietz.retroauth.AuthenticationActivity;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.presenters.BasePresenter;

import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public abstract class BaseLoginActivity extends AuthenticationActivity {

    private Toolbar mToolbar;

    /**
     * The onCreate base will set the view specified in {@link #getLayout()} and will
     * inject dependencies and views.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        injectDependencies();
        injectViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getPresenter() != null)
            getPresenter().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (getPresenter() != null)
            getPresenter().onStop();
    }

    /**
     * Its common use a toolbar within activity, if it exists in the
     * layout this will be configured
     */
    public void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    /**
     * @return The layout that's gonna be the activity view.
     */
    protected abstract int getLayout();

    /**
     * @return The presenter attached to the activity. This must extends from {@link BasePresenter}
     */
    @Nullable
    protected abstract BasePresenter getPresenter();

    /**
     * Setup the object graph and inject the dependencies needed on this activity.
     */
    public void injectDependencies() {
        //setUpComponent(SpotifyStreamerApp.getApp(this).getComponent());
    }

    /**
     * Every object annotated with {@link } its gonna injected trough butterknife
     */
    private void injectViews() {
        ButterKnife.bind(this);
    }

}
