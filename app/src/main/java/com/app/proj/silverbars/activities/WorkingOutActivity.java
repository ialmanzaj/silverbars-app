package com.app.proj.silverbars.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.adapters.ExerciseWorkingOutAdapter;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.presenters.WorkingOutPresenter;
import com.app.proj.silverbars.utils.OnSwipeTouchListener;
import com.app.proj.silverbars.utils.RecyclerViewTouch;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnTextChanged;


public class WorkingOutActivity extends AppCompatActivity implements View.OnClickListener,WorkingOutView{

    private static final String TAG = WorkingOutActivity.class.getSimpleName();



    WorkingOutPresenter mWorkingOutPresenter;


    @BindView(R.id.PlayerLayout) LinearLayout mPlayerLayout;

    @BindView(R.id.play_music) ImageButton mPlayMusicbutton;
    @BindView(R.id.pause_music) ImageButton mPauseMusicbutton;

    @BindView(R.id.repetition_timer) TextView mRepetitionTimerText;
    
    @BindView(R.id.song_name) TextView mSongName;
    @BindView(R.id.artist_name) TextView mArtistName;
    
    @BindView(R.id.current_set) TextView mCurrentSetText;
    @BindView(R.id.current_exercise) TextView mCurrentExerciseText;
    
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
    

    ArrayList<File> mySongsList;
    ArrayList<File> playlist;
    
    private Uri url_music;


    private boolean Songs_from_Phone = false, finish = false, INITIAL_TIMER = false,main = false,MAIN_TIMER = false;
    

    
    private boolean VibrationPerSet = false,VibrationPerRep = false, pause=false,rest = false;

    private int exercises_size = 0;
    
    private int RestByExercise = 0,RestBySet = 0;
    
    String spotify_playlist;

    private String mSpotifyToken;

    Boolean Music_Spotify = false,play_exercise_audio = false,BUTTON_PAUSE = false,onPause = false;

    ArrayList<ExerciseRep> exercises = new ArrayList<>();
    
    private Utilities utilities;

    ExerciseWorkingOutAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);


        Bundle extras = getIntent().getExtras();

        setExtras(extras);

        list.addOnItemTouchListener(new RecyclerViewTouch()); // disables scolling
        // list en linear
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ExerciseWorkingOutAdapter(this,exercises);
        list.setAdapter(adapter);




        // inicialize tempo
        tempo = exercises.get(y).getTempo_positive() 
                + exercises.get(y).getTempo_isometric() 
                + exercises.get(y).getTempo_negative();

        
        
        
        // spotify inicialization
        if (spotify_playlist != null && mSpotifyToken != null){
            configPlayerSpotify(mSpotifyToken);
        }


        elements = adapter.getItemCount();
        mCurrentExerciseText.setText("1");

        mTotalExercises.setText(String.valueOf(elements));
        totalSet.setText(String.valueOf(TotalSets));

        totalSet.setText(String.valueOf(TotalSets));
        mCurrentSetText.setText("0");


        // Inicialize variables
        exercises_size = exercises.size();
        playlist = new ArrayList<>();

        // contadores de descanso y repeticiones actuales
        actualReps = exercises.get(0).getRepetition();
        actualRest = RestByExercise;
        

        //positivie,mNegativeText and mIsometricText
        mPositiveText.setText(String.valueOf(exercises.get(0).getTempo_positive()));
        mIsometricText.setText(String.valueOf(exercises.get(0).getTempo_isometric()));
        mNegativeText.setText(String.valueOf(exercises.get(0).getTempo_negative()));

        mNextExercisebutton.setVisibility(View.VISIBLE);

        mPlayMusicbutton.setOnClickListener(this);
        mPauseMusicbutton.setOnClickListener(this);
        
        mSongName.setSelected(true);

        //repetiton text
     

        //inicializar rep text
        mRepetitionTimerText.setText(String.valueOf(exercises.get(0).getRepetition()));

        mPlayerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {

                onMusicNext();
            }
            @Override
            public void onSwipeLeft() {

                onMusicPreview();

            }
        });

        
        if (adapter.getItemCount() <= 1){
            mPreviewExerciseButton.setVisibility(View.GONE);
            mNextExercisebutton.setVisibility(View.GONE);
        }


        mWorkingOutPresenter.startInicialTimer(actual_start_time);
    }

    private void setExtras(Bundle extras){
        exercises = extras.getParcelableArrayList("exercises");
        RestByExercise =  extras.getInt("RestByExercise");
        RestBySet = extras.getInt("RestBySet");

        VibrationPerRep = extras.getBoolean("VibrationPerRep");
        VibrationPerSet =  extras.getBoolean("VibrationPerSet");

        play_exercise_audio = extras.getBoolean("play_exercise_audio");
        TotalSets = extras.getInt("Sets");
        mySongsList = (ArrayList) extras.getParcelableArrayList("songlist");
        spotify_playlist = extras.getString("playlist_spotify");
        mSpotifyToken = extras.getString("token");
        String[] song_names = extras.getStringArray("songs");
    }



    @OnTextChanged(R.id.rest_counter)
    public void TextChanged(CharSequence charSequence){
        mWorkingOutPresenter.restOperationLogic(charSequence);
    }

    @OnTextChanged(R.id.repetition_timer)
    public void onTextChanged(CharSequence charSequence){
        mWorkingOutPresenter.repetitionOperationLogic(charSequence);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.play_music:
                break;
            case R.id.pause_music:
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
        mPlayMusicbutton.setEnabled(false);
        mPlayMusicbutton.setClickable(false);
        mPauseMusicbutton.setEnabled(false);
        mPauseMusicbutton.setClickable(false);
    }

    private void launchResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putParcelableArrayListExtra("exercises", exercises);
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
    public void displayModalRest() {
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
    public void onNextExercise() {
        mNextExercisebutton.setVisibility(View.GONE);
    }

    @Override
    public void onPreviewExercise() {
        mPreviewExerciseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRepsFinished(int exercise_position) {
        mPreviewExerciseButton.setVisibility(View.VISIBLE);

        list.smoothScrollToPosition(exercise_position);
        mCurrentExerciseText.setText(String.valueOf(exercise_position+1));


        ActivateVibrationPerSet();
        mRepetitionTimerText.setEnabled(false);

        mWorkingOutPresenter.showRestModal(RestByExercise);// descanso por ejercicio
    }

    @Override
    public void onSetFinished(int set) {

        mCurrentSetText.setText(String.valueOf(set));
        mCurrentExerciseText.setText(String.valueOf("1"));

        list.smoothScrollToPosition(0);

        mWorkingOutPresenter.showRestModal(RestBySet);//descanso por set

        if (exercises_size > 1){
            mNextExercisebutton.setVisibility(View.VISIBLE);
            mPreviewExerciseButton.setVisibility(View.GONE);
        }


    }

    @Override
    public void onWorkoutFinished() {
        onScreenOff();

        launchResultsActivity();
    }

    private void onScreenOn(){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void onScreenOff(){getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void ActivateVibrationPerRep(){
        if (VibrationPerRep) {
            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (!pause){
                vb.vibrate(250);
            }else {
                vb.cancel();
            }
        }
    }

    private void ActivateVibrationPerSet(){
        if (VibrationPerSet){
            Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
            if (!pause){
                vb.vibrate(1000);
            }else {
                vb.cancel();
            }
        }
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
    public void onBackPressed(){
        Log.d(TAG,"onBackPressed");
        mWorkingOutPresenter.finishWorkout();
    }

    @Override
    protected void onDestroy() {
        onScreenOn();
        super.onDestroy();
    }


}
