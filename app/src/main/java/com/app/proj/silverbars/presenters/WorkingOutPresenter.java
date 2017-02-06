package com.app.proj.silverbars.presenters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.proj.silverbars.CountDownTimerWithPause;
import com.app.proj.silverbars.MusicCallback;
import com.app.proj.silverbars.MusicPlayer;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SpotifyPlayerImpl;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter  implements MusicCallback {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();


    private Utilities utilities = new Utilities();
    
    
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
    private CountDownTimerWithPause sMainCountDownTimer, mRestTimer, mStartedInitialTimer;



    private int TotalSets = 0, mCurrentSet = 0,mCurrentRest = 0,mCurrentStartTime = 0,mTempobyExercise = 0;

    private int actualReps;
    private int exercise_position = 0;

    int currentMainTime = 0, currentRestTime=0;

    private int totalTime;


    //FLAGS
    private boolean isWorkoutPaused = false,isWorkoutRest = false;
    private boolean isInitialCountDownActive = false,main = false,isMainCountDownActive = false;

    private boolean isButtonPauseClicked;

    private boolean isAudioExerciseActive;


    public WorkingOutPresenter(Context context,WorkingOutView view){
        this.context = context;
        this.view = view;
    }

    public void setInitialSetup(ArrayList<ExerciseRep> exercises,boolean exerciseActive){
        this.exercises = exercises;
        this.isAudioExerciseActive = exerciseActive;
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

    public void playExerciseAudio(String exercise_audio_file){
        Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

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


    public void onSwipeMusicPreview(){

    }

    public void onSwipeMusicNext(){

    }

    @Override
    public void onSongName(String song_name) {
        view.updateSongName(song_name);
    }

    @Override
    public void onArtistName(String artist_name) {
        view.updateArtistName(artist_name);
    }




    /**
     *    CountDownTimers logic
     *<p>
     *
     *
     */
    public void startInicialTimer(int seconds){
        mCurrentStartTime = seconds;


        long total_sec = seconds * 1000;

        mStartedInitialTimer = new CountDownTimerWithPause(total_sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mCurrentStartTime--;

                view.onMainCounterStarted(String.valueOf(mCurrentStartTime));
            }
            public void onFinish() {
                onInitialTimerFinished();
            }
        }.start();
    }

    private void startMainCountDown(){
        Log.i(TAG,"startMainCountDown");


        if (!isButtonPauseClicked && !isWorkoutRest){

            actualReps = getTotalRepetitionTime(exercise_position);
            //Log.i(TAG,"actualReps: "+actualReps);


            int tempo = getTempo(exercise_position);
            long interval =  tempo * 1000;

            Log.i(TAG,"interval: "+interval);


            int total_seconds = (actualReps * tempo +5) * 1000;
            Log.i(TAG,"total_seconds: "+total_seconds);


            sMainCountDownTimer = new CountDownTimerWithPause(total_seconds, interval) {
                public void onTick(long millisUntilFinished) {

                    Log.i(TAG,""+millisUntilFinished);


                    //restar reps de texto mostrado
                    view.onRepetitionCountdown(String.valueOf(actualReps--));
                }
                public void onFinish() {}
            }.start();
        }
    }



    private void startRestCountDownTimer(int rest){
        //set the actual rest var
        mCurrentRest = rest;
        
        int total_secs = (rest +1) * 1000;

        Log.i(TAG,"mCurrentRest"+mCurrentRest);
        Log.i(TAG,"total_secs"+total_secs);

        mRestTimer = new CountDownTimerWithPause(total_secs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                
                //restar 
                mCurrentRest--;

                //UI changes
                view.onRestCounterStarted(String.valueOf(mCurrentRest));
            }
            public void onFinish() {}
        }.start();
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

    private void onInitialTimerFinished(){
        //SET false inital countdown
        isInitialCountDownActive = false;

        //set the main flag active
        isMainCountDownActive = true;

        startMainCountDown();

        view.onOverlayViewOff();
    }



    public void repetitionListener(CharSequence repetition){
        Log.i(TAG,"repetitionListener"+repetition.toString());

        if (Integer.parseInt(repetition.toString()) == 0 && isMainCountDownActive){

            if (exercise_position + 1 < exercises.size()){
                exercise_position++;

                if (exercises.size() > 1){

                    //set preview exercise button on
                    view.onPreviewExerciseUI();

                    if( (exercise_position+1) == exercises.size()) {

                        //set next exercise button on
                        view.onNextExerciseUI();
                    }
                }

                //when the repetitions of the exercise has finished.
                view.onRepsFinished(exercise_position);

                
                
            } else {

                // contar sets
                if (mCurrentSet + 1 <= TotalSets){

                    //plus one more set
                    mCurrentSet++;

                    //set the exercise in the first position
                    exercise_position = 0;

                    view.onSetFinished(mCurrentSet);


                    //finish = true;

                } else {


                    view.onWorkoutFinished();
                }
            }
        }

    }

    public void restlistener(CharSequence rest){

        //AFTER INITIAL TIMER FINISHED
        if (Integer.parseInt(rest.toString()) == 0 && isMainCountDownActive){

            isWorkoutRest = false;

            //restarting the view of the workout
            view.onWorkoutRestart(
                    exercises.get(exercise_position).getRepetition(),
                    exercises.get(exercise_position).getTempo_positive(),
                    exercises.get(exercise_position).getTempo_isometric(),
                    exercises.get(exercise_position).getTempo_negative());

            
            view.onOverlayViewOff();

            
            if (isAudioExerciseActive){
                //playing Exercise Audio
                playExerciseAudio(exercises.get(exercise_position).getExercise().getExercise_audio());
            }


            startMainCountDown();
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
    
    private void pauseCountDown(){

        //set pause flag on
        isWorkoutPaused = true;



       /* if (isMainCountDownActive && main_timer!=null){

            if (!isWorkoutRest){

                currentMainTime = Format_Time;
                main_timer.cancel();



            }else {

                //Log.v(TAG,"Pausar mRestActive: activado");
                currentRestTime = Format_Time_Rest;
                restTimer.cancel();



            }
        }else if (isInitialCountDownActive) {

            startTimer.cancel();

        }*/


    }
    
    private void resumeCountDown(){
        isWorkoutPaused = false;


       /* if (!isWorkoutRest){


            if (isMainCountDownActive){
                startMainCountDown(currentMainTime,Time_aux);
            }

            if (isInitialCountDownActive){
                startInicialTimer(mCurrentStartTime);
            }




        }else {
            Log.v(TAG,"currentRestTime: "+currentRestTime);
            startRestCountDownTimer(currentRestTime);

        }*/


    }
    
    
    

    /**
     *    Events of the workout 
     *<p>
     *
     *
     */
    
    public void playWorkout(){
        if (isWorkoutPaused){
            isButtonPauseClicked = false;



            resumeCountDown();
            playMusic();
        }


        view.onWorkoutResume();
    }
    
    public void pauseWorkout(){
        if (!isWorkoutPaused){

            isButtonPauseClicked = true;



            pauseCountDown();
            pauseMusic();
        }



        view.onWorkoutPaused();
    }

    public void finishWorkout(){
        Log.i(TAG,"finishWorkout");

      /*  //workout paused
        pauseWorkout();
*/


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
    

    
    public void dialogNextExercise(){


        pauseWorkout();
        
        String title = "Desea pasar al siguiente ejercicio?";
        String content = "Esta seguro que quiere pasar al siguiente ejercicio?";
        
        
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .titleColor(context.getResources().getColor(R.color.colorPrimaryText))
                .contentColor(context.getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(context.getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(context.getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .positiveText(context.getResources().getString(R.string.positive_dialog))
                .onPositive((dialog, which) -> {
                    
                    
                   /* dialog.dismiss();


                    if (!isButtonPauseClicked){
                        resumeCountDown();
                    }


                    if (isMainCountDownActive){

                        exercise_position++;


                        view.onChangeToExercise(exercise_position);
                        

                        //main_timer.cancel();


                        view.onWorkoutRestart(
                                exercises.get(exercise_position).getRepetition(),
                                exercises.get(exercise_position).getTempo_positive(),
                                exercises.get(exercise_position).getTempo_isometric(),
                                exercises.get(exercise_position).getTempo_negative());


                        //setTotalRepetitionTime(exercise_position);
                        //startMainCountDown(totalTime,totalTime);

                        if (exercise_position == elements -1){

                            mNextExercisebutton.setVisibility(View.GONE);


                        } else if (exercise_position > 0 && exercises.size() > 1){
                                
                            mPreviewExerciseButton.setVisibility(View.VISIBLE);
                            
                        }
                        
                        

                        if (isWorkoutPaused){
                            pauseCountDown();
                            pauseLocalMusic();
                        }


                    }
                    */
                    
                    
                    
                })
                .negativeText(context.getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> {
                    
                    dialog.dismiss();
                    resumeCountDown();
                    
                }).show();
    }

    public void dialogPreviewExercise(){
        
        pauseWorkout();
        
        
        
        String title = "Desea regresar al ejercicio anterior?";
        String content = "Esta seguro que quiere regresar al ejercicio anterior?";
        
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .titleColor(context.getResources().getColor(R.color.colorPrimaryText))
                .contentColor(context.getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(context.getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(context.getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .positiveText(context.getResources().getString(R.string.positive_dialog))

                .onPositive((dialog, which) -> {
    
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
                    
                    
                    
                })
                .negativeText(context.getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> {

                    dialog.dismiss();
                    resumeCountDown();


                }).show();


    }


    public void onStart() {
        Log.d(TAG,"onStart");


       /* if (onPause){
            resumeCountDown();
        }*/

    }


    
    @Override
    public void onResume() {
        Log.d(TAG,"onResume");

       /* if (mSpotifyPlayer != null){
            mSpotifyPlayer.onResume();
        }


        if (onPause){
            resumeCountDown();
        }*/

    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
/*
        if (mSpotifyPlayer != null){
            mSpotifyPlayer.onPause();
        }


        pauseCountDown();
        onPause = true;*/
    }


    @Override
    public void onStop() {
        Log.d(TAG,"onStop");
        

    }


    @Override
    public void onDestroy() {

      /*  if (mSpotifyPlayer != null){
            mSpotifyPlayer.onDestroy();
        }


        if (isMainCountDownActive && sMainCountDownTimer != null){sMainCountDownTimer.cancel();}

        if (mExerciseAudioPlayer!=null){mExerciseAudioPlayer.release();}
*/
    }


   

}
