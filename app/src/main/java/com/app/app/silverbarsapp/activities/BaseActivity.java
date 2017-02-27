package com.app.app.silverbarsapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.presenters.BasePresenter;

import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 01/21/17.
 */

/**
 * Created by Pedro Antonio Hernández on 12/06/2015.
 *
 * <p>
 * The activity only will execute operations that affect the UI. These operations are defined
 * by a view model and are triggered by its presenter.
 * </p>
 *
 * <p>
 * Perhaps the activity only will work as a fragment container, if that is the case only
 * return null on {@link BaseActivity#getPresenter()}
 * </p>
 * <p>
 * I made some modifications to the BaseActivity presented in the next project.
 * I implement Dagger 2 instead Dagger 1 and i attach the presenter directly
 * <a href="https://github.com/pedrovgs/EffectiveAndroidUI">EffectiveAndroidUI</a>
 * </p>
 */
public abstract class BaseActivity extends AppCompatActivity {

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
    protected void onResume() {
        super.onResume();

        if (getPresenter() != null)
            getPresenter().onResume();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (getPresenter() != null)
            getPresenter().onRestart();

    }


    @Override
    protected void onPause() {
        super.onPause();

        if (getPresenter() != null)
            getPresenter().onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (getPresenter() != null)
            getPresenter().onStop();
    }

    @Override
    protected void onDestroy() {

        if (getPresenter() != null)
            getPresenter().onDestroy();

        super.onDestroy();
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


    //public abstract void setUpComponent(SpotifyStreamerComponent appComponent);

}