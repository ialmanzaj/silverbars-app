package com.app.app.silverbarsapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ResultsAdapter;
import com.app.app.silverbarsapp.components.DaggerResultsComponent;
import com.app.app.silverbarsapp.handlers.ProgressionAlgoritm;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.modules.ResultsModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ResultsPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ResultsView;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ResultsActivity extends BaseActivity implements ResultsView {

    private static final String TAG = ResultsActivity.class.getSimpleName();

    @Inject
    ResultsPresenter mResultsPresenter;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.total_time) TextView mTotalTime;
    @BindView(R.id.sets) TextView mSetsCompletedText;
    @BindView(R.id.exercises) RecyclerView mExercisesList;

    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;

    //@BindView(R.id.muscles) RecyclerView mMuscleActivation;

    private Utilities utilities = new Utilities();
    private ProgressionAlgoritm mProgressionAlgoritm = new ProgressionAlgoritm();

    private int workout_id;
    boolean isUserWorkout;
    private String total_time;
    private int mSetsCompleted;
    private int mTotalSets;
    private int exercises_completed;
    private ArrayList<ExerciseProgression> mExercises = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_results;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mResultsPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerResultsComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .resultsModule(new ResultsModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        setupTabs();
        Bundle extras =  getIntent().getExtras();

        workout_id = extras.getInt("workout_id");
        isUserWorkout = extras.getBoolean("user_workout",false);
        mExercises = extras.getParcelableArrayList("exercises");
        mTotalSets = extras.getInt("sets");
        int current_set = extras.getInt("current_set");
        exercises_completed = extras.getInt("exercises_completed");
        total_time = extras.getString("total_time");
        mSetsCompleted = getSetsCompleted(current_set,exercises_completed,mExercises.size());

        //update UI
        mSetsCompletedText.setText(String.valueOf(mSetsCompleted) + "/" + String.valueOf(mTotalSets));
        mTotalTime.setText(String.valueOf(total_time));


        getOldProgression();
    }

    private int getSetsCompleted(int current_set,int exercises_completed,int total_exercises){
        if (exercises_completed == total_exercises){
            return current_set;
        }else {
            return current_set-1;
        }
    }


    private void getOldProgression(){
        try {
            mResultsPresenter.getExercisesProgression(mExercises);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAdapter(ArrayList<ExerciseProgression> exercises){
        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setNestedScrollingEnabled(false);
        mExercisesList.setHasFixedSize(false);
        mExercisesList.setAdapter(new ResultsAdapter(this,exercises));
    }

    public void setupToolbar(){
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(getString(R.string.activity_results_title));
        }
    }

    private void setupTabs(){
        TabHost Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec overview = Tab_layout.newTabSpec("Overview");
        overview.setIndicator(getString(R.string.activity_results_tab_overview));
        overview.setContent(R.id.overview);

        TabHost.TabSpec exercises = Tab_layout.newTabSpec("Exercises");
        exercises.setIndicator(getString(R.string.activity_results_tab_exercises));
        exercises.setContent(R.id.exercises);

        //TabHost.TabSpec muscles = Tab_layout.newTabSpec("Muscles");
        //muscles.setIndicator(getString(R.string.tab_muscles));
        //muscles.setContent(R.id.muscles);

        Tab_layout.addTab(exercises);
        Tab_layout.addTab(overview);
        //Tab_layout.addTab(muscles);
    }

   /* private void setupAdapterMuscleActivation(ArrayList<MuscleActivation> muscleActivations){
        mMuscleActivation.setLayoutManager(new LinearLayoutManager(this));
        mMuscleActivation.setNestedScrollingEnabled(false);
        mMuscleActivation.setHasFixedSize(false);
        mMuscleActivation.setAdapter(new ResultsMuscleActivation(muscleActivations));
    }*/

    @OnClick(R.id.save_button)
    public void savebutton(){
        saveResults();
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        saveResults();
    }

    private void saveResults(){
        try {
            onLoadingViewOn();
            mResultsPresenter.createWorkoutDone(workout_id,mSetsCompleted,total_time,isUserWorkout);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void onWorkoutDone(WorkoutDone workout) {
        try {
            mResultsPresenter.saveExerciseProgressions(workout.getId(),mTotalSets,mExercises);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExerciseProgressionsSaved() {
        onLoadingViewOff();
        utilities.toast(this,getString(R.string.activity_results_saved));
        finish();
    }

    @Override
    public void isEmptyProgression() {
        ArrayList<ExerciseProgression> progressions_compared = mProgressionAlgoritm.addFirstProgressions(mExercises);
        setupAdapter(progressions_compared);
    }

    @Override
    public void onExerciseProgression(ArrayList<ExerciseProgression> exerciseProgressions) {
        ArrayList<ExerciseProgression> progressions_compared = mProgressionAlgoritm.compareExerciseProgression(exerciseProgressions,mExercises);
        setupAdapter(progressions_compared);
    }

    private void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){mErrorView.setVisibility(View.VISIBLE);}

    private void onErrorViewOff(){mErrorView.setVisibility(View.GONE);}

    @Override
    public void onBackPressed() {
        dialog();
    }

    @OnClick(R.id.delete)
    public void deleteButton(){
        dialog();
    }

    private void dialog(){
        new MaterialDialog.Builder(this)
                .title(getString(R.string.activity_results_dialog_title))
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content(getString(R.string.activity_results_dialog_content))
                .positiveText(getResources().getString(R.string.positive_dialog))
                .onPositive((dialog, which) -> finish()).negativeText(getResources().getString(R.string.negative_dialog)).
                onNegative((dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            dialog();
        }
        return super.onOptionsItemSelected(item);
    }

}
