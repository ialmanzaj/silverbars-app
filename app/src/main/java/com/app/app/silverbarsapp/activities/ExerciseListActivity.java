package com.app.app.silverbarsapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.AllExercisesAdapter;
import com.app.app.silverbarsapp.components.DaggerExerciseListComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.modules.ExerciseListModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ExerciseListPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ExerciseListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseListActivity extends BaseActivity implements ExerciseListView {

    private static final String TAG = ExerciseListActivity.class.getSimpleName();

    private Utilities utilities = new Utilities();

    @Inject
    ExerciseListPresenter mExerciseListPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload)Button mReload;

    @BindView(R.id.exercises_list) RecyclerView list;
    @BindView(R.id.add_exercises) Button mAddExercises;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;

    private AllExercisesAdapter adapter;

    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<String> muscles_selected;

    private ArrayList<Integer> mExercisesSelectedIds;


    @Override
    protected int getLayout() {
        return R.layout.activity_exercise_list;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mExerciseListPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerExerciseListComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .exerciseListModule(new ExerciseListModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();

        getExtras(getIntent().getExtras());

        list.setLayoutManager(new LinearLayoutManager(this));
        mExerciseListPresenter.getExercises();
    }

    private void getExtras(Bundle extras){
        muscles_selected = extras.getStringArrayList("muscles");

        if (extras.getIntegerArrayList("exercises") != null){
            mExercisesSelectedIds = extras.getIntegerArrayList("exercises");
        }
    }

    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_exercises_title));
    }

    private String getJson(){
        String json = null;
        try {
            InputStream is = this.getAssets().open("exercises.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    @OnClick(R.id.reload)
    public void reloadButton(){
        onErrorOff();
        onProgressViewOn();
        mExerciseListPresenter.getExercises();
    }

    @OnClick(R.id.add_exercises)
    public void addExercises(){

        ArrayList<Integer> mListExercisesIds = new ArrayList<>();

        for(int i = 0; i < adapter.getExercisesSelected().size(); i++) {
            Object exercise_is_selected = adapter.getExercisesSelected().valueAt(i);

            if ((Boolean)exercise_is_selected){
                int exercise_id = adapter.getExercisesSelected().keyAt(i);
                mListExercisesIds.add(exercise_id);
            }
        }

        if (mListExercisesIds.size() > 0) {
            // enviar items a la actividad anterior
            Intent return_intent = new Intent();
            return_intent.putExtra("exercises",mExercises);
            return_intent.putExtra("exercises_selected",mListExercisesIds);
            setResult(RESULT_OK, return_intent);
            finish();
        }

    }


    @Override
    public void displayExercises(List<Exercise> exercises) {
        onProgressViewOff();
        mExercises.addAll(exercises);

        if (mExercisesSelectedIds != null){
            setExercisesView(filterExerciseByMuscle(muscles_selected, getExercisesNoSelected(mExercisesSelectedIds,mExercises)));
        }else {
            setExercisesView(filterExerciseByMuscle(muscles_selected,mExercises));
        }

    }

    @Override
    public void displayNetworkError() {
        onErrorOn();
    }

    @Override
    public void displayServerError() {
        onErrorOn();
    }


    private ArrayList<Exercise> filterExerciseByMuscle(ArrayList<String> muscles_selected,ArrayList<Exercise> exercises){
        Log.d(TAG,"muscles_selected: "+muscles_selected);
        ArrayList<Exercise> exercises_filtered_muscles = new ArrayList<>();
        for (Exercise exercise: exercises){
                for (String muscle_name: muscles_selected){
                    for (MuscleExercise muscle: exercise.getMuscles()){
                        if (Objects.equals(muscle_name, muscle.getMuscle())){
                            if (!exercises_filtered_muscles.contains(exercise)) {
                                exercises_filtered_muscles.add(exercise);
                            }
                        }
                    }
                }
        }

        return exercises_filtered_muscles;
    }

    private ArrayList<Exercise> getExercisesNoSelected(ArrayList<Integer> exercises_ids,ArrayList<Exercise> exercises){
        for (Integer exercise_id: exercises_ids){
            exercises.remove(utilities.getExerciseById(exercises,exercise_id));
        }
        return exercises;
    }

    private void setExercisesView(ArrayList<Exercise> exercises){
        if (exercises.size() > 0) {
            adapter = new AllExercisesAdapter(this, exercises);
            list.setAdapter(adapter);
        }else {
            onEmptyViewOn();
        }
    }

    private void onErrorOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorOff(){
        mErrorView.setVisibility(View.GONE);
    }

    private void onProgressViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onProgressViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onEmptyViewOn(){mEmptyView.setVisibility(View.VISIBLE);}

    private void onEmptyViewOff(){mEmptyView.setVisibility(View.GONE);}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}