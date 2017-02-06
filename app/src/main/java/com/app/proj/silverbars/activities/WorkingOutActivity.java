package com.app.proj.silverbars.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.adapters.ExerciseWorkingOutAdapter;
import com.app.proj.silverbars.components.DaggerWorkingOutComponent;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.modules.WorkingOutModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.WorkingOutPresenter;
import com.app.proj.silverbars.utils.DisableTouchRecyclerListener;
import com.app.proj.silverbars.utils.OnSwipeTouchListener;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnTextChanged;


public class WorkingOutActivity extends BaseActivity implements View.OnClickListener,WorkingOutView{

    private static final String TAG = WorkingOutActivity.class.getSimpleName();


    @Inject
    WorkingOutPresenter mWorkingOutPresenter;




    @BindView(R.id.player_layout) LinearLayout mPlayerLayout;

    @BindView(R.id.play_music) ImageButton mPlayMusicbutton;
    @BindView(R.id.pause_music) ImageButton mPauseMusicbutton;

    @BindView(R.id.repetition_timer) TextView mRepetitionTimerText;
    
    @BindView(R.id.song_name) TextView mSongName;
    @BindView(R.id.artist_name) TextView mArtistName;
    
    @BindView(R.id.current_set) TextView mCurrentSetText;
    @BindView(R.id.total_sets) TextView mTotalSetsText;
    @BindView(R.id.current_exercise) TextView mCurrentExercisePositionText;
    
    @BindView(R.id.rest_counter) TextView mRestCounter;
    @BindView(R.id.headerText) TextView headerText;

    @BindView(R.id.modal_overlay) LinearLayout mModalOverlayView;
    
    @BindView(R.id.finish_workout) ImageButton mFinishWorkoutButton;
    @BindView(R.id.play_workout) ImageButton mPlayWorkoutButton;
    @BindView(R.id.pause_workout) ImageButton mPauseWorkoutButton;

    @BindView(R.id.positive) TextView mPositiveText;
    @BindView(R.id.negative) TextView mNegativeText;
    @BindView(R.id.isometric) TextView mIsometricText;

    @BindView(R.id.prev_exercise) ImageButton mPreviewExerciseButton;
    @BindView(R.id.next_exercise) ImageButton mNextExercisebutton;

    @BindView(R.id.list) RecyclerView list;
    
    @BindView(R.id.total_exercises) TextView mTotalExercises;

    private boolean mVibrationPerSet = false,mVibrationPerRep = false;

    private int mRestByExercise = 30,RestBySet = 60;

    ArrayList<ExerciseRep> mExercises = new ArrayList<>();

    private Utilities utilities = new Utilities();

    @Override
    protected int getLayout() {
        return R.layout.activity_working_out;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mWorkingOutPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerWorkingOutComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .workingOutModule(new WorkingOutModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Bundle extras = getIntent().getExtras();

        setExtras(extras);

        // disables scolling
        list.addOnItemTouchListener(new DisableTouchRecyclerListener());
        // list en linear
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Crear  nuevo adaptador
        list.setAdapter(new ExerciseWorkingOutAdapter(this,mExercises));




        mPlayerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {

                mWorkingOutPresenter.onSwipeMusicNext();
            }
            @Override
            public void onSwipeLeft() {

                mWorkingOutPresenter.onSwipeMusicPreview();
            }
        });
        


