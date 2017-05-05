package com.app.app.silverbarsapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.adapters.ExerciseDetailWeeklyAdapter;
import com.app.app.silverbarsapp.adapters.MuscleActivationAdapter;
import com.app.app.silverbarsapp.adapters.SkillAdapter;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.MuscleActivation;
import com.app.app.silverbarsapp.models.Skill;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseDetailWeeklyActivity extends AppCompatActivity {

    private static final String TAG = ExerciseDetailWeeklyActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.exercises)RecyclerView mExercisesList;
    @BindView(R.id.skills)RecyclerView mSkillsList;

    @BindView(R.id.muscle_selected)TextView mMuscleSelected;
    @BindView(R.id.activation) RecyclerView mMuscleActivation;

    private Utilities utilities = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        ButterKnife.bind(this);

        setupTabs();

        Bundle extras = getIntent().getExtras();

        //title Toolbar
        String title = extras.getString("title");
        setupToolbar(title);

        //subtitle toolbar
        String muscle = extras.getString("subtitle","");
        mMuscleSelected.setText(muscle);

        //which date is month,week or daily
        int type_date = extras.getInt("type_date");

        ArrayList<ExerciseProgression> mExercises = extras.getParcelableArrayList("exercises");

        setupAdapter(mExercises,type_date);
        setupAdapterSkills(mExercises);


        MuscleActivation muscleActivation = extras.getParcelable("muscle_activation");
        setupAdapterMuscleActivation(muscleActivation,type_date);
    }



    private void setupTabs(){
        TabHost Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec muscle_activation = Tab_layout.newTabSpec("Activation");
        muscle_activation.setIndicator("Activation");
        muscle_activation.setContent(R.id.activation);

        TabHost.TabSpec exercises = Tab_layout.newTabSpec("Exercises");
        exercises.setIndicator("Exercises");
        exercises.setContent(R.id.exercises);

        TabHost.TabSpec skills = Tab_layout.newTabSpec("Focus");
        skills.setIndicator("Focus");
        skills.setContent(R.id.skills);

        Tab_layout.addTab(muscle_activation);
        Tab_layout.addTab(exercises);
        Tab_layout.addTab(skills);
    }

    private void setupAdapter(ArrayList<ExerciseProgression> exercises, int type_date){
        //mExercisesList settings
        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setNestedScrollingEnabled(false);
        mExercisesList.setHasFixedSize(false);
        mExercisesList.setAdapter(new ExerciseDetailWeeklyAdapter(exercises,type_date));
    }

    private void setupAdapterSkills(ArrayList<ExerciseProgression> exercises){
        //mExercisesList settings
        mSkillsList.setLayoutManager(new LinearLayoutManager(this));
        mSkillsList.setNestedScrollingEnabled(false);
        mSkillsList.setHasFixedSize(false);
        mSkillsList.setAdapter(new SkillAdapter(getTypes(exercises)));
    }

    private void setupAdapterMuscleActivation(MuscleActivation muscleActivation, int type_date ){
        MuscleActivationAdapter adapter = new MuscleActivationAdapter(type_date);
        //mExercisesList settings
        adapter.add(muscleActivation);
        mMuscleActivation.setLayoutManager(new LinearLayoutManager(this));
        mMuscleActivation.setNestedScrollingEnabled(false);
        mMuscleActivation.setHasFixedSize(false);
        mMuscleActivation.setAdapter(adapter);
    }

    private List<Skill> getTypes(ArrayList<ExerciseProgression> exercises){
        List<Skill> types = new ArrayList<>();
        List<String> types_list = new ArrayList<>();
        for (ExerciseProgression exercise: exercises){
            for (String type: exercise.getExercise().getType_exercise()){
                types_list.add(type);
            }
        }
        for (String type : utilities.deleteCopiesofList(types_list)){
            double porcentaje = (utilities.counter(types_list,type)*100 / types_list.size());
            //Log.d(TAG,type +" " + porcentaje);
            types.add(new Skill(type, (int) porcentaje));
        }

        return types;
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
