package com.app.app.silverbarsapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExerciseDetailAdapter;
import com.app.app.silverbarsapp.adapters.MuscleActivationAdapter;
import com.app.app.silverbarsapp.adapters.SkillAdapter;
import com.app.app.silverbarsapp.components.DaggerExerciseDetailComponent;
import com.app.app.silverbarsapp.models.ExerciseProgressionCompared;
import com.app.app.silverbarsapp.models.MuscleActivationCompared;
import com.app.app.silverbarsapp.modules.ExerciseDetailModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ExerciseDetailPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ExerciseDetailView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class ExerciseDetailActivity extends BaseActivity implements ExerciseDetailView {

    private static final String TAG = ExerciseDetailActivity.class.getSimpleName();

    @Inject
    ExerciseDetailPresenter mExerciseDetailPresenter;


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.exercises)RecyclerView mExercisesList;
    @BindView(R.id.skills)RecyclerView mSkillsList;
    @BindView(R.id.muscle_selected)TextView mMuscleSelected;
    @BindView(R.id.activation) RecyclerView mMuscleActivation;

    private Utilities utilities = new Utilities();

    @Override
    protected int getLayout() {
        return R.layout.activity_exercise_detail;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mExerciseDetailPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerExerciseDetailComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .exerciseDetailModule(new ExerciseDetailModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTabs();

        Bundle extras = getIntent().getExtras();

        //title Toolbar
        String title = extras.getString("title");
        setupToolbar(title);

        //subtitle toolbar
        String muscle = extras.getString("subtitle","");
        mMuscleSelected.setText(muscle);

        ArrayList<ExerciseProgressionCompared> mExercises = extras.getParcelableArrayList("exercises");

        setupAdapter(mExercises);
        setupAdapterSkills(mExercises);


        MuscleActivationCompared muscleActivation = extras.getParcelable("muscle_activation");
        setupAdapterMuscleActivation(muscleActivation);
    }

    private void setupTabs(){
        TabHost Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec muscle_activation = Tab_layout.newTabSpec("Activation");
        muscle_activation.setIndicator(getString(R.string.exercise_detail_tab_activacion));
        muscle_activation.setContent(R.id.activation);

        TabHost.TabSpec exercises = Tab_layout.newTabSpec("Exercises");
        exercises.setIndicator(getString(R.string.exercise_detail_tab_exercises));
        exercises.setContent(R.id.exercises);

        TabHost.TabSpec skills = Tab_layout.newTabSpec("Focus");
        skills.setIndicator(getString(R.string.exercise_detail_tab_focus));
        skills.setContent(R.id.skills);

        Tab_layout.addTab(exercises);
        Tab_layout.addTab(skills);
        Tab_layout.addTab(muscle_activation);
    }

    private void setupAdapter(ArrayList<ExerciseProgressionCompared> progressions){
        //mExercisesList settings
        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setNestedScrollingEnabled(false);
        mExercisesList.setHasFixedSize(false);
        mExercisesList.setAdapter(new ExerciseDetailAdapter(this,progressions));
    }

    private void setupAdapterSkills(ArrayList<ExerciseProgressionCompared> progressions){
        //mExercisesList settings
        mSkillsList.setLayoutManager(new LinearLayoutManager(this));
        mSkillsList.setNestedScrollingEnabled(false);
        mSkillsList.setHasFixedSize(false);
        mSkillsList.setAdapter(new SkillAdapter(utilities.getTypesByExerciseProgression(progressions)));
    }

    private void setupAdapterMuscleActivation(MuscleActivationCompared muscleActivation){
        MuscleActivationAdapter adapter = new MuscleActivationAdapter();
        //mExercisesList settings
        adapter.add(muscleActivation);
        mMuscleActivation.setLayoutManager(new LinearLayoutManager(this));
        mMuscleActivation.setNestedScrollingEnabled(false);
        mMuscleActivation.setHasFixedSize(false);
        mMuscleActivation.setAdapter(adapter);
    }

    private void setupToolbar(String title){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
