package com.app.app.silverbarsapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExerciseAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkoutComponent;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.WorkoutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkoutPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkoutView;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutActivity extends BaseActivity implements WorkoutView{

    private static final String TAG = WorkoutActivity.class.getSimpleName();

    @Inject
    WorkoutPresenter mWorkoutPresenter;

    @BindView(R.id.toolbar) Toolbar myToolbar;
    @BindView(R.id.tabHost2) TabHost mTabLayout;

    @BindView(R.id.sets) TextView Sets;
    @BindView(R.id.rest_by_set) AutoCompleteTextView RestbySet;
    @BindView(R.id.rest_by_exercise) AutoCompleteTextView RestbyExercise;

    @BindView(R.id.list) RecyclerView list;
    @BindView(R.id.muscles) RelativeLayout mBodyMuscleWrapper;
    @BindView(R.id.webview) WebView webview;

    @BindView(R.id.toggle_save_workout) RelativeLayout mSaveWorkoutLayout;
    @BindView(R.id.save_workout_local) SwitchCompat mSaveWorkoutSwitch;

    @BindView(R.id.voice_per_exercise)SwitchCompat mVoicePerExercise;

    @BindView(R.id.start_button) Button mStartButton;
    @BindView(R.id.SelectMusic) RelativeLayout mSelectMusicButton;

    private ExerciseAdapter adapter;

    private int workoutId = 0, workoutSets = 0;
    private String workoutName, workoutLevel, mainMuscle, workoutImgUrl;

    boolean isDownloadAudioExerciseActive = false;

    private String mMuscleParts = " ";

    //music vars
    private String[] mLocalSongsNames;
    private ArrayList<File> mLocalSongsFiles;

    //spotify vars
    private String mPlaylistSpotify,mSpotifyToken;

    private ArrayList<ExerciseRep> mExercises;

    private Utilities utilities = new Utilities();

    @Override
    protected int getLayout() {
        return R.layout.activity_workout;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mWorkoutPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerWorkoutComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .workoutModule(new WorkoutModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWorkoutPresenter.init(this);
        getExtras(getIntent().getExtras());
        setupToolbar();
        setupTabs();
        setupWebview();
        setupAdapter();
    }


    private void getExtras(Bundle extras){
        workoutId = extras.getInt("workout_id",0);
        workoutName = extras.getString("name");
        workoutImgUrl = extras.getString("image");
        workoutSets = extras.getInt("sets", 1);
        workoutLevel = extras.getString("level");
        mainMuscle = extras.getString("main_muscle");
        mExercises =  getIntent().getParcelableArrayListExtra("exercises");
        boolean isUserWorkout = extras.getBoolean("user_workout",false);

        //init the ui
        initUI(isUserWorkout);

        setExercisesInAdapter(mExercises);
    }


    public void setupToolbar(){
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(workoutName);
    }

    private void initUI(boolean isUserWorkout){
        Sets.setText(String.valueOf(workoutSets));
        RestbyExercise.setText("30");
        RestbySet.setText("60");

        mVoicePerExercise.setOnCheckedChangeListener((compoundButton, isChecked) -> {});

        mStartButton.setOnClickListener(view -> LaunchWorkingOutActivity());
        mSelectMusicButton.setOnClickListener(v -> { startActivity(new Intent(this,SelectionMusicActivity.class));});

        saveWorkoutUi(isUserWorkout);
    }

    private void saveWorkoutUi(boolean isUserWorkout) {
        if (!isUserWorkout){
            mSaveWorkoutLayout.setVisibility(View.VISIBLE);
        }

        try {

            mSaveWorkoutSwitch.setChecked(mWorkoutPresenter.isWorkoutAvailable(workoutId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
            mSaveWorkoutSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked){
                    saveWorkout();
                } else{
                    logMessage("Switch off");
                    try {
                        mWorkoutPresenter.setWorkoutOff(workoutId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
    }


    private void setupTabs(){
        //Defining Tabs
        mTabLayout.setup();

        TabHost.TabSpec overview = mTabLayout.newTabSpec(getResources().getString(R.string.tab_overview));
        TabHost.TabSpec muscles = mTabLayout.newTabSpec(getResources().getString(R.string.tab_muscles));
        TabHost.TabSpec exercises = mTabLayout.newTabSpec(getResources().getString(R.string.tab_exercises));

        overview.setIndicator(getResources().getString(R.string.tab_overview));
        overview.setContent(R.id.overview);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        exercises.setIndicator(getResources().getString(R.string.tab_exercises));
        exercises.setContent(R.id.exercises);

        mTabLayout.addTab(overview);
        mTabLayout.addTab(exercises);
        mTabLayout.addTab(muscles);
    }

    private void setupWebview(){
        webview.getSettings().setJavaScriptEnabled(true);
        utilities.loadUrlOfMuscleBody(this,webview);
    }

    private void setupAdapter(){
        //list settings
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setNestedScrollingEnabled(false);
        list.setHasFixedSize(false);
    }

    @Override
    public void onWorkout(boolean created) {
        Log.d(TAG,"onWorkoutcreated: "+created);
    }


    private void setExercisesInAdapter(ArrayList<ExerciseRep> exercises){

        List<String> muscles = new ArrayList<>();

        for (ExerciseRep exerciseRep: exercises){
            //Collections.addAll(TypeExercises, new List<String>[]{exerciseRep.getExercise().getType_exercise()});
          for (MuscleExercise muscle:  exerciseRep.getExercise().getMuscles()){muscles.add(muscle.getMuscle());}
        }


        Log.d(TAG,"weight "+exercises.get(0).getWeight());

        adapter = new ExerciseAdapter(this,exercises);
        list.setAdapter(adapter);

        setMusclesToView(muscles);
        //putTypesInWorkout(TypeExercises);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){

            if (data.hasExtra("playlist_spotify") && data.hasExtra("token")){

                mPlaylistSpotify = data.getStringExtra("playlist_spotify");
                mSpotifyToken =  data.getStringExtra("token");

            }else if (data.hasExtra("songs") && data.hasExtra("positions")){

                mLocalSongsNames = data.getStringArrayExtra("positions");
                mLocalSongsFiles = (ArrayList<File>) data.getSerializableExtra("songs");
            }
        }
    }


    private void LaunchWorkingOutActivity() {
        Intent intent = new Intent(this, WorkingOutActivity.class);

        intent.putParcelableArrayListExtra("exercises", adapter.getExercises());
        intent.putExtra("sets",Integer.parseInt(Sets.getText().toString()));

        //rests
        intent.putExtra("rest_exercise",Integer.parseInt(RestbyExercise.getText().toString()));
        intent.putExtra("rest_set",Integer.parseInt(RestbySet.getText().toString()));

        //vibrations option
        intent.putExtra("vibration_per_set",true);

        //exercise audio option
        intent.putExtra("exercise_audio",isDownloadAudioExerciseActive);

        //songs of the phone
        intent.putExtra("playlist_local",mLocalSongsFiles);
        intent.putExtra("songs_local",mLocalSongsNames);

        //songs of spotify
        intent.putExtra("spotify_playlist",mPlaylistSpotify);
        intent.putExtra("spotify_token",mSpotifyToken);

        intent.putExtra("workout_id",workoutId);
        Log.d(TAG,"workout_id: "+workoutId);

        startActivity(intent);
    }

    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){
            mMuscleParts  = utilities.getMusclesReadyForWebview(utilities.deleteCopiesofList(musculos));
        }
        utilities.onWebviewReady(webview,mMuscleParts);
    }

    private void logMessage(String msg) {
        Log.v(TAG, msg);
    }

    private Workout getCurrentWorkout(){
        return new Workout(workoutId,workoutName,workoutImgUrl,workoutSets,workoutLevel,mainMuscle,mExercises);
    }

    private void saveWorkout() {
        Log.d(TAG,"save workout");
        try {

            //check if workout doesnt exist in the database
            if (!mWorkoutPresenter.isWorkoutExist(workoutId)){
                //save in database
                mWorkoutPresenter.saveWorkout(getCurrentWorkout());

            } else {

                //if exist set workout mode ON
                mWorkoutPresenter.setWorkoutOn(workoutId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
