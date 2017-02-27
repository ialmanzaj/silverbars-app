package com.app.app.silverbarsapp.presenters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.app.app.silverbarsapp.utils.CountDownController;
import com.app.app.silverbarsapp.callbacks.MusicCallback;
import com.app.app.silverbarsapp.MusicPlayer;
import com.app.app.silverbarsapp.SpotifyPlayerImpl;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter implements MusicCallback,CountDownController.CountDownEvents {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();
    
    private Context context;
    private ArrayList<ExerciseRep> exercises;

    //my view callback
    private WorkingOutView view;
    private SpotifyPlayerImpl mSpotifyPlayer;
    private MusicPlayer mLocalPlayer;

    //exercise audio 
    private MediaPlayer mExerciseAudioPlayer;

    //exercise audio constants
    private final float VOLUME_FULL = (float)(Math.log(100)/Math.log(100));;
    private final float VOLUME_HALF = (float)(Math.log(100-90)/Math.log(100));


    private int mCurrentReps;
    private int mCurrentExercisePosition = 0;
    private int mCurrentSet = 1;

    //this var for the countdown;
    private int mCurrentTempo;
    private int mCurrentRest = 0;
    private int mCurrentStartTime = 0;


    //sets in total
    private int totalSets;

    //FLAGS for the events in workout
    private boolean isWorkoutPaused = false,isWorkoutRest = false;

    //FLAGS for the coutdowns
    private boolean isInitialCountDownActive = false,isMainCountDownActive = false;

    //flag to know button workout is paused
    private boolean isButtonPauseClicked;

    //exercise audio flag
    private boolean isAudioExerciseActive;

    private CountDownController mCountDownController;

    public WorkingOutPresenter(Context context,WorkingOutView view){
        this.context = context;
        this.view = view;
    }

    public void setInitialSetup(ArrayList<ExerciseRep> exercises,boolean exerciseActive,int sets){
        this.exercises = exercises;
        isAudioExerciseActive = exerciseActive;
        totalSets = sets;
        mCountDownController = new CountDownController(this);
    }

    /**
     *    music methods
     *<p>
     *
     *
     */
    public void createSpotifyPlayer(String spotify_token, String spotify_playlist){
        if (mSpotifyPlayer == null){
            mSpotifyPlayer = new SpotifyPlayerImpl(context,spotify_token,spotify_playlist,this);
            mSpotifyPlayer.setup();
        }
    }

    public void createLocalMusicPlayer(String[] song_names,ArrayList<File> songs_files){
        if (mLocalPlayer == null){
            mLocalPlayer = new MusicPlayer(context,song_names,songs_files,this);
            mLocalPlayer.setup(0,true);
        }
    }

    private boolean isLocalPlayerAvailable(){
        return  mLocalPlayer != null;
    }

    private boolean isSpotifyPlayerAvailable(){
        return  mSpotifyPlayer != null;
    }

    public boolean isWorkoutPaused(){
        return isWorkoutPaused;
    }

    public void playMusic(){
        if (isLocalPlayerAvailable()){

            mLocalPlayer.play();
            //event ui on music playing
            view.onPlayMusic();

        }else if (isSpotifyPlayerAvailable()){

            mSpotifyPlayer.play();
            //event ui on music playing
            view.onPlayMusic();
        }
    }

    public void pauseMusic(){
        if (isLocalPlayerAvailable()){

            mLocalPlayer.pause();

            //event ui on music paused
            view.onPauseMusic();

        }else if (isSpotifyPlayerAvailable()){

            mSpotifyPlayer.pause();

            //event ui on music paused
            view.onPauseMusic();
        }
    }


    private void setupExercisePlayer(String exercise_audio_file){
            mExerciseAudioPlayer = new MediaPlayer();

            if (mExerciseAudioPlayer.isPlaying()) {
                mExerciseAudioPlayer.stop();
                mExerciseAudioPlayer.release();

                mExerciseAudioPlayer = new MediaPlayer();
            }

            try {

                String[] mp3dir = exercise_audio_file.split("/SilverbarsMp3/");
                mExerciseAudioPlayer = MediaPlayer.create(context, Uri.parse(context.getFilesDir()+"/SilverbarsMp3/"+mp3dir[1]));

            } catch (NullPointerException e) {
                Log.e(TAG,"NullPointerException",e);
            }
    }

    private void playExerciseAudio(String exercise_audio_file){
        //Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

        setupExercisePlayer(exercise_audio_file);

        mExerciseAudioPlayer.setOnPreparedListener(player -> {
            Log.e("mExerciseAudioPlayer","ready!");
          
            if (isLocalPlayerAvailable() && mLocalPlayer.isPlaying())
                mLocalPlayer.setVolume(0.04f,0.04f);

            if (isSpotifyPlayerAvailable()){
                mSpotifyPlayer.pause();
            }

            mExerciseAudioPlayer.start();
        });

        mExerciseAudioPlayer.setOnCompletionListener(mediaPlayer -> {

            if (isLocalPlayerAvailable() && mLocalPlayer.isPlaying()) {
                mLocalPlayer.setVolume(VOLUME_FULL, VOLUME_FULL);
                mediaPlayer.release();
            }

            if (isSpotifyPlayerAvailable()){
                mSpotifyPlayer.play();
            }

        });
    }

    private boolean isExerciseAudioPlayerAvailable(){
        return mExerciseAudioPlayer != null;
    }

    public void onSwipeMusicPreview(){}

    public void onSwipeMusicNext(){}


    @Override
    public void onSongName(String song_name) {
        view.updateSongName(song_name);
    }

    @Override
    public void onArtistName(String artist_name) {
        view.updateArtistName(artist_name);
    }


    /**
     *    methods of the InicialTimer
     *<p>
     *
     *
     */
    public void startInicialTimer(int seconds){
        //the initial flag on
        isInitialCountDownActive = true;

        //save the current seconds
        mCurrentStartTime = seconds;
        mCountDownController.createInicialTimer(seconds);
    }

    /**
     *    methods of the MainCountDown
     *<p>
     *
     *
     */
    private void startMainCountDown(){
        //Log.i(TAG,"startMainCountDown");

        if (!isWorkoutPaused() && !isWorkoutRest){

            mCurrentReps = getTotalRepetitionTime(mCurrentExercisePosition);

            //get the current tempo of the current exercise
            mCurrentTempo = getTempo(mCurrentExercisePosition);

            int total_seconds = (mCurrentReps * mCurrentTempo +3);
            //Log.i(TAG,"total_seconds: "+total_seconds);

            mCountDownController.createMainCountDownTimer(total_seconds,mCurrentTempo);
        }
    }


    /**
     *    methods of the RestCountDownTimer
     *<p>
     *
     *
     */
    private void startRestCountDownTimer(int rest){
        //set the actual rest var
        mCurrentRest = rest;
        int total_secs = (rest +2);

        //create timer
        mCountDownController.createRestTimer(total_secs);
    }

    /**
     *    methods with tempo and repetition logic
     *<p>
     *
     *
     */

    private int getTotalRepetitionTime(int mCurrentExercisePosition){
        if (exercises.get(mCurrentExercisePosition).getRepetition() > 0)
            return exercises.get(mCurrentExercisePosition).getRepetition();
        else
            return exercises.get(mCurrentExercisePosition).getSeconds();
    }

    private int getTempo(int mCurrentExercisePosition){
        if (exercises.get(mCurrentExercisePosition).getRepetition() > 0)
            return exercises.get(mCurrentExercisePosition).getTempo_positive()
                    + exercises.get(mCurrentExercisePosition).getTempo_isometric()
                    + exercises.get(mCurrentExercisePosition).getTempo_negative();
        else
            return 1;
    }


    /**
     *    Events when timer has finished
     *<p>
     *
     *
     */
    public void onOverlayTextListener(int  second){
        //Log.i(TAG,"onOverlayTextListener"+s);
        if (second == 0 && isMainCountDownActive){

            onRestFinished();

        } else if( second == 0 && isInitialCountDownActive) {

            onInitialTimerFinished();
        }
    }

    private void onInitialTimerFinished(){
        mCountDownController.destroyInicialTimer();

        //SET  inital countdown off
        isInitialCountDownActive = false;

        //set the main flag active
        isMainCountDownActive = true;

        //starting the countdown
        startMainCountDown();

        // overlay off
        view.onOverlayViewOff();
    }

    private void onRestFinished(){
        //Log.i(TAG,"onRestFinished");
        mCountDownController.destroyRestCountDownTimer();

        //flag rest off
        isWorkoutRest = false;


        // overlay VIEW off 
        view.onOverlayViewOff();


        //play exercise audio
        if (isAudioExerciseActive){
            //playing Exercise Audio
            playExerciseAudio(exercises.get(mCurrentExercisePosition).getExercise().getExercise_audio());
        }

        //Log.i("restlistener","startMainCountDown");
        startMainCountDown();
    }


    public void repetitionListener(int repetition){
        //Log.i(TAG,"repetitionListener"+repetition.toString());

        if (repetition == 0 && isMainCountDownActive){

            //destroy MainCount Down
            mCountDownController.destroyMainCountDownTimer();


            //on next exercise
            if ( (mCurrentExercisePosition+1) < exercises.size()){
                onChangeToNextExercise();
            } else {
               onChangeToNextSet();
            }

        }

    }

    private void onChangeToNextExercise(){
        Log.i(TAG,"next exercise");

        mCurrentExercisePosition++;

        //when the repetitions of the exercise has finished.
        view.onRepsFinished( mCurrentExercisePosition );

        //restarting the view of the workout
        restartExerciseValues();
    }

    private void onChangeToNextSet(){
        Log.i(TAG,"next set");

        // contar sets
        if (mCurrentSet+1 <= totalSets){

            //plus one more set
            mCurrentSet++;
            view.onSetFinished(mCurrentSet);

            //set the exercise in the first position
            mCurrentExercisePosition = 0;

            //restarting the view of the workout
            restartExerciseValues();

        } else
            view.onWorkoutFinished();

    }

    /**
     *    Events with the UI 
     *<p>
     *
     *
     */
    public void showRestOverlayView(int rest_time){
        //set rest flag active
        isWorkoutRest = true;

       //starting rest CountDownTimer
        startRestCountDownTimer(rest_time);

        //CHANGES UI
        view.onOverlayViewOn();
    }

    /**
     *    Events of the CountDown 
     *<p>
     *
     *
     */
    
    private void pauseCountDown(){
        //set pause flag on
        isWorkoutPaused = true;

        //resume initial countdown
        if (mCountDownController.isInicialTimerAvailable()){
            mCountDownController.pauseInicialTimer();
        }

        //pause main COUNTDOWN
        if (mCountDownController.isMainCountDownTimerAvailable()){
            mCountDownController.pauseMainCountDown();
        }

        //pause rest countdown
        if (mCountDownController.isRestCountDownTimerAvailable()){
            mCountDownController.pauseRestCountDownTimer();
        }

    }
    
    private void resumeCountDown(){
        //set pause flag off
        isWorkoutPaused = false;

        //resume initial countdown
        if (mCountDownController.isInicialTimerAvailable()){
            mCountDownController.resumeInicialTimer();
        }

        //resume main countdown
        if (mCountDownController.isMainCountDownTimerAvailable()){
            mCountDownController.resumeMainCountDown(mCurrentTempo);
        }

        //Resume rest countdown
        if (mCountDownController.isRestCountDownTimerAvailable()){
            mCountDownController.resumeRestCountDownTimer();
        }

    }
    

    /**
     *    Events of the workout
     *<p>
     *
     *
     */
    
    public void playWorkout(){
        if (isWorkoutPaused){
            Log.i(TAG,"playWorkout");

            //flag button pause clicked off
            isButtonPauseClicked = false;

            resumeCountDown();
            playMusic();

            //update view on resume workout
            view.onWorkoutResume();
        }
    }

    public void pauseWorkout(){
        if (!isWorkoutPaused){
            Log.i(TAG,"pauseWorkout");

            //flag button pause clicked on
            isButtonPauseClicked = true;

            pauseCountDown();
            pauseMusic();


            //update view on pause workout
            view.onWorkoutPaused();
        }
    }

    public void finishWorkout(){
        view.onWorkoutFinished();
    }

    /**
     *    Events of next exercise
     *<p>
     *
     *
     */
    
    public void nextExercise(){
        if (!isMainCountDownActive){
            return;
        }

        pauseWorkout();
        view.onNextExercise();
    }

    public void onNextExercisePositive(){
        Log.d(TAG,"onNextExercisePositive");

        //set to the next exercise
        mCurrentExercisePosition++;

        //changes the ui to the next exercise
        view.onChangeToExercise(mCurrentExercisePosition);
        restartExerciseValues();


        //starting again the next exercise
        playWorkout();

        //hiding or not the preview and next buttons
        //onHideUi();
    }

    private void restartExerciseValues(){
        if (exercises.get(mCurrentExercisePosition).getRepetition() > 0)
            view.onChangeExerciseValues(exercises.get(mCurrentExercisePosition).getRepetition(),
                    exercises.get(mCurrentExercisePosition).getTempo_positive(),
                    exercises.get(mCurrentExercisePosition).getTempo_isometric(),
                    exercises.get(mCurrentExercisePosition).getTempo_negative(),false);
        else
            view.onChangeExerciseValues(exercises.get(mCurrentExercisePosition).getSeconds(), 0, 0, 0,true);
    }


    private void onHideUi(){
        if (mCurrentExercisePosition == 0){
            view.hidePrevExercisebutton();
        } else if (mCurrentExercisePosition == exercises.size() -1){
            view.hideNextExercisebutton();
        }
    }

    public void onNextExerciseNegative(){
        playWorkout();
    }
    /**
     *    Events of preview exercise
     *<p>
     *
     *
     */
    public void previewExercise(){
        if (!isMainCountDownActive){
            return;
        }

        pauseWorkout();
        view.onPreviewExercise();
    }

    public void onPreviewExercisePositive(){
        Log.d(TAG,"onPreviewExercisePositive");

        //move to exercise before
        mCurrentExercisePosition--;
        view.onChangeToExercise(mCurrentExercisePosition);

        restartExerciseValues();


        //starting again the next exercise
        playWorkout();

        //hiding or not the preview and next buttons
        //onHideUi();
    }

    public void onPreviewExerciseNegative(){
        playWorkout();
    }

    @Override
    public void onTickInitialCounter() {
        mCurrentStartTime--;
        //update UI
        view.onInitialCounterStarted(String.valueOf( mCurrentStartTime ));
    }

    @Override
    public void onTickMainCounter() {
        mCurrentReps--;
        //update UI
        view.onRepetitionCountdown(String.valueOf( mCurrentReps ));
    }

    @Override
    public void onTickRestCounter() {
        mCurrentRest--;
        //UI changes
        view.onRestCounterStarted(String.valueOf( mCurrentRest ));
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart");
        playWorkout();
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        playWorkout();
    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
        pauseWorkout();
    }

    @Override
    public void onRestart() {
        Log.d(TAG,"onRestart");
        playWorkout();
    }

    @Override
    public void onStop() {
        Log.d(TAG,"onStop");
        pauseWorkout();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        cancelEverything();
    }


    private void cancelEverything(){
        mCountDownController.destroyInicialTimer();
        mCountDownController.destroyMainCountDownTimer();
        mCountDownController.destroyRestCountDownTimer();


        destroySpotifyPlayer();
        destroyExerciseAudioPlayer();
    }

    private void destroySpotifyPlayer(){
        if (isSpotifyPlayerAvailable()){mSpotifyPlayer.onDestroy();}
    }

    private void destroyExerciseAudioPlayer(){
        if (isExerciseAudioPlayerAvailable()){mExerciseAudioPlayer.release();}
    }


}
