package com.app.app.silverbarsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.PausableChronometer;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExerciseWorkingOutAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkingOutComponent;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.modules.WorkingOutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkingOutPresenter;
import com.app.app.silverbarsapp.utils.DisableTouchRecyclerListener;
import com.app.app.silverbarsapp.utils.OnSwipeTouchListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;


public class WorkingOutActivity extends BaseActivity implements WorkingOutView{

    private static final String TAG = WorkingOutActivity.class.getSimpleName();

    @Inject
    WorkingOutPresenter mWorkingOutPresenter;

    @BindView(R.id.player_layout) LinearLayout mPlayerLayout;

    @BindView(R.id.play_music) ImageView mPlayMusicbutton;
    @BindView(R.id.pause_music) ImageView mPauseMusicbutton;

    @BindView(R.id.repetition_timer) TextView mRepetitionTimerText;
    
    @BindView(R.id.song_name) TextView mSongName;
    @BindView(R.id.artist_name) TextView mArtistName;
    
    @BindView(R.id.current_set) TextView mCurrentSetText;
    @BindView(R.id.sets_total) TextView mTotalSetsText;
    @BindView(R.id.current_exercise) TextView mCurrentExercisePositionText;

    @BindView(R.id.modal_overlay) LinearLayout mModalOverlayView;
    @BindView(R.id.rest_counter) TextView mOverlayTextCounter;
    @BindView(R.id.headerText) TextView mHeaderTextOverlay;

    @BindView(R.id.play_workout) Button mPlayWorkoutButton;
    @BindView(R.id.pause_workout) RelativeLayout mStopWorkoutButton;

    @BindView(R.id.prev_exercise) ImageView mPreviewExerciseButton;
    @BindView(R.id.next_exercise) ImageView mNextExercisebutton;

    @BindView(R.id.option) TextView mOptionRepOrSecond;

    @BindView(R.id.list) RecyclerView list;
    
    @BindView(R.id.total_exercises) TextView mTotalExercises;

    @BindView(R.id.chronometer2) PausableChronometer mChronometer2;

    private Utilities utilities = new Utilities();
    ArrayList<ExerciseRep> mExercises = new ArrayList<>();

    private boolean mVibrationPerSet = false;

    private int mSetsTotal = 1;
    private int mRestByExercise = 30,RestBySet = 60;


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

        //get extras from activity before
        getExtras(getIntent().getExtras());


        setupAdapter();


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
        

