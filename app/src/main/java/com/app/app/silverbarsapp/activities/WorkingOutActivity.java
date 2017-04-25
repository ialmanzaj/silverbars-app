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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExerciseWorkingOutAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkingOutComponent;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.modules.WorkingOutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkingOutPresenter;
import com.app.app.silverbarsapp.utils.DisableTouchRecyclerListener;
import com.app.app.silverbarsapp.utils.OnSwipeTouchListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkingOutView;
import com.app.app.silverbarsapp.widgets.PausableChronometer;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class WorkingOutActivity extends BaseActivity implements WorkingOutView{

    private static final String TAG = WorkingOutActivity.class.getSimpleName();

    private Utilities utilities = new Utilities();

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

    @BindView(R.id.play_workout) ImageView mPlayWorkoutButton;
    @BindView(R.id.pause_workout) ImageView mPauseWorkoutButton;
    @BindView(R.id.stop_workout) RelativeLayout mStopWorkoutButton;

    @BindView(R.id.next_exercise) ImageView mNextExercisebutton;

    @BindView(R.id.option) TextView mOption;

    @BindView(R.id.weight_layout) LinearLayout mWeightLayout;
    @BindView(R.id.weight) TextView mExerciseWeight;

    @BindView(R.id.list) RecyclerView list;
    
    @BindView(R.id.total_exercises) TextView mTotalExercises;

    @BindView(R.id.chronometer2) PausableChronometer mChronometer;
    @BindView(R.id.countdown) TextView mCountDownTimer;

    @BindView(R.id.total_timer) PausableChronometer mTotalTimerChronometer;

    @BindView(R.id.review) LinearLayout mReviewExerciseView;
    @BindView(R.id.exercise_name) TextView mExerciseName;
    @BindView(R.id.exercise_option)TextView mExerciseOption;
    @BindView(R.id.numberPicker) com.shawnlin.numberpicker.NumberPicker mNumberPicker;


    private ArrayList<ExerciseRep> mExercises;

    private boolean mVibrationPerSet;
    private int mSetsTotal;
    private int mWorkout_id;

    private ArrayList<ExerciseProgression> progressions;

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
    }

    @OnClick({ R.id.play_music,R.id.pause_music,R.id.play_workout, R.id.pause_workout,R.id.stop_workout, R.id.next_exercise})
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
                Log.i(TAG,"play workout");
                mWorkingOutPresenter.playWorkout();
                break;
            case R.id.pause_workout:
                Log.i(TAG,"pause_workout");
                mWorkingOutPresenter.pauseWorkout();
                break;
            case R.id.stop_workout:
                Log.i(TAG,"stop_workout");
                stopWorkout();
                break;
            case R.id.next_exercise:
                Log.i(TAG,"next_exercise");
                mWorkingOutPresenter.nextExercise();
                break;
        }
    }

    @OnClick(R.id.save_progress)
    public void saveProgress(){
        int index = Integer.parseInt(mCurrentExercisePositionText.getText().toString())-1;

        //get the last exercise and get the reps or second completed
        ExerciseProgression exerciseProgression = progressions.get(index);


        //set the reps or seconds
        if (utilities.checkIfRep(mExercises.get(index))) {
            exerciseProgression.setRepetitions_done(mNumberPicker.getValue());
        }else {
            exerciseProgression.setSeconds_done(mNumberPicker.getValue());
        }

        //add to the list again with the user data
        progressions.set(index,exerciseProgression);


        //ui events
        onOverlayUiOff();
        onReviewOff();
    }

    private void getExtras(Bundle extras){
        mWorkout_id = extras.getInt("workout_id");
        mExercises = extras.getParcelableArrayList("exercises");
        mSetsTotal = extras.getInt("sets");

        int mRestByExercise = extras.getInt("rest_exercise");
        int mRestBySet = extras.getInt("rest_set");

        Log.i(TAG,"mRestByExercise: "+ mRestByExercise);
        Log.i(TAG,"RestBySet: "+ mRestBySet);

        mVibrationPerSet = extras.getBoolean("vibration_per_set");

        boolean play_exercise_audio = extras.getBoolean("exercise_audio");

        //local music
        ArrayList<File> local_playlist = (ArrayList) extras.getParcelableArrayList("playlist_local");
        String[] local_song_names = extras.getStringArray("songs_local");

        //spotify music
        String spotify_playlist = extras.getString("spotify_playlist");
        String spotify_token = extras.getString("spotify_token");

        //init presenter
        mWorkingOutPresenter.setInitialSetup(
                getExerciseWithTimesArray(mExercises),play_exercise_audio,mSetsTotal, mRestByExercise, mRestBySet);

        //music setup and UI
        setupMusic(local_playlist,local_song_names,spotify_playlist,spotify_token);

        //init THE UI
        initUI();


        //set new array progression to the other user data that we need
        progressions = getProgression(mExercises);
    }


    private ArrayList<ExerciseProgression> getProgression(ArrayList<ExerciseRep> exercises){
        ArrayList<ExerciseProgression> exerciseProgressions = new ArrayList<>();

        for (ExerciseRep exercise: exercises) {
            ExerciseProgression exerciseProgression = new ExerciseProgression();
            exerciseProgression.setExercise(exercise.getExercise());
            exerciseProgression.setTotal_repetition(exercise.getRepetition());
            exerciseProgression.setTotal_seconds(exercise.getSeconds());
            exerciseProgression.setTotal_weight(exercise.getWeight());
            exerciseProgressions.add(exerciseProgression);
        }

        return exerciseProgressions;
    }
    private ArrayList<ExerciseRep> getExerciseWithTimesArray(ArrayList<ExerciseRep> exercises){
        for (ExerciseRep exercise: exercises){
            exercise.createTimesPerSet(mSetsTotal);
        }

        return exercises;
    }

    private void initUI(){
        initExerciseRepOrSecond(0);
        //set the exercises in total
        mTotalExercises.setText(String.valueOf(mExercises.size()));
        //set total sets
        mTotalSetsText.setText(String.valueOf(mSetsTotal));
    }

    private void setupAdapter(){
        // disables scolling
        list.addOnItemTouchListener(new DisableTouchRecyclerListener());
        // list en linear
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Crear  nuevo adaptador
        list.setAdapter(new ExerciseWorkingOutAdapter(this,mExercises));
    }

    private void setupMusic(ArrayList<File> local_playlist,String[] local_song_names,String spotify_playlist,String spotify_token){
        if (local_playlist != null && local_song_names != null){
            //creating local player
            mWorkingOutPresenter.createLocalMusicPlayer(local_song_names,local_playlist);
        }else if (spotify_playlist != null && spotify_token != null){
            //creating spotify player
            mWorkingOutPresenter.createSpotifyPlayer(spotify_token,spotify_playlist);
        }else {

            //No music UI
            noMusic();
        }
    }


    private void initExerciseRepOrSecond(int exercise_position){
        //setup the rep or sec
        if (utilities.checkIfRep(mExercises.get(exercise_position))){
            mRepetitionTimerText.setText(String.valueOf(mExercises.get(exercise_position).getRepetition()));
            mOption.setText(getString(R.string.working_out_reps));
        }else {
            mRepetitionTimerText.setText(String.valueOf(mExercises.get(exercise_position).getSeconds()));
            mOption.setText(getString(R.string.working_out_seconds));
        }

        //check the weight
        checkWeight(exercise_position);


        //setup the chronometer or CountDown
        initChronometerOrCountDown(mExercises.get(exercise_position));
    }

    private void checkWeight(int exercise_position){
        if (mExercises.get(exercise_position).getWeight() > 0){
            mWeightLayout.setVisibility(View.VISIBLE);
            mExerciseWeight.setText(utilities.formaterDecimal(String.valueOf(mExercises.get(exercise_position).getWeight())));
        }else {
            mWeightLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void initChronometerOrCountDown(ExerciseRep exercise){
        if (!utilities.checkIfRep(exercise)) {
            //hide mChronometer
            mChronometer.setVisibility(View.GONE);
            mCountDownTimer.setVisibility(View.VISIBLE);

            //init mCountDownTimer
            mCountDownTimer.setText(utilities.formatHMS(exercise.getSeconds()));
        }else {
            //hide mCountDownTimer
            mCountDownTimer.setVisibility(View.GONE);
            mChronometer.setVisibility(View.VISIBLE);

            //changes in the chronometer
            restartChronometer();
        }
    }


    private void setupPicker(ExerciseRep exercise){
        mExerciseName.setText(exercise.getExercise().getExercise_name());
        mNumberPicker.setMinValue(0);

        if (utilities.checkIfRep(exercise)) {
            mExerciseOption.setText("Repetition completed: ");
            mNumberPicker.setMaxValue(exercise.getRepetition());
        }else {
            mExerciseOption.setText("Second completed: ");
            mNumberPicker.setMaxValue(exercise.getSeconds());
        }
    }


    private void startChronometer(){
        Log.d(TAG,"startChronometer");
        mChronometer.start();


        if (mTotalTimerChronometer.getTimeElapsed() <= 0) {
            mTotalTimerChronometer.start();
        }
    }

    private void restartChronometer(){
        Log.d(TAG,"restartChronometer");
        mChronometer.reset();
    }

    private void stopChronometer(){
        Log.d(TAG,"stopChronometer");
        mChronometer.stop();
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
        Log.d(TAG,"onPauseMusic");
        onPauseMusicPlayerUI();
        onScreenOff();
    }

    @Override
    public void onPlayMusic() {
        Log.d(TAG,"onPlayMusic");
        onPlayMusicPlayerUI();
        onScreenOn();
    }

    @Override
    public void onWorkoutReady() {
        Log.d(TAG,"onWorkoutReady");
        mPlayWorkoutButton.setVisibility(View.VISIBLE);
        mStopWorkoutButton.setVisibility(View.INVISIBLE);
        mPauseWorkoutButton.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onRestCounterStarted(String second) {
        mOverlayTextCounter.setText(second);
    }

    @Override
    public void onRestFinished() {
        onOverlayUIOn();
        setupPicker(mExercises.get(Integer.parseInt(mCurrentExercisePositionText.getText().toString()) -1));
        onReviewOn();
    }


    @Override
    public void onOverlayViewOn() {
        mModalOverlayView.setVisibility(View.VISIBLE);
        mHeaderTextOverlay.setText(getResources().getString(R.string.rest_text));

       onOverlayUIOn();
    }

    @Override
    public void onOverlayViewOff() {
        onOverlayUiOff();
        onScreenOn();

        //overlay gone
        mModalOverlayView.setVisibility(View.GONE);
    }



    @Override
    public void onChangeToExercise(int exercise_position_list) {
        //restarting list to exercise_position
        list.smoothScrollToPosition(exercise_position_list);

        //changes of exercise
        initExerciseRepOrSecond(exercise_position_list);

        //hide exercise button
        hideNextExerciseButton(exercise_position_list);

        //change the current exercise indicator
        mCurrentExercisePositionText.setText(String.valueOf(exercise_position_list+1));
    }

    @Override
    public void onSetFinished(int set) {
        activateVibrationPerSet();

        //restarting list to first position
        list.smoothScrollToPosition(0);

        //changes of exercise
        initExerciseRepOrSecond(0);

        //hide exercise button
        hideNextExerciseButton(0);

        //set the current set
        mCurrentSetText.setText(String.valueOf(set));
        //restart current exercise to the first
        mCurrentExercisePositionText.setText(String.valueOf("1"));
    }


    @Override
    public void onStartChronometer() {
        startChronometer();
    }

    @Override
    public void onCountDownWorking(String second) {
        mCountDownTimer.setText(second);
    }

    @Override
    public void onStopChronometer() {
        stopChronometer();
    }

    @Override
    public void onResumeWorkout() {
        Log.d(TAG,"onResumeWorkout");
        onPlayWorkoutUI();
        onPlayMusicPlayerUI();
    }

    @Override
    public void onPauseWorkout() {
        Log.d(TAG,"onPauseWorkout");
        onPauseWorkoutUI();
        onPauseMusicPlayerUI();
    }

    @Override
    public void onFinishWorkout() {
        Log.d(TAG,"onFinishWorkout");

        onScreenOff();
        launchResultsActivity();
/*
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

                }).negativeText(getResources().getString(R.string.negative_dialog)).
                onNegative((dialog, which) -> {
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

    private void stopWorkout(){
        Log.d(TAG,"stopWorkout");
        saveTimesOfChronometerPerExercise();
        mWorkingOutPresenter.pauseWorkout();
        mWorkingOutPresenter.startRest();
    }

    private void saveTimesOfChronometerPerExercise(){
        Log.d(TAG,"time: "+mChronometer.getTimeElapsed());
        mWorkingOutPresenter.saveTime(mChronometer.getTimeElapsed());
    }

    private void launchResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);

        intent.putParcelableArrayListExtra("exercises", progressions);
        intent.putExtra("sets",mSetsTotal);
        intent.putExtra("sets_completed",Integer.parseInt(mCurrentSetText.getText().toString()));
        intent.putExtra("total_time",utilities.formatHMS(mTotalTimerChronometer.getTimeElapsed()));
        intent.putExtra("workout_id",mWorkout_id);

        startActivity(intent);
        finish();
    }

    private void hideNextExerciseButton(int exercise_position){
        if (exercise_position+1 == mExercises.size()){
            mNextExercisebutton.setVisibility(View.GONE);
        }else {
            mNextExercisebutton.setVisibility(View.VISIBLE);
        }
    }

    private void noMusic(){
        mPlayerLayout.setVisibility(View.GONE);
        mSongName.setText(getResources().getString(R.string.no_song));
    }

    private void onPauseWorkoutUI(){
        mPauseWorkoutButton.setVisibility(View.INVISIBLE);
        mStopWorkoutButton.setVisibility(View.INVISIBLE);
        mPlayWorkoutButton.setVisibility(View.VISIBLE);
    }

    private void onPlayWorkoutUI(){
        mStopWorkoutButton.setVisibility(View.VISIBLE);
        mPauseWorkoutButton.setVisibility(View.VISIBLE);
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

    private void onOverlayUIOn(){
        //disable play button
        mPlayWorkoutButton.setEnabled(false);

        //disable next button workout
        mNextExercisebutton.setEnabled(false);
    }

    private void onOverlayUiOff(){
        //enable workout next
        mNextExercisebutton.setEnabled(true);

        //play button enable
        mPlayWorkoutButton.setEnabled(true);
    }

    private void onReviewOn(){
        mReviewExerciseView.setVisibility(View.VISIBLE);
    }

    private void onReviewOff(){
        mReviewExerciseView.setVisibility(View.GONE);
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
