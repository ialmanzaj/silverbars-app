package com.app.proj.silverbarsapp.activities;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.proj.silverbarsapp.R;
import com.app.proj.silverbarsapp.SilverbarsApp;
import com.app.proj.silverbarsapp.adapters.ExerciseWorkingOutAdapter;
import com.app.proj.silverbarsapp.components.DaggerWorkingOutComponent;
import com.app.proj.silverbarsapp.models.ExerciseRep;
import com.app.proj.silverbarsapp.modules.WorkingOutModule;
import com.app.proj.silverbarsapp.presenters.BasePresenter;
import com.app.proj.silverbarsapp.presenters.WorkingOutPresenter;
import com.app.proj.silverbarsapp.utils.DisableTouchRecyclerListener;
import com.app.proj.silverbarsapp.utils.OnSwipeTouchListener;
import com.app.proj.silverbarsapp.utils.Utilities;
import com.app.proj.silverbarsapp.viewsets.WorkingOutView;

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

    @BindView(R.id.play_music) ImageButton mPlayMusicbutton;
    @BindView(R.id.pause_music) ImageButton mPauseMusicbutton;

    @BindView(R.id.repetition_timer) TextView mRepetitionTimerText;
    
    @BindView(R.id.song_name) TextView mSongName;
    @BindView(R.id.artist_name) TextView mArtistName;
    
    @BindView(R.id.current_set) TextView mCurrentSetText;
    @BindView(R.id.sets_total) TextView mTotalSetsText;
    @BindView(R.id.current_exercise) TextView mCurrentExercisePositionText;

    @BindView(R.id.modal_overlay) LinearLayout mModalOverlayView;
    @BindView(R.id.rest_counter) TextView mOverlayTextCounter;
    @BindView(R.id.headerText) TextView mHeaderTextOverlay;

    @BindView(R.id.play_workout) ImageButton mPlayWorkoutButton;
    @BindView(R.id.pause_workout) ImageButton mPauseWorkoutButton;
    @BindView(R.id.finish_workout) ImageButton mFinishWorkoutButton;

    @BindView(R.id.positive) TextView mPositiveText;
    @BindView(R.id.negative) TextView mNegativeText;
    @BindView(R.id.isometric) TextView mIsometricText;

    @BindView(R.id.prev_exercise) ImageButton mPreviewExerciseButton;
    @BindView(R.id.next_exercise) ImageButton mNextExercisebutton;

    @BindView(R.id.list) RecyclerView list;
    
    @BindView(R.id.total_exercises) TextView mTotalExercises;

    private Utilities utilities = new Utilities();
    ArrayList<ExerciseRep> mExercises = new ArrayList<>();

    private boolean mVibrationPerSet = false,mVibrationPerRep = false;
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

        mSetsTotal = extras.getInt("sets");
        //mRestByExercise =  extras.getInt("mRestByExercise");
        //RestBySet = extras.getInt("RestBySet");

        //Log.i(TAG,"mRestByExercise: "+mRestByExercise);
        //Log.i(TAG,"RestBySet: "+RestBySet);

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

        mNextExercisebutton.setVisibility(View.VISIBLE);

        //inicializar rep text
        mRepetitionTimerText.setText(String.valueOf(mExercises.get(0).getRepetition()));

        mCurrentExercisePositionText.setText("1");
        mTotalExercises.setText(String.valueOf(mExercises.size()));

        //set current set
        mCurrentSetText.setText("1");
        mTotalSetsText.setText(String.valueOf(mSetsTotal));

        //init values tempo
        mPositiveText.setText(String.valueOf(mExercises.get(0).getTempo_positive()));
        mIsometricText.setText(String.valueOf(mExercises.get(0).getTempo_isometric()));
        mNegativeText.setText(String.valueOf(mExercises.get(0).getTempo_negative()));
    }

    @OnTextChanged(R.id.rest_counter)
    public void restListener(CharSequence second){
        mWorkingOutPresenter.onOverlayTextListener(Integer.parseInt(second.toString()));
    }


    @OnTextChanged(R.id.repetition_timer)
    public void repTimer(CharSequence repetition){
        mWorkingOutPresenter.repetitionListener(Integer.parseInt(repetition.toString()));
    }

   @OnClick({ R.id.play_music,R.id.pause_music,R.id.play_workout,R.id.finish_workout, R.id.pause_workout, R.id.prev_exercise, R.id.next_exercise})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.play_music:
                Log.i(TAG,"play_music");
                mWorkingOutPresenter.playMusic();
                break;
            case R.id.pause_music:
                Log.i(TAG,"pause_music");
                mWorkingOutPresenter.pauseMusic();
                break;
            case R.id.play_workout:
                Log.i(TAG,"play workout");
                mWorkingOutPresenter.playWorkout();
                break;
            case R.id.finish_workout:
                Log.i(TAG,"finish_workout");
                mWorkingOutPresenter.finishWorkout();
                break;
            case R.id.pause_workout:
                Log.i(TAG,"pause_workout");
                mWorkingOutPresenter.pauseWorkout();
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
        //disable workout next and previews
        mNextExercisebutton.setEnabled(false);
        mPreviewExerciseButton.setEnabled(false);


        mModalOverlayView.setVisibility(View.VISIBLE);
        mHeaderTextOverlay.setText(getResources().getString(R.string.rest_text));
    }

    @Override
    public void onRestCounterStarted(String second) {
        mOverlayTextCounter.setText(second);
    }

    @Override
    public void onInitialCounterStarted(String second) {
        mOverlayTextCounter.setText(second);
    }

    @Override
    public void onRepetitionCountdown(String repetition) {
        //Log.i(TAG,"repetition: "+repetition);
        mRepetitionTimerText.setText(repetition);
        activateVibrationPerRep();
    }


    @Override
    public void hideNextExercisebutton() {
        mNextExercisebutton.setVisibility(View.GONE);
    }

    @Override
    public void showNextExercisebutton() {
        mNextExercisebutton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPrevExercisebutton() {
        mPreviewExerciseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePrevExercisebutton() {
        mPreviewExerciseButton.setVisibility(View.GONE);
    }

    @Override
    public void onNextExercise() {
        String title = "Desea pasar al siguiente ejercicio?";
        String content = "Esta seguro que quiere pasar al siguiente ejercicio?";

        new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .backgroundColor(Color.BLACK)
                .positiveText(getResources().getString(R.string.positive_dialog))
                .onPositive((dialog, which) -> {

                    dialog.dismiss();
                    mWorkingOutPresenter.onNextExercisePositive();

                })
                .negativeText(getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> {

                    dialog.dismiss();
                    mWorkingOutPresenter.onNextExerciseNegative();

                }).show();

    }

    @Override
    public void onPreviewExercise() {

        String title = "Desea regresar al ejercicio anterior?";
        String content = "Esta seguro que quiere regresar al ejercicio anterior?";

        new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .backgroundColor(Color.BLACK)
                .positiveText(getResources().getString(R.string.positive_dialog))
                .onPositive((dialog, which) -> {

                    dialog.dismiss();
                    mWorkingOutPresenter.onPreviewExercisePositive();

                })
                .negativeText(getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> {
                    dialog.dismiss();

                    dialog.dismiss();
                    mWorkingOutPresenter.onPreviewExerciseNegative();

                }).show();
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

        activateVibrationPerSet();

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
    public void onChangeExerciseValues(int rep, int tempo_positive, int tempo_isometric, int tempo_negative) {
        mRepetitionTimerText.setText(String.valueOf(rep));
        mPositiveText.setText(String.valueOf(tempo_positive));
        mIsometricText.setText(String.valueOf(tempo_isometric));
        mNegativeText.setText(String.valueOf(tempo_negative));
    }


    @Override
    public void onWorkoutResume() {
        onPlayWorkoutUI();
        onPlayMusicPlayerUI();
    }

    @Override
    public void onWorkoutPaused() {
        onPauseWorkoutUI();
        onPauseMusicPlayerUI();
    }

    @Override
    public void onWorkoutFinished() {

    /*    new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.title_dialog))
                .titleColor(context.getResources().getColor(R.color.colorPrimaryText))
                .contentColor(context.getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(context.getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(context.getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content(context.getResources().getString(R.string.content_dialog))
                .positiveText(context.getResources().getString(R.string.positive_dialog))

                .onPositive((dialog, which) -> {

                   *//* dialog.dismiss();


                    //canceling music player
                    cancelLocalMusicPlayer();


                   view.onWorkoutFinished();*//*

                })
                .negativeText(context.getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> {

                   *//* dialog.dismiss();

                    if (!isButtonPauseClicked){

                        resumeCountDown();
                        startLocalMusicPlayer();
                    }
*//*

                }).show();*/

        onScreenOff();
        launchResultsActivity();
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
        mPlayWorkoutButton.setVisibility(View.VISIBLE);
        mPauseWorkoutButton.setVisibility(View.GONE);
    }

    private void onPlayWorkoutUI(){
        mPlayWorkoutButton.setVisibility(View.GONE);
        mPauseWorkoutButton.setVisibility(View.VISIBLE);
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

    private void activateVibrationPerRep(){
        if (mVibrationPerRep) {
            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (!mWorkingOutPresenter.isWorkoutPaused()){
                vb.vibrate(250);
            }else {
                vb.cancel();
            }
        }
    }

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
