package com.app.app.silverbarsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.app.silverbarsapp.Constants;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExerciseWorkingOutAdapter;
import com.app.app.silverbarsapp.components.DaggerWorkingOutComponent;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Metadata;
import com.app.app.silverbarsapp.modules.WorkingOutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.WorkingOutPresenter;
import com.app.app.silverbarsapp.utils.DisableTouchRecyclerListener;
import com.app.app.silverbarsapp.utils.OnSwipeTouchListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkingOutView;
import com.app.app.silverbarsapp.widgets.PausableChronometer;
import com.michaelflisar.rxbus.RXBusBuilder;
import com.michaelflisar.rxbus.rx.RXSubscriptionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


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
    @BindView(R.id.stop_workout) ImageView mStopWorkoutButton;

    @BindView(R.id.next_exercise) ImageView mNextExercisebutton;

    @BindView(R.id.option) TextView mOption;

    @BindView(R.id.weight_layout) LinearLayout mWeightLayout;
    @BindView(R.id.weight) TextView mExerciseWeight;

    @BindView(R.id.list) RecyclerView mExercisesList;
    
    @BindView(R.id.total_exercises) TextView mTotalExercises;

    @BindView(R.id.chronometer2) PausableChronometer mChronometer;
    @BindView(R.id.countdown) TextView mCountDownTimer;

    @BindView(R.id.total_timer) PausableChronometer mTotalTimerChronometer;

    @BindView(R.id.review) LinearLayout mReviewExerciseView;
    @BindView(R.id.exercise_name) TextView mExerciseName;
    @BindView(R.id.exercise_option)TextView mExerciseOption;
    @BindView(R.id.numberPicker) com.shawnlin.numberpicker.NumberPicker mNumberPicker;


    private ArrayList<ExerciseProgression> progressions;
    private ArrayList<ExerciseRep> mExercises;


    private boolean mVibrationPerSet;
    private int mSetsTotal;
    private int mWorkout_id;
    boolean isUserWorkout;

    private ArrayList<File> mSongsLocal;
    private Metadata mFirstSongMetadata;

    private boolean mReviewFinished = false;
    private int mPositionExercise;

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
        getExtras(getIntent().getExtras());
        setupAdapter();
        mPlayerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight(){mWorkingOutPresenter.previewMusic();}
            @Override
            public void onSwipeLeft() {
                mWorkingOutPresenter.nextMusic();
            }
        });
    }

    @OnClick(R.id.save_progress)
    public void saveProgress(){
        int position = Integer.parseInt(mCurrentExercisePositionText.getText().toString())-1;

        //add to the again with the user data
        progressions.set(position,addTotalProgression(progressions.get(position),position,mNumberPicker.getValue()));

        //FLAG to move to the next exercise
        mReviewFinished = true;

        //ui events
        onOverlayUiOff();
        onReviewOff();
        onChangeToExercise(mPositionExercise);
    }

    private ExerciseProgression addTotalProgression(ExerciseProgression exerciseProgression,int position,int number){
        if (utilities.checkIfRep(mExercises.get(position))) {
            int total_reps_done = exerciseProgression.getRepetitions_done() + number;
            exerciseProgression.setRepetitions_done(total_reps_done);
        }else {
            int total_second_done = exerciseProgression.getSeconds_done() + number;
            exerciseProgression.setSeconds_done(total_second_done);
        }
        return exerciseProgression;
    }

    @OnClick({ R.id.play_music,R.id.pause_music,R.id.play_workout, R.id.pause_workout,R.id.stop_workout, R.id.next_exercise,R.id.skip})
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
            case R.id.pause_workout:
                mWorkingOutPresenter.pauseWorkout();
                break;
            case R.id.stop_workout:
                mWorkingOutPresenter.stopWorkout(mChronometer.getTimeElapsed());
                break;
            case R.id.next_exercise:
                mWorkingOutPresenter.nextExercise();
                break;
            case R.id.skip:
                mWorkingOutPresenter.skipRest();
                break;
        }
    }

    private void getExtras(Bundle extras){
        mWorkout_id = extras.getInt("workout_id");
        isUserWorkout = extras.getBoolean("user_workout",false);
        mExercises = extras.getParcelableArrayList("exercises");
        mSetsTotal = extras.getInt("sets");
        int mRestByExercise = extras.getInt("rest_exercise");
        int mRestBySet = extras.getInt("rest_set");
        mVibrationPerSet = extras.getBoolean("vibration_per_set");
        boolean play_exercise_audio = extras.getBoolean("exercise_audio");

        //music extras
        int typeMusic = extras.getInt("type_music",0);
        mFirstSongMetadata = extras.getParcelable("metadata");
        mSongsLocal = (ArrayList<File>) extras.getSerializable("songs");

        //init presenter
        mWorkingOutPresenter.setInitialSetup(
                mExercises,play_exercise_audio,mSetsTotal, mRestByExercise, mRestBySet);

        //music selection
        onMusicSelection(typeMusic);

        //set new array progression to the other user data that we need
        progressions = utilities.convertToExerciseProgressions(mExercises);

        //init THE UI
        initMainUI();
    }


    /**
     *
     *
     *
     *
     *   INIT functions
     *<p>
     *
     *
     *
     *
     *
     *
     */
    private void initMainUI(){
        initExerciseUI(0);
        //set the exercises in total
        mTotalExercises.setText(String.valueOf(mExercises.size()));
        //set total sets
        mTotalSetsText.setText(String.valueOf(mSetsTotal));

        //to move the song name marquee
        mSongName.setSelected(true);
    }

    private void onMusicSelection(int typeMusic){
        if (typeMusic == Constants.MusicTypes.SPOTIFY) {
            mWorkingOutPresenter.setupSpotifyPlayer();

            //handle the events with spotify broadcast
            Subscription subscription =
                    RXBusBuilder.create(Metadata.class)
                            .subscribe(this::updateMusicUI, (Action1<Throwable>) throwable -> Log.e(TAG,"error",throwable));
            RXSubscriptionManager.addSubscription(this, subscription);

            mWorkingOutPresenter.playMusic();
            mFirstSongMetadata.setPlaying(true);
            updateMusicUI(mFirstSongMetadata);

        }else if (typeMusic == Constants.MusicTypes.LOCAL_MUSIC) {

            mWorkingOutPresenter.setupMusicPlayerLocal(mSongsLocal);

            mWorkingOutPresenter.playMusic();
            mFirstSongMetadata.setPlaying(true);
            updateMusicUI(mFirstSongMetadata);

        }else {

            //No music UI
            noMusic();
        }
    }

    private void setupAdapter(){
        mExercisesList.addOnItemTouchListener(new DisableTouchRecyclerListener());
        mExercisesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mExercisesList.setAdapter(new ExerciseWorkingOutAdapter(this,mExercises));
    }

    private void initExerciseUI(int exercise_position){
        //setup the rep or sec
        if (utilities.checkIfRep(mExercises.get(exercise_position))){
            mRepetitionTimerText.setText(String.valueOf(mExercises.get(exercise_position).getRepetition()));
            mOption.setText(getString(R.string.working_out_reps));
        }else {
            mRepetitionTimerText.setText(String.valueOf(mExercises.get(exercise_position).getSeconds()));
            mOption.setText(getString(R.string.working_out_seconds));
        }

        //check the weight
        checkWeightUI(exercise_position);

        //setup the chronometer or CountDown
        initChronometerOrCountDown(mExercises.get(exercise_position));
    }

    private void checkWeightUI(int exercise_position){
        if (mExercises.get(exercise_position).getWeight() > 0){
            mWeightLayout.setVisibility(View.VISIBLE);
            mExerciseWeight.setText(utilities.formaterDecimal(String.valueOf(mExercises.get(exercise_position).getWeight())));
        }else {
            mWeightLayout.setVisibility(View.GONE);
        }
    }

    private void initChronometerOrCountDown(ExerciseRep exercise){
        if (!utilities.checkIfRep(exercise)) {

            mChronometer.setVisibility(View.GONE);
            mCountDownTimer.setVisibility(View.VISIBLE);

            mCountDownTimer.setText(utilities.formatHMS(exercise.getSeconds()));
        }else {

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
            mExerciseOption.setText(getString(R.string.activity_workingout_review_rep));
            mNumberPicker.setMaxValue(exercise.getRepetition());
        }else {
            mExerciseOption.setText(getString(R.string.activity_workingout_review_sec));
            mNumberPicker.setMaxValue(exercise.getSeconds());
        }
    }

    private void restartChronometer(){
        mChronometer.reset();
    }

    /**
     *
     *
     *
     *
     *    Music events
     *<p>
     *
     *
     *
     *
     *
     *
     */

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

    /**
     *
     *
     *
     *
     *    Workout events
     *<p>
     *
     *
     *
     *
     *
     */
    @Override
    public void onWorkoutReady() {
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
        mPositionExercise = exercise_position_list;
        if (mReviewFinished) {
            //restarting mExercisesList to exercise_position
            mExercisesList.smoothScrollToPosition(exercise_position_list);

            //changes of exercise
            initExerciseUI(exercise_position_list);

            //hide exercise button
            hideNextExerciseButton(exercise_position_list);

            //change the current exercise indicator
            mCurrentExercisePositionText.setText(String.valueOf(exercise_position_list + 1));

            //FLAG
            mReviewFinished = false;
        }
    }

    @Override
    public void onSetFinished(int set) {
        activateVibrationPerSet();

        //restarting mExercisesList to first position
        mExercisesList.smoothScrollToPosition(0);

        //changes of exercise
        initExerciseUI(0);

        //hide exercise button
        hideNextExerciseButton(0);

        //set the current set
        mCurrentSetText.setText(String.valueOf(set));
        //restart current exercise to the first
        mCurrentExercisePositionText.setText(String.valueOf("1"));
    }

    @Override
    public void onStartChronometer() {
        mChronometer.start();

        if (mTotalTimerChronometer.getTimeElapsed() <= 0) {
            mTotalTimerChronometer.start();
        }
    }

    @Override
    public void onCountDownWorking(String second) {
        mCountDownTimer.setText(second);
    }

    @Override
    public void onStopChronometer() {
        mChronometer.stop();
    }

    @Override
    public void onResumeWorkout() {
        onPlayWorkoutUI();
    }

    @Override
    public void onPauseWorkout() {
        onPauseWorkoutUI();
    }

    @Override
    public void onFinishWorkout() {
        onScreenOff();
        launchResultsActivity();
    }

    private void launchResultsActivity() {
        Intent intent = new Intent(this, ResultsActivity.class);

        intent.putParcelableArrayListExtra("exercises",setTotalTimeToExercises(mWorkingOutPresenter.getExercises(),progressions));
        intent.putExtra("sets", mSetsTotal);
        intent.putExtra("current_set", Integer.parseInt(mCurrentSetText.getText().toString()));
        intent.putExtra("exercises_completed", Integer.parseInt(mCurrentExercisePositionText.getText().toString()));
        intent.putExtra("total_time", utilities.formatHMS(mTotalTimerChronometer.getTimeElapsed()));
        intent.putExtra("workout_id", mWorkout_id);
        intent.putExtra("user_workout",isUserWorkout);

        startActivity(intent);
        finish();
    }

    private ArrayList<ExerciseProgression> setTotalTimeToExercises(ArrayList<ExerciseRep> exercises,
                                                                   ArrayList<ExerciseProgression> exercises_progresions){
        for (int position = 0;position<exercises.size();position++){
            //set the total time

            String total_time = exercises.get(position).getTimes_per_set() == null ? "0": exercises.get(position).getTimes_per_set();
            exercises_progresions.get(position).setTotal_time(total_time);

            //set the set completed
            int sets_completed;

            if (total_time.toLowerCase().contains("_")){
                sets_completed = total_time.toLowerCase().split("_").length;
            }else if (Objects.equals(total_time, "0")){
                sets_completed = 0;
            } else {
                sets_completed = total_time.isEmpty() ? 0 : 1;
            }

            exercises_progresions.get(position).setSets_completed(sets_completed);
        }
        return exercises_progresions;
    }

    @Override
    public void onBackPressed(){
        dialogTOExitWorkout();
    }

    private void dialogTOExitWorkout(){
        new MaterialDialog.Builder(this)
                .title(getString(R.string.activity_workingout_dialog_title))
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content(getString(R.string.activity_workingout_dialog_content))
                .positiveText(getResources().getString(R.string.positive_dialog))
                .onPositive((dialog, which) -> {
                    onScreenOff();
                    launchResultsActivity();
                }).negativeText(getResources().getString(R.string.negative_dialog)).
                onNegative((dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }


    /**
     *
     *
     *
     *
     *
     *    UI events
     *<p>
     *
     *
     *
     *
     *
     *
     *
     */

    public void updateMusicUI(Metadata metadata) {
        if (metadata.isPlaying()) {
            onPlayMusic();
        } else {
            onPauseMusic();
        }

        if (metadata.getArtistName() != null) {
            updateArtistName(metadata.getArtistName());
        }

        if (metadata.getTrackName() != null) {
            updateSongName(metadata.getTrackName());
        }
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

        //disable nextMusic button workout
        mNextExercisebutton.setEnabled(false);
    }

    private void onOverlayUiOff(){
        //enable workout nextMusic
        mNextExercisebutton.setEnabled(true);

        //play button enable
        mPlayWorkoutButton.setEnabled(true);
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

    private void onReviewOn(){
        mReviewExerciseView.setVisibility(View.VISIBLE);
    }

    private void onReviewOff(){
        mReviewExerciseView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    private void destroy(){
        onScreenOff();
        RXSubscriptionManager.unsubscribe(this);
    }

}