        mWorkingOutPresenter.startInicialTimer(5);
    }



    private void setExtras(Bundle extras){
        mExercises = extras.getParcelableArrayList("exercises");


        //int sets = extras.getInt("Sets");

        //mRestByExercise =  extras.getInt("mRestByExercise");
        //RestBySet = extras.getInt("RestBySet");

        mVibrationPerRep = extras.getBoolean("VibrationPerRep");
        mVibrationPerSet =  extras.getBoolean("VibrationPerSet");

        boolean play_exercise_audio = extras.getBoolean("play_exercise_audio");

        //local music
        ArrayList<File> local_playlist = (ArrayList) extras.getParcelableArrayList("songlist");
        String[] local_song_names = extras.getStringArray("songs");


        //spotify music
        String spotify_playlist = extras.getString("playlist_spotify");
        String spotify_token = extras.getString("token");


        //init presenter
        mWorkingOutPresenter.setInitialSetup(mExercises,play_exercise_audio);


        if (local_playlist != null && local_song_names != null){

            //creating local player
            mWorkingOutPresenter.createLocalMusicPlayer(local_song_names,local_playlist);

        }else if (spotify_playlist != null && spotify_token != null){

            //creating spotify playe
            mWorkingOutPresenter.createSpotifyPlayer(spotify_token,spotify_playlist);
        }else {

            //No music UI
            zeroMusic();
        }



        initUI();
    }


    private void initUI(){

        mNextExercisebutton.setVisibility(View.VISIBLE);


        //inicializar rep text
        mRepetitionTimerText.setText(String.valueOf(mExercises.get(0).getRepetition()));


        mCurrentExercisePositionText.setText("1");

        mTotalExercises.setText(String.valueOf(mExercises.size()));

        //set current set
        mCurrentSetText.setText("1");
        //mTotalSetsText.setText(sets);



        //init values tempo
        mPositiveText.setText(String.valueOf(mExercises.get(0).getTempo_positive()));
        mIsometricText.setText(String.valueOf(mExercises.get(0).getTempo_isometric()));
        mNegativeText.setText(String.valueOf(mExercises.get(0).getTempo_negative()));
    }

    @OnTextChanged(R.id.rest_counter)
    public void restListener(CharSequence rest){
        mWorkingOutPresenter.restlistener(rest);
    }


    @OnTextChanged(R.id.repetition_timer)
    public void repTimer(CharSequence repetition){
        mWorkingOutPresenter.repetitionListener(repetition);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.play_music:
                mWorkingOutPresenter.playMusic();
                break;
            case R.id.pause_music:
                mWorkingOutPresenter.pauseMusic();
                break;
            case R.id.play_workout:
                mWorkingOutPresenter.playWorkout();
                break;
            case R.id.finish_workout:
                mWorkingOutPresenter.finishWorkout();
                break;
            case R.id.pause_workout:
                mWorkingOutPresenter.pauseWorkout();
                break;
            case R.id.prev_exercise:
                mWorkingOutPresenter.dialogPreviewExercise();
                break;
            case R.id.next_exercise:
                mWorkingOutPresenter.dialogNextExercise();
                break;
        }
    }


    private void zeroMusic(){
        mPlayerLayout.setVisibility(View.GONE);
        mSongName.setText(getResources().getString(R.string.no_song));
    }

    private void launchResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putParcelableArrayListExtra("mExercises", mExercises);
        startActivity(intent);
        finish();
    }

    private void onPauseMusicPlayerUI(){
        mPlayMusicbutton.setVisibility(View.VISIBLE);
        mPauseMusicbutton.setVisibility(View.GONE);
    }

    private void onPlayMusicPlayerUI(){
        mPlayMusicbutton.setVisibility(View.GONE);
        mPauseMusicbutton.setVisibility(View.VISIBLE);
    }


    @Override
    public void updateSongName(String song_name) {
        mSongName.setText(utilities.removeLastMp3(song_name));
    }

    @Override
    public void updateArtistName(String artist_name) {
        mArtistName.setText(artist_name);
    }

    @Override
    public void onPauseMusic() {

        mPauseWorkoutButton.setVisibility(View.GONE);
        mPlayWorkoutButton.setVisibility(View.VISIBLE);
        mFinishWorkoutButton.setVisibility(View.VISIBLE);

        onScreenOff();
    }

    @Override
    public void onPlayMusic() {

        mPauseWorkoutButton.setVisibility(View.VISIBLE);
        mPlayWorkoutButton.setVisibility(View.GONE);
        mFinishWorkoutButton.setVisibility(View.GONE);

        onScreenOn();
    }

    @Override
    public void onOverlayViewOn() {

        //disable workout next and previews
        mNextExercisebutton.setEnabled(false);
        mPreviewExerciseButton.setEnabled(false);


        mModalOverlayView.setVisibility(View.VISIBLE);
        headerText.setText(getResources().getString(R.string.rest_text));
    }

    @Override
    public void onRestCounterStarted(String second) {
        mRestCounter.setText(second);
    }

    @Override
    public void onMainCounterStarted(String second) {
        mRestCounter.setText(second);
    }

    @Override
    public void onRepetitionCountdown(String second) {
        mRepetitionTimerText.setText(second);
        ActivateVibrationPerRep();
    }

    @Override
    public void onNextExerciseUI() {
        mNextExercisebutton.setVisibility(View.GONE);
    }

    @Override
    public void onPreviewExerciseUI() {
        mPreviewExerciseButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onChangeToExercise(int exercise_position) {
        list.smoothScrollToPosition(exercise_position);
        mCurrentExercisePositionText.setText(String.valueOf(exercise_position+1));
    }


    @Override
    public void onRepsFinished(int exercise_position) {

        // descanso por ejercicio
        mWorkingOutPresenter.showRestOverlayView(mRestByExercise);


        //move the list recycler to this position
        list.smoothScrollToPosition(exercise_position);
        mCurrentExercisePositionText.setText(String.valueOf(exercise_position+1));
    }


    @Override
    public void onSetFinished(int set) {

        mCurrentSetText.setText(String.valueOf(set));
        mCurrentExercisePositionText.setText(String.valueOf("1"));


        //restarting list to first position
        list.smoothScrollToPosition(0);


        mWorkingOutPresenter.showRestOverlayView(RestBySet);//descanso por set

        if (mExercises.size() > 1){
            mNextExercisebutton.setVisibility(View.VISIBLE);
            mPreviewExerciseButton.setVisibility(View.GONE);
        }

    }



    @Override
    public void onOverlayViewOff() {

        mNextExercisebutton.setEnabled(true);
        mPreviewExerciseButton.setEnabled(true);


        mModalOverlayView.setVisibility(View.GONE);

        onScreenOn();
    }

    @Override
    public void onWorkoutRestart(int rep,int tempo_positive,int tempo_isometric,int tempo_negative) {

        mRepetitionTimerText.setText(String.valueOf(rep));
        mPositiveText.setText(String.valueOf(tempo_positive));
        mIsometricText.setText(String.valueOf(tempo_isometric));
        mNegativeText.setText(String.valueOf(tempo_negative));
    }

    @Override
    public void onWorkoutResume() {
    }

    @Override
    public void onWorkoutPaused() {
    }

    @Override
    public void onWorkoutFinished() {
        onScreenOff();

        launchResultsActivity();
    }

    private void onScreenOn(){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void onScreenOff(){getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void ActivateVibrationPerRep(){
        if (mVibrationPerRep) {
            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (!mWorkingOutPresenter.isWorkoutPaused()){
                vb.vibrate(250);
            }else {
                vb.cancel();
            }
        }
    }

    private void ActivateVibrationPerSet(){
        if (mVibrationPerSet){
            Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
            if (!mWorkingOutPresenter.isWorkoutPaused()){

                vb.vibrate(1000);


            }else {
                vb.cancel();
            }
        }
    }


    @Override
    public void onBackPressed(){
        mWorkingOutPresenter.finishWorkout();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        onScreenOff();
        super.onDestroy();
    }





}
