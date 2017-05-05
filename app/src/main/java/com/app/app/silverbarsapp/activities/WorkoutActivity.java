package com.app.app.silverbarsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.app.silverbarsapp.MyRxBus;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.utils.SpotifyActions;
import com.app.app.silverbarsapp.adapters.ExerciseAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkoutComponent;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.WorkoutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkoutPresenter;
import com.app.app.silverbarsapp.utils.MusclesWebviewHandler;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkoutView;

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

    @BindView(R.id.exercises) RecyclerView mExercisesList;
    @BindView(R.id.muscles) RelativeLayout mBodyMuscleWrapper;
    @BindView(R.id.webview) WebView webview;

    @BindView(R.id.toggle_save_workout) RelativeLayout mSaveWorkoutLayout;
    @BindView(R.id.save_workout_local) SwitchCompat mSaveWorkoutSwitch;

    @BindView(R.id.voice_per_exercise)SwitchCompat mVoicePerExercise;

    @BindView(R.id.start_button) Button mStartButton;
    @BindView(R.id.select_music) LinearLayout mSelectMusicButton;

    @BindView(R.id.skills)RecyclerView mSkillsList;
    @BindView(R.id.music)TextView mMusicType;


    private ArrayList<ExerciseRep> mExercises;

    private ExerciseAdapter adapter;
    private int workoutId = 0, workoutSets = 0;
    private String workoutName, workoutLevel, mainMuscle, workoutImgUrl;

    boolean isDownloadAudioExerciseActive = false;
    private String music;

    private Utilities utilities = new Utilities();
    private MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();

    private SpotifyActions mSpotifyActions;
    private String mMusclesJs;

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
        //init the presenter to get the context
        mWorkoutPresenter.init(this);

        //spotify handle actions
        mSpotifyActions = new SpotifyActions(this);

        //get the extras
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
        initUI();
    }

    public void setupToolbar(){
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(workoutName);
    }

    private void initUI(){
        setExercisesInAdapter(mExercises);

        Sets.setText(String.valueOf(workoutSets));
        RestbyExercise.setText("30");
        RestbySet.setText("60");

        mVoicePerExercise.setOnCheckedChangeListener((compoundButton, isChecked) -> {});
        mStartButton.setOnClickListener(view -> LaunchWorkingOutActivity());
        mSelectMusicButton.setOnClickListener(v -> mSpotifyActions.launch());


        try {
            onSaveWorkout();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MyRxBus.instanceOf().getEvents().subscribe(object -> {
            if(object instanceof Context){
                Context context = (Context) object;
                onSpotifySelected(context);
            }
        });
    }

    private void onSpotifySelected(Context context){
        if (music == null) {
            Log.d(TAG,"onSpotifySelected");

            mSpotifyActions.stopMusic();
            //bring my app to the front
            bringMyApp(context);
            //update the ui
            mMusicType.setText("Spotify");
            //set selection spotify
            music = "spotify";
        }
    }

    private void bringMyApp(Context context){
        Intent intent = new Intent(context, WorkoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    private void onSaveWorkout() throws SQLException {
        mSaveWorkoutSwitch.setChecked(mWorkoutPresenter.isWorkoutAvailable(workoutId));
        mSaveWorkoutSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked){

                saveWorkout();

            } else{
                //logMessage("Switch off");
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

        TabHost.TabSpec overview = mTabLayout.newTabSpec(getResources().getString(R.string.tab_workout));
        TabHost.TabSpec muscles = mTabLayout.newTabSpec(getResources().getString(R.string.tab_muscles));
        TabHost.TabSpec exercises = mTabLayout.newTabSpec(getResources().getString(R.string.tab_exercises));

        overview.setIndicator(getResources().getString(R.string.tab_workout));
        overview.setContent(R.id.overview);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        exercises.setIndicator(getResources().getString(R.string.tab_exercises));
        exercises.setContent(R.id.exercises);

       /* TabHost.TabSpec skills = mTabLayout.newTabSpec("Focus");
        skills.setIndicator("Focus");
        skills.setContent(R.id.skills);*/

        mTabLayout.addTab(overview);
        mTabLayout.addTab(exercises);
        mTabLayout.addTab(muscles);
        //mTabLayout.addTab(skills);
    }

    private void setupWebview(){
        webview.getSettings().setJavaScriptEnabled(true);
        utilities.loadBodyFromLocal(this,webview);
        mMusclesWebviewHandler.addWebviewClientPaint(webview,mMusclesJs);
    }

    private void setupAdapter(){
        //mExercisesList settings
        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setNestedScrollingEnabled(false);
        mExercisesList.setHasFixedSize(false);
    }

    @Override
    public void onWorkout(boolean created) {
        //Log.d(TAG,"onWorkoutcreated: "+created);
    }

    private void setExercisesInAdapter(ArrayList<ExerciseRep> exercises){
        List<String> muscles = new ArrayList<>();

        for (ExerciseRep exerciseRep: exercises){
            //Collections.addAll(TypeExercises, new List<String>[]{exerciseRep.getExercise().getType_exercise()});
          for (MuscleExercise muscle:  exerciseRep.getExercise().getMuscles()){muscles.add(muscle.getMuscle());}
        }

        adapter = new ExerciseAdapter(this,exercises);
        mExercisesList.setAdapter(adapter);

        setMusclesToView(muscles);
        //putTypesInWorkout(TypeExercises);
    }

/*

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
*/


    private void LaunchWorkingOutActivity() {
        Intent intent = new Intent(this, WorkingOutActivity.class);

        intent.putParcelableArrayListExtra("exercises", adapter.getExercises());
        intent.putExtra("sets",Integer.parseInt(Sets.getText().toString()));
        intent.putExtra("rest_exercise",Integer.parseInt(RestbyExercise.getText().toString()));
        intent.putExtra("rest_set",Integer.parseInt(RestbySet.getText().toString()));

        //vibrations option
        intent.putExtra("vibration_per_set",true);

        //exercise audio option
        intent.putExtra("exercise_audio",isDownloadAudioExerciseActive);

        intent.putExtra("workout_id",workoutId);

        intent.putExtra("music",music);

        startActivity(intent);
    }

    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){
            mMusclesJs = mMusclesWebviewHandler.getMusclesReadyForWebview(utilities.deleteCopiesofList(musculos));
        }
    }

    private Workout getCurrentWorkout(){
        return new Workout(workoutId,workoutName,workoutImgUrl,workoutSets,workoutLevel,mainMuscle,mExercises);
    }

    private void saveWorkout() {
        //Log.d(TAG,"save workout");
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
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