       // mWorkingOutPresenter.startInicialTimer(5);
    }



    private void setupAdapter(){

        // disables scolling
        list.addOnItemTouchListener(new DisableTouchRecyclerListener());
        // list en linear
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Crear  nuevo adaptador
        list.setAdapter(new ExerciseWorkingOutAdapter(this,mExercises));
    }


    private void getExtras(Bundle extras){
        mExercises = extras.getParcelableArrayList("exercises");

        mSetsTotal = extras.getInt("sets");
        //mRestByExercise =  extras.getInt("rest_exercise");
        //RestBySet = extras.getInt("rest_set");

        Log.i(TAG,"mRestByExercise: "+mRestByExercise);
        Log.i(TAG,"RestBySet: "+RestBySet);

        mVibrationPerSet = extras.getBoolean("VibrationPerSet");


        boolean play_exercise_audio = extras.getBoolean("exercise_audio");


        //local music
        ArrayList<File> local_playlist = (ArrayList) extras.getParcelableArrayList("playlist_local");
        String[] local_song_names = extras.getStringArray("songs_local");

        //spotify music
        String spotify_playlist = extras.getString("spotify_playlist");
        String spotify_token = extras.getString("spotify_token");


        //init presenter
        mWorkingOutPresenter.setInitialSetup(mExercises,play_exercise_audio,mSetsTotal);


        if (local_playlist != null && local_song_names != null){

            //creating local player
            mWorkingOutPresenter.createLocalMusicPlayer(local_song_names,local_playlist);

        }else if (spotify_playlist != null && spotify_token != null){

            //creating spotify player
            mWorkingOutPresenter.createSpotifyPlayer(spotify_token,spotify_playlist);
        }else {

            //No music UI
            zeroMusic();
        }


        initUI();
    }


    private void initUI(){

        //inicializar rep text
        if (mExercises.get(0).getRepetition() > 0){
            mRepetitionTimerText.setText(String.valueOf(mExercises.get(0).getRepetition()));
        }else {
            mRepetitionTimerText.setText(String.valueOf(mExercises.get(0).getSeconds()));
        }

        mCurrentExercisePositionText.setText("1");
        mTotalExercises.setText(String.valueOf(mExercises.size()));

        //set current set
        mCurrentSetText.setText("1");
        mTotalSetsText.setText(String.valueOf(mSetsTotal));

    }

    @OnTextChanged(R.id.rest_counter)
    public void restListener(CharSequence second){
        mWorkingOutPresenter.onOverlayTextListener(Integer.parseInt(second.toString()));
    }

    @OnTextChanged(R.id.repetition_timer)
    public void repTimer(CharSequence repetition){
        mWorkingOutPresenter.repetitionListener(Integer.parseInt(repetition.toString()));
    }

   @OnClick({ R.id.play_music,R.id.pause_music,R.id.play_workout, R.id.pause_workout, R.id.prev_exercise, R.id.next_exercise})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.play_music:
                //Log.i(TAG,"play_music");
                mWorkingOutPresenter.playMusic();
                break;
            case R.id.pause_music:
                //Log.i(TAG,"pause_music");
                mWorkingOutPresenter.pauseMusic();
                break;
            case R.id.play_workout:
                //Log.i(TAG,"play workout");
                startChronometer();
                break;
            case R.id.pause_workout:
                //Log.i(TAG,"pause_workout");
                stopChronometer();
                break;
            case R.id.prev_exercise:
                Log.i(TAG,"prev_exercise");
                mWorkingOutPresenter.previewExercise();
                break;
            case R.id.next_exercise:
                Log.i(TAG,"next_exercise");
                mWorkingOutPresenter.nextExercise();
                break;
        }
    }

    private void startChronometer(){
        onResumeWorkout();
        mChronometer2.start();
    }


    private void restart(){
        mChronometer2.reset();
    }


    private void stopChronometer(){
        onPauseWorkout();
        mChronometer2.stop();
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
        onPauseMusicPlayerUI();
        onScreenOff();
    }

    @Override
    public void onPlayMusic() {
        onPlayMusicPlayerUI();
        onScreenOn();
    }

    @Override
    public void onOverlayViewOn() {
        mModalOverlayView.setVisibility(View.VISIBLE);
        mHeaderTextOverlay.setText(getResources().getString(R.string.rest_text));

        //play button disable
        mPlayWorkoutButton.setEnabled(false);

        //disable workout next and previews
        mNextExercisebutton.setEnabled(false);
        mPreviewExerciseButton.setEnabled(false);
    }

    @Override
    public void onOverlayViewOff() {
        //enable workout next and previews
        mNextExercisebutton.setEnabled(true);
        mPreviewExerciseButton.setEnabled(true);

        //play button enable
        mPlayWorkoutButton.setEnabled(true);

        //overlay gone
        mModalOverlayView.setVisibility(View.GONE);

        onScreenOn();
    }

    @Override
    public void onRestCounterStarted(String second) {
        mOverlayTextCounter.setText(second);
    }

    @Override
    public void onNextExercise() {
        Log.d(TAG,"onNextExercise");
        mWorkingOutPresenter.onNextExercisePositive();
        restart();
    }

    @Override
    public void onPreviewExercise() {
        mWorkingOutPresenter.onPreviewExercisePositive();
    }

    @Override
    public void onChangeToExercise(int exercise_position) {
        Log.d(TAG,"onChangeToExercise");

        // descanso por ejercicio
        mWorkingOutPresenter.showRestOverlayView(mRestByExercise);

        list.smoothScrollToPosition(exercise_position);
        mCurrentExercisePositionText.setText(String.valueOf(exercise_position+1));
    }


    @Override
    public void onSetFinished(int set) {
        activateVibrationPerSet();

        mCurrentSetText.setText(String.valueOf(set));
        mCurrentExercisePositionText.setText(String.valueOf("1"));

        //restarting list to first position
        list.smoothScrollToPosition(0);

        //descanso por set
        mWorkingOutPresenter.showRestOverlayView(RestBySet);
    }


    @Override
    public void updateToExercise(int repOrSecond, boolean isSecond) {
        //reps text
        mRepetitionTimerText.setText(String.valueOf(repOrSecond));

        if (isSecond){
            mOptionRepOrSecond.setText("Seconds");
        }else {
            mOptionRepOrSecond.setText("Reps");
        }
    }


    @Override
    public void onWorkoutReady() {
        mPlayWorkoutButton.setVisibility(View.VISIBLE);
        mStopWorkoutButton.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onResumeWorkout() {
       // Log.d(TAG,"onResumeWorkout");
        onPlayWorkoutUI();
        onPlayMusicPlayerUI();
    }

    @Override
    public void onPauseWorkout() {
        //Log.d(TAG,"onPauseWorkout");
        onPauseWorkoutUI();
        onPauseMusicPlayerUI();
    }

    @Override
    public void onFinishWorkout() {

        onScreenOff();
        launchResultsActivity();


         /*new MaterialDialog.Builder(this)
                 .title(getResources().getString(R.string.title_dialog))
                 .backgroundColor(Color.WHITE)
                 .content(getResources().getString(R.string.content_dialog))
                 .positiveText(getResources().getString(R.string.positive_dialog))
                 .onPositive((dialog, which) -> {

                 })
                 .negativeText(getResources().getString(R.string.negative_dialog))
                 .onNegative((dialog, which) -> {

                     dialog.dismiss();

                 }).show();*/
    }

    @Override
    public void onBackPressed(){
        mWorkingOutPresenter.finishWorkout();
    }

    @Override
    protected void onDestroy() {
        onScreenOff();
        super.onDestroy();
    }

    private void zeroMusic(){
        mPlayerLayout.setVisibility(View.GONE);
        mSongName.setText(getResources().getString(R.string.no_song));
    }

    private void launchResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putParcelableArrayListExtra("exercises", mExercises);
        startActivity(intent);
        finish();
    }

    private void onPauseWorkoutUI(){
        mStopWorkoutButton.setVisibility(View.INVISIBLE);
        mPlayWorkoutButton.setVisibility(View.VISIBLE);
    }


    private void onPlayWorkoutUI(){
        mStopWorkoutButton.setVisibility(View.VISIBLE);
        mPlayWorkoutButton.setVisibility(View.INVISIBLE);
    }


    private void onPauseMusicPlayerUI(){
        mPlayMusicbutton.setVisibility(View.VISIBLE);
        mPauseMusicbutton.setVisibility(View.GONE);
    }

    private void onPlayMusicPlayerUI(){
        mPlayMusicbutton.setVisibility(View.GONE);
        mPauseMusicbutton.setVisibility(View.VISIBLE);
    }

    private void onScreenOn(){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void onScreenOff(){getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}


    private void activateVibrationPerSet(){
        if (mVibrationPerSet){
            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (!mWorkingOutPresenter.isWorkoutPaused()){

                vb.vibrate(1000);


            }else {
                vb.cancel();
            }
        }
    }

}
