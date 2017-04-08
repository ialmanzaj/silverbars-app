package com.app.app.silverbarsapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ResultsAdapter;
import com.app.app.silverbarsapp.components.DaggerResultsComponent;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.modules.ResultsModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ResultsPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ResultsView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.list) RecyclerView mExercisesList;

    private Utilities utilities = new Utilities();


    private int workout_id;
    private int sets;
    String total_time;
    private ArrayList<ExerciseRep> mExercises = new ArrayList<>();
    private Utilities mUtilities = new Utilities();

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

        Bundle extras =  getIntent().getExtras();

        workout_id = extras.getInt("workout_id");
        mExercises = extras.getParcelableArrayList("exercises");
        sets = extras.getInt("sets");
        total_time = extras.getString("total_time");

        mTotalTime.setText(String.valueOf(total_time));

        try {
            mResultsPresenter.getExercisesProgression(mExercises);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //setupTabs();
    }


    public void setupToolbar(){
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Results");
        }
    }

    private void setupTabs(){
        TabHost Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec muscles = Tab_layout.newTabSpec(getResources().getString(R.string.tab_muscles));

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        Tab_layout.addTab(muscles);
    }



    @OnClick(R.id.save_button)
    public void save(){

        try {
            mResultsPresenter.createWorkoutDone(workout_id,sets,total_time);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayNetworkError() {
    }

    @Override
    public void displayServerError() {
    }

    @Override
    public void onWorkoutDone(WorkoutDone workout) {
        Log.d(TAG,"workout" +workout.getDate());
        utilities.toast(this,"Your results are saved");
    }

    @Override
    public void isEmptyProgression() {
        Log.d(TAG,"isEmptyProgression");
        //setupAdapter(mExercises);
    }

    @Override
    public void onExerciseProgression(List<com.app.app.silverbarsapp.database_models.ExerciseProgression> exerciseProgressions) {
        Log.d(TAG,"onExerciseProgression");
        setupAdapter(compareExerciseProgression(exerciseProgressions,mExercises));
    }


    private void setupAdapter(ArrayList<ExerciseProgression> exercises){
        //list settings
        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setNestedScrollingEnabled(false);
        mExercisesList.setHasFixedSize(false);
        mExercisesList.setAdapter(new ResultsAdapter(this,exercises));
    }


    private ArrayList<ExerciseProgression> compareExerciseProgression(List<com.app.app.silverbarsapp.database_models.ExerciseProgression> exerciseProgressions, ArrayList<ExerciseRep> current_exercises){
        ArrayList<ExerciseProgression> progressions = new ArrayList<>();

        for (com.app.app.silverbarsapp.database_models.ExerciseProgression old_last_exercise_progression: exerciseProgressions){
            for (ExerciseRep current_exercise: current_exercises){

                ExerciseProgression exerciseProgression_ready = new ExerciseProgression();

                if (old_last_exercise_progression.getExercise().getId() == current_exercise.getExercise().getId()){

                    if (old_last_exercise_progression.getRepetitions_done() > 0){

                        exerciseProgression_ready.setExercise(current_exercise.getExercise());
                        exerciseProgression_ready.setRepetitions_done(current_exercise.getRepetition());

                        if ( current_exercise.getRepetition()  > old_last_exercise_progression.getRepetitions_done()){

                            Log.d(TAG,current_exercise.getExercise().getExercise_name()+" rep mejoro ");
                            exerciseProgression_ready.setPositive(true);

                        }else if(current_exercise.getRepetition() == old_last_exercise_progression.getRepetitions_done()) {

                            exerciseProgression_ready.setEqual(true);


                        }else {

                            Log.d(TAG,current_exercise.getExercise().getExercise_name()+" rep bajo ");
                            exerciseProgression_ready.setPositive(false);
                        }

                        progressions.add(exerciseProgression_ready);

                    }else {

                        exerciseProgression_ready.setExercise(current_exercise.getExercise());
                        exerciseProgression_ready.setSeconds_done(current_exercise.getSeconds());

                        if ( current_exercise.getSeconds()  > old_last_exercise_progression.getSeconds_done()){

                            Log.d(TAG,"current_exercise second es mayor "+current_exercise.getExercise().getExercise_name());
                            exerciseProgression_ready.setPositive(true);

                        }else if(current_exercise.getSeconds() == old_last_exercise_progression.getSeconds_done()) {

                            exerciseProgression_ready.setEqual(true);

                        }else {
                            Log.d(TAG,"current_exercise second es mayor "+current_exercise.getExercise().getExercise_name());
                            exerciseProgression_ready.setPositive(false);
                        }


                        progressions.add(exerciseProgression_ready);
                    }


                }else {

                    Log.d(TAG,"No tiene progressiones viejas"+current_exercise.getExercise().getExercise_name());
                    exerciseProgression_ready.setExercise(current_exercise.getExercise());
                    exerciseProgression_ready.setRepetitions_done(current_exercise.getRepetition());
                    exerciseProgression_ready.setSeconds_done(current_exercise.getSeconds());
                    exerciseProgression_ready.setPositive(true);

                    progressions.add(exerciseProgression_ready);

                }
            }
        }


        Log.d(TAG,"mExercises "+mExercises.size());
        Log.d(TAG,"progressions "+progressions.size());
        return progressions;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            dialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        dialog();
    }


    private void dialog(){
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.title_dialog))
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content(getResources().getString(R.string.content_dialog))
                .positiveText(getResources().getString(R.string.positive_dialog))
                .onPositive((dialog, which) -> {
                    finish();
                }).negativeText(getResources().getString(R.string.negative_dialog)).
                onNegative((dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }
}
