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
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.app.silverbarsapp.Constants;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExerciseAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkoutComponent;
import com.app.app.silverbarsapp.handlers.MusclesWebviewHandler;
import com.app.app.silverbarsapp.handlers.SpotifyHandler;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Metadata;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.WorkoutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkoutPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkoutView;
import com.michaelflisar.rxbus.RXBusBuilder;
import com.michaelflisar.rxbus.rx.RXSubscriptionManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscription;
import rx.functions.Action1;

import static com.app.app.silverbarsapp.Constants.BroadcastTypes.METADATA_CHANGED;
import static com.app.app.silverbarsapp.Constants.BroadcastTypes.PLAYBACK_STATE_CHANGED;
import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;


/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutActivity extends BaseActivity implements WorkoutView{

    private static final String TAG = WorkoutActivity.class.getSimpleName();

    private static final int LOCAL_MUSIC = 1;

    @Inject
    WorkoutPresenter mWorkoutPresenter;


    @BindView(R.id.toolbar) Toolbar myToolbar;
    @BindView(R.id.sets) TextView Sets;
    @BindView(R.id.rest_by_set) AutoCompleteTextView RestbySet;
    @BindView(R.id.rest_by_exercise) AutoCompleteTextView RestbyExercise;
    @BindView(R.id.exercises) RecyclerView mExercisesList;
    @BindView(R.id.webview) WebView webview;
    @BindView(R.id.toggle_save_workout) RelativeLayout mSaveWorkoutLayout;
    @BindView(R.id.save_workout_local) SwitchCompat mSaveWorkoutSwitch;
    @BindView(R.id.voice_per_exercise)SwitchCompat mVoicePerExercise;
    @BindView(R.id.start_button) Button mStartButton;
    @BindView(R.id.select_music) LinearLayout mSelectMusicButton;

    //@BindView(R.id.skills)RecyclerView mSkillsList;
    @BindView(R.id.music)TextView mTypeOfMusicText;
    @BindView(R.id.main_muscle) TextView mMainMuscle;


    private ArrayList<ExerciseRep> mExercises;

    boolean isUserWorkout;
    private int workoutId = 0, workoutSets = 0;
    private String workoutName, workoutLevel, mainMuscle, workoutImgUrl;

    boolean isDownloadAudioExerciseActive = false;
    private int mTypeMusic;

    private Utilities utilities = new Utilities();
    private MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();

    private SpotifyHandler mSpotifyHandler;
    private String mMusclesJs;

    //for local songs
    private ArrayList<File> mLocalSongs;
    private Metadata mFirstSongMetadata;

    private MixpanelAPI mixpanel;

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

        //get the extras
        getExtras(getIntent().getExtras());
        setupToolbar();
        //setupTabs();
        setupWebview();
        setupAdapter();

        addSpotifyListener();

        mixpanel = MixpanelAPI.getInstance(this, MIX_PANEL_TOKEN);
    }

    private void getExtras(Bundle extras){
        workoutId = extras.getInt("workout_id", 0);
        workoutName = extras.getString("name");
        workoutImgUrl = extras.getString("image");
        workoutSets = extras.getInt("sets", 1);
        workoutLevel = extras.getString("level");
        mainMuscle = extras.getString("main_muscle");
        mExercises =  getIntent().getParcelableArrayListExtra("exercises");
        isUserWorkout = extras.getBoolean("user_workout", false);

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
        mMainMuscle.setText(mainMuscle);

        //mVoicePerExercise.setOnCheckedChangeListener((compoundButton, isChecked) -> {});

        mStartButton.setOnClickListener(view -> {
            LaunchWorkingOutActivity();
            mixpanelEventStartWorkout();
        });

        mSelectMusicButton.setOnClickListener(v -> dialogMusic());

        try {
            onSaveWorkout();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTabs(){
       /* //Defining Tabs
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

       *//* TabHost.TabSpec skills = mTabLayout.newTabSpec("Focus");
        skills.setIndicator("Focus");
        skills.setContent(R.id.skills);*//*

        mTabLayout.addTab(overview);
        mTabLayout.addTab(exercises);
        mTabLayout.addTab(muscles);
        //mTabLayout.addTab(skills);*/
    }

    private void setupWebview(){
        webview.getSettings().setJavaScriptEnabled(true);
        utilities.loadBodyFromLocal(this,webview);
        mMusclesWebviewHandler.addWebviewClientPaint(webview,mMusclesJs);
    }

    private void setupAdapter(){
        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setNestedScrollingEnabled(false);
        mExercisesList.setHasFixedSize(false);
    }

    @Override
    public void onWorkout(boolean created) {}

    private void setExercisesInAdapter(ArrayList<ExerciseRep> exercises){
        List<String> muscles = new ArrayList<>();

        for (ExerciseRep exerciseRep: exercises){for (MuscleExercise muscle:  exerciseRep.getExercise().getMuscles()){muscles.add(muscle.getMuscle());}}

        mExercisesList.setAdapter(new ExerciseAdapter(this,exercises));
        setMusclesToView(muscles);
    }

    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){
            mMusclesJs = mMusclesWebviewHandler.getMusclesReadyForWebview(utilities.deleteCopiesofList(musculos));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCAL_MUSIC && resultCode == RESULT_OK){
            if (data.hasExtra("songs")){

                ArrayList<File> local_songs = (ArrayList<File>) data.getSerializableExtra("songs");

                setCurrentSongMetadata(
                        new Metadata(utilities.getSongName(this,local_songs.get(0)),
                        utilities.SongArtist(this,local_songs.get(0))));

                onLocalMusicSelected(local_songs);
            }
        }
    }

    /**
     *
     *
     *
     *     Music selection
     *
     *
     *
     *
     */

    private void addSpotifyListener(){
        mSpotifyHandler = new SpotifyHandler(this);

        Subscription subscription_playstate = RXBusBuilder.create(Metadata.class)
                .withKey(PLAYBACK_STATE_CHANGED)
                .subscribe(this::onSpotifySelected, (Action1<Throwable>) throwable -> Log.e(TAG,"error",throwable));

        Subscription subscription_song_metadata = RXBusBuilder.create(Metadata.class)
                .withKey(METADATA_CHANGED)
                .subscribe(this::setCurrentSongMetadata, (Action1<Throwable>) throwable -> Log.e(TAG,"error",throwable));

        RXSubscriptionManager.addSubscription(this, subscription_playstate);
        RXSubscriptionManager.addSubscription(this, subscription_song_metadata);
    }

    private void setCurrentSongMetadata(Metadata metadata){
        mFirstSongMetadata = metadata;
    }

    private void dialogMusic(){
        //mix panel
        mixpanelEventMusicDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(getString(R.string.activity_workout_dialog_title))
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .customView(R.layout.music_view_dialog,true);
        MaterialDialog dialog = builder.build();
        dialog.show();

        View dialog_view = dialog.getCustomView();
        if (dialog_view != null) {
            LinearLayout spotify = (LinearLayout) dialog_view.findViewById(R.id.spotify);
            LinearLayout local_music = (LinearLayout) dialog_view.findViewById(R.id.local_music);

            spotify.setOnClickListener(v -> {
                dialog.dismiss();
                mSpotifyHandler.launch();
                mixpanelEventSpotify();
            });

            local_music.setOnClickListener(v -> {
                dialog.dismiss();
                launchLocalMusic();
                mixpanelEventLocalMusic();
            });
        }
    }

    private void launchLocalMusic(){
        startActivityForResult(new Intent(this, SongsActivity.class),LOCAL_MUSIC);
    }

    private void onLocalMusicSelected(ArrayList<File> local_songs){
        //update the ui
        mTypeOfMusicText.setText(getString(R.string.music_dialog_my_music));

        //which music is selected
        mTypeMusic = Constants.MusicTypes.LOCAL_MUSIC;

        //set the local song
        mLocalSongs = local_songs;

        //mix panel events
        mixpanelEventLocalMusicSelected();
    }


    private void onSpotifySelected(Metadata metadata){
        if (mSpotifyHandler.isSpotifyLaunched()) {
            mSpotifyHandler.stopMusic();

            //bring my app to the front
            bringMyApp(metadata.getContext());

            //update the ui
            mTypeOfMusicText.setText(getString(R.string.music_dialog_spotify));

            //set selection spotify
            mTypeMusic = Constants.MusicTypes.SPOTIFY;

            //flag off
            mSpotifyHandler.setSpotifyLaunched(false);

            //mix panel events
            mixpanelEventSpotifySelected();
        }
    }

    private void bringMyApp(Context context){
        Intent intent = new Intent(context, WorkoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    private void LaunchWorkingOutActivity() {
        Intent intent = new Intent(this, WorkingOutActivity.class);

        intent.putParcelableArrayListExtra("exercises", mExercises);
        intent.putExtra("sets",Integer.parseInt(Sets.getText().toString()));
        intent.putExtra("rest_exercise",Integer.parseInt(RestbyExercise.getText().toString()));
        intent.putExtra("rest_set",Integer.parseInt(RestbySet.getText().toString()));

        //vibrations option
        intent.putExtra("vibration_per_set",true);

        //exercise audio option
        intent.putExtra("exercise_audio",isDownloadAudioExerciseActive);

        intent.putExtra("workout_id",workoutId);
        intent.putExtra("user_workout",isUserWorkout);

        //songs
        intent.putExtra("type_music",mTypeMusic);
        intent.putExtra("metadata",mFirstSongMetadata);
        intent.putExtra("songs",mLocalSongs);
        

        startActivity(intent);
    }

    private Workout getCurrentWorkout(){
        return new Workout(workoutId,workoutName,workoutImgUrl,workoutSets,workoutLevel,mainMuscle,mExercises);
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

    private void saveWorkout() {
        try {

            if (!mWorkoutPresenter.isWorkoutExist(workoutId)){

                //save in database
                mWorkoutPresenter.saveWorkout(getCurrentWorkout());

            } else {
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

    @Override
    protected void onDestroy() {
        RXSubscriptionManager.unsubscribe(this);
        super.onDestroy();
    }


    /**
     *
     *
     *
     *    Mix panel events
     *
     *
     *
     */

    private void mixpanelEventStartWorkout(){
        mixpanel.track("Start Workout", utilities.getUserData(this));
    }

    private void mixpanelEventMusicDialog(){
        mixpanel.track("Music dialog", utilities.getUserData(this));
    }

    private void mixpanelEventSpotify(){
        mixpanel.track("on Spotify", utilities.getUserData(this));
    }

    private void mixpanelEventSpotifySelected(){
        mixpanel.track("spotify Selected", utilities.getUserData(this));
    }

    private void mixpanelEventLocalMusic(){
        mixpanel.track("on LocalMusic", utilities.getUserData(this));
    }

    private void mixpanelEventLocalMusicSelected(){
        mixpanel.track("localMusic Selected", utilities.getUserData(this));
    }

}
