package com.app.proj.silverbars.presenters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

import com.app.proj.silverbars.MusicCallback;
import com.app.proj.silverbars.MusicPlayer;
import com.app.proj.silverbars.SpotifyPlayerImpl;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter  implements MusicCallback {

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


    //the main countdowns to use 
    private CountDownTimer sMainCountDownTimer, mRestTimer, mStartedInitialTimer;

    private int mCurrentRest = 0;
    private int mCurrentStartTime = 0;

    private int mCurrentSet = 1;

    private int actualReps;
    private int exercise_position = 0;

    //this var for the countdown;
    private int mCurrentTempo;

    //save the the seconds to finish the coutdown
    private int mCurrentSecondsMainTimer,mCurrentSecondsRestTimer,mCurrentSecondInitialTimer;

    //sets in total
    private int totalSets;

    //FLAGS for the events in workout
    private boolean isWorkoutPaused = false,isWorkoutRest = false;

    //FLAGS for the coutdowns
    private boolean isInitialCountDownActive = false,isMainCountDownActive = false;


    //flag to know button workout is paused
    private boolean isButtonPauseClicked;

    private boolean isAudioExerciseActive;

    public WorkingOutPresenter(Context context,WorkingOutView view){
        this.context = context;
        this.view = view;
    }

    public void setInitialSetup(ArrayList<ExerciseRep> exercises,boolean exerciseActive,int sets){
        this.exercises = exercises;
        isAudioExerciseActive = exerciseActive;
        totalSets = sets;
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
                Log.e(TAG,"Exception",e);
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

        //set the current seconds
        mCurrentStartTime = seconds;


        createInicialTimer(seconds);
    }

    private void createInicialTimer(int seconds){
        //current second default to 0
        mCurrentSecondInitialTimer = 0;

        long total_sec = (seconds +4) * 1000;

        mStartedInitialTimer = new CountDownTimer(total_sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                //save current second to resume-pause
                mCurrentSecondInitialTimer  = Math.round(millisUntilFinished * 0.001f);


                mCurrentStartTime--;

                //update UI
                view.onInitialCounterStarted(String.valueOf(mCurrentStartTime));
            }
            public void onFinish() {}
        }.start();
    }

    private boolean isInicialTimerAvailable(){
        return mStartedInitialTimer != null;
    }

    private void pauseInicialTimer(){
        mStartedInitialTimer.cancel();
    }

    private void resumeInicialTimer(){
        createInicialTimer(mCurrentSecondInitialTimer);
    }


    /**
     *    methods of the MainCountDown
     *<p>
     *
     *
     */
    private void startMainCountDown(){
        //Log.i(TAG,"startMainCountDown");

        if (!isButtonPauseClicked && !isWorkoutRest){

            actualReps = getTotalRepetitionTime(exercise_position);
            //Log.i(TAG,"actualReps: "+actualReps);

            //get the current tempo of the current exercise
            mCurrentTempo = getTempo(exercise_position);


            int total_seconds = (actualReps * mCurrentTempo +3);
            //Log.i(TAG,"total_seconds: "+total_seconds);

            createMainCountDownTimer(total_seconds);
        }
    }



    private void createMainCountDownTimer(int seconds){
        mCurrentSecondsMainTimer = 0;

        //Log.i(TAG,"createMainCountDownTimer"+seconds_total);
        //Log.i(TAG,"mCurrentSecondsMainTimer "+mCurrentSecondsMainTimer);

        long seconds_total = seconds * 1000;
        //Log.i(TAG,"total_seconds "+seconds_total);
        long interval =  mCurrentTempo * 1000;
        //Log.i(TAG,"interval "+interval);


        sMainCountDownTimer = new CountDownTimer(seconds_total, interval) {
            public void onTick(long millisUntilFinished) {
                
                //save current second to resume-pause
                mCurrentSecondsMainTimer  = Math.round(millisUntilFinished * 0.001f);
                //Log.i(TAG,"mCurrentSecondsMainTimer "+mCurrentSecondsMainTimer);

                //restar reps de texto mostrado
                actualReps--;
                view.onRepetitionCountdown(String.valueOf(actualReps));
            }
            public void onFinish() {}
        }.start();
    }

    private boolean isMainCountDownTimerAvailable(){
        return sMainCountDownTimer != null;
    }

    private void pauseMainCountDown(){
        sMainCountDownTimer.cancel();
    }


    private void resumeMainCountDown(){
        createMainCountDownTimer(mCurrentSecondsMainTimer);
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
        
        int total_secs = (rest +1);

        //Log.i(TAG,"mCurrentRest"+mCurrentRest);
        //Log.i(TAG,"total_secs"+total_secs);

        //create timer
        createRestTimer(total_secs);
    }

    private void createRestTimer(int seconds){
        //init mCurrentSecondsRestTimer
        mCurrentSecondsRestTimer = 0;

        //Log.i(TAG,"createRestTimer: "+seconds);

        long seconds_total = seconds * 1000;

        mRestTimer = new CountDownTimer(seconds_total, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i(TAG,"mRestTimer "+millisUntilFinished);

                mCurrentSecondsRestTimer  = Math.round(millisUntilFinished * 0.001f);
                //Log.i(TAG,"mCurrentSecondsRestTimer "+mCurrentSecondsRestTimer);

                //restar 
                mCurrentRest--;

                //UI changes
                view.onRestCounterStarted(String.valueOf(mCurrentRest));
            }
            public void onFinish() {}
        }.start();
    }
    

    private boolean isRestCountDownTimerAvailable(){
        return mRestTimer != null;
    }

    private void pauseRestCountDownTimer(){
        mRestTimer.cancel();
    }

    private void resumeRestCountDownTimer(){
        createRestTimer(mCurrentSecondsRestTimer);
    }

    /**
     *    methods with tempo and repetition logic
     *<p>
     *
     *
     */

    private int getTotalRepetitionTime(int exercise_position){
        return exercises.get(exercise_position).getRepetition();
    }

    private int getTempo(int exercise_position){
        return exercises.get(exercise_position).getTempo_positive()
                + exercises.get(exercise_position).getTempo_isometric()
                + exercises.get(exercise_position).getTempo_negative();
    }

    /**
     *    Events when timer has finished
     *<p>
     *
     *
     */
    public void onOverlayTextListener(int  second){
        //Log.i(TAG,"onOverlayTextListener"+s);

        //AFTER INITIAL TIMER FINISHED
        if ( second == 0 && isMainCountDownActive ){

            onRestFinished();

        } else if( second == 0 && isInitialCountDownActive ) {

            onInitialTimerFinished();
            
        }

    }


    private void onInitialTimerFinished(){
        destroyInicialTimer();

        //SET  inital countdown off
        isInitialCountDownActive = false;
        //set the main flag active
        isMainCountDownActive = true;


        startMainCountDown();

        view.onOverlayViewOff();
    }

    private void onRestFinished(){
        //Log.i(TAG,"onRestFinished");

        destroyRestCountDownTimer();

        //flag rest off
        isWorkoutRest = false;


        view.onOverlayViewOff();

        //restarting the view of the workout
        view.onWorkoutRestart(
                exercises.get(exercise_position).getRepetition(),
                exercises.get(exercise_position).getTempo_positive(),
                exercises.get(exercise_position).getTempo_isometric(),
                exercises.get(exercise_position).getTempo_negative());

        // overlay VIEW off 
        view.onOverlayViewOff();

        //play exercise audio
        if (isAudioExerciseActive){
            //playing Exercise Audio
            playExerciseAudio(exercises.get(exercise_position).getExercise().getExercise_audio());
        }


        //Log.i("restlistener","startMainCountDown");
        startMainCountDown();
    }

    public void repetitionListener(int repetition){
        //Log.i(TAG,"repetitionListener"+repetition.toString());

        if ( repetition == 0 && isMainCountDownActive){

            //destroy MainCount Down
            destroyMainCountDownTimer();



            //exercise and set logic
            if (exercise_position + 1 < exercises.size()){
                Log.i(TAG,"next exercise");


                exercise_position++;

                if (exercises.size() > 1){

                    //set preview exercise button on
                    view.onPreviewExerciseUi();

                    if( (exercise_position+1) == exercises.size()) {

                        //set next exercise button on
                        view.onNextExerciseUi();
                    }
                }

                //when the repetitions of the exercise has finished.
                view.onRepsFinished(exercise_position);

                
            } else {

                Log.i(TAG,"count sets");

                // contar sets
                Log.i(TAG,"mCurrentSet"+mCurrentSet);
                Log.i(TAG,"totalSets"+totalSets);
                if (mCurrentSet + 1 <= totalSets){

                    //plus one more set
                    mCurrentSet++;

                    //set the exercise in the first position
                    exercise_position = 0;

                    
                    view.onSetFinished(mCurrentSet);

                } else {
                    view.onWorkoutFinished();
                }
            }
        }

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
        if (isInicialTimerAvailable()){
            pauseInicialTimer();
        }

        //pause main COUNTDOWN
        if (isMainCountDownTimerAvailable()){
            pauseMainCountDown();
        }

        //pause rest countdown
        if (isRestCountDownTimerAvailable()){
            pauseRestCountDownTimer();
        }

    }
    
    private void resumeCountDown(){
        //set pause flag off
        isWorkoutPaused = false;

        //resume initial countdown
        if (isInicialTimerAvailable()){
            resumeInicialTimer();
        }

        //resume main countdown
        if (isMainCountDownTimerAvailable()){
            resumeMainCountDown();
        }

        //Resume rest countdown
        if (isRestCountDownTimerAvailable()){
            Log.i("resumeCountDown","resumeRestCountDownTimer");
            resumeRestCountDownTimer();
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
        Log.i(TAG,"finishWorkout");

       //workout paused
        //pauseWorkout();


        view.onWorkoutFinished();




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
    }


    /**
     *    Events of next exercise
     *<p>
     *
     *
     */
    
    public void nextExercise(){
        pauseWorkout();

        view.onNextExercise();
    }

    public void onNextExercisePositive(){


        if (!isButtonPauseClicked){
            playWorkout();
        }



        if (isMainCountDownActive){
            exercise_position++;

            view.onChangeToExercise(exercise_position);


            destroyMainCountDownTimer();


            view.onWorkoutRestart(
                    exercises.get(exercise_position).getRepetition(),
                    exercises.get(exercise_position).getTempo_positive(),
                    exercises.get(exercise_position).getTempo_isometric(),
                    exercises.get(exercise_position).getTempo_negative());


            startMainCountDown();

            if (exercise_position == exercises.size() -1){

               view.onNextExerciseUi();


            } else if (exercise_position > 0 && exercises.size() > 1){

                view.onPreviewExerciseUi();

            }



           pauseWorkout();
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
        pauseWorkout();


        view.onPreviewExercise();
    }

    public void onPreviewExercisePositive(){
         /*   resumeCountDown();


                    if (isMainCountDownActive){

                        //move to before exercise
                        exercise_position--;



                        view.onChangeToExercise(exercise_position);


                        //main_timer.cancel();


                        view.onWorkoutRestart(
                                exercises.get(exercise_position).getRepetition(),
                                exercises.get(exercise_position).getTempo_positive(),
                                exercises.get(exercise_position).getTempo_isometric(),
                                exercises.get(exercise_position).getTempo_negative());

                        //setTotalRepetitionTime(exercise_position);


                        //startMainCountDown(totalTime,totalTime);

                        if (exercise_position == 0){

                            mPreviewExerciseButton.setVisibility(View.GONE);

                        }else if(exercise_position < elements -1 && exercises.size() > 1){

                            mNextExercisebutton.setVisibility(View.VISIBLE);

                        }





                        if (isWorkoutPaused){
                            pauseCountDown();
                            pauseLocalMusic();

                        }

                    }

                    */


    }

    public void onPreviewExerciseNegative(){
        playWorkout();
    }


    @Override
    public void onStart() {
        Log.i(TAG,"onStart");
        playWorkout();
    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        playWorkout();
    }

    @Override
    public void onPause() {
        Log.i(TAG,"onPause");
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

        destroySpotifyPlayer();
        destroyInicialTimer();
        destroyMainCountDownTimer();
        destroyRestCountDownTimer();
        destroyExerciseAudioPlayer();
    }


    private void destroySpotifyPlayer(){
        Log.i(TAG,"destroySpotifyPlayer");
        if (isSpotifyPlayerAvailable()){mSpotifyPlayer.onDestroy();}
    }

    private void destroyInicialTimer(){
        Log.i(TAG,"destroyInicialTimer");
        if (isInicialTimerAvailable()){mStartedInitialTimer.cancel();mStartedInitialTimer = null;}
    }

    private void destroyMainCountDownTimer(){
        Log.i(TAG,"destroyMainCountDownTimer");
        if (isMainCountDownTimerAvailable()){sMainCountDownTimer.cancel();sMainCountDownTimer = null;}
    }
    private void destroyRestCountDownTimer(){
        Log.i(TAG,"destroyRestCountDownTimer");
        if (isRestCountDownTimerAvailable()){mRestTimer.cancel();mRestTimer = null;}
    }

    private void destroyExerciseAudioPlayer(){
        Log.i(TAG,"destroyExerciseAudioPlayer");
        if (isExerciseAudioPlayerAvailable()){mExerciseAudioPlayer.release();}
    }



}
