package com.app.proj.silverbars.presenters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
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
    private MusicPlayer mLocalMusicPlayer;


    //exercise audio 
    private MediaPlayer mExerciseAudioPlayer;
    
    //exercise audio constants
    private final float VOLUME_FULL = (float)(Math.log(100)/Math.log(100));;
    private final float VOLUME_HALF = (float)(Math.log(100-90)/Math.log(100));
    
    
    //the main countdowns to use 
    private CountDownTimer mMainTimer, mRestTimer, mStartedInitialTimer;
    
    
    
    private int TotalSets = 0, mCurrentSet = 0,actualRest = 0,actual_start_time = 0,mTempobyExercise = 0;

    private int actualReps;
    private int exercise_position = 0;

    int currentMainTime = 0, currentRestTime=0;

    private int totalTime;


    //FLAGS
    boolean isWorkoutPaused = false,isWorkoutRest = false;
    boolean isInitialCountDownActive = false,main = false,isMainCountDownActive = false;
    boolean isButtonPauseClicked;


    public WorkingOutPresenter(Context context,WorkingOutView view){
        this.context = context;
        this.view = view;
    }

    public void setInitialSetup(ArrayList<ExerciseRep> exercises){
        this.exercises = exercises;
    }

    public void createSpotifyPlayer(String spotify_token, String spotify_playlist){
        if (mSpotifyPlayer == null){
            mSpotifyPlayer = new SpotifyPlayerImpl(context,spotify_token,spotify_playlist,this);
            mSpotifyPlayer.setup();
        }
    }

    public void createLocalMusicPlayer(String[] song_names,ArrayList<File> songs_files){
        if (mLocalMusicPlayer == null){
            mLocalMusicPlayer = new MusicPlayer(context,song_names,songs_files,this);
            mLocalMusicPlayer.setup(0,true);
        }
    }


    private void startLocalMusicPlayer(){
        mLocalMusicPlayer.startPlayer();
    }

    private void pauseLocalMusicPlayer(){
        mLocalMusicPlayer.pausePlayer();
    }


    private void resumeLocalMusicPlayer(){
        mLocalMusicPlayer.resumePlayer();
    }


    private void stopLocalMusicPlayer(){
        mLocalMusicPlayer.stopPlayer();
    }

    private void cancelLocalMusicPlayer(){
        mLocalMusicPlayer.cancelPlayer();
    }

    private boolean isPlayingLocalMusicPlayer(){
        return mLocalMusicPlayer.isPlaying();
    }


    private void finishMainCountDownTimer(){
        mMainTimer.onFinish();
    }

    public boolean isWorkoutPaused(){
        return isWorkoutPaused;
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

          
            if (mLocalMusicPlayer != null && mLocalMusicPlayer.isPlaying())
                mLocalMusicPlayer.setVolume(0.04f,0.04f);


            if (mSpotifyPlayer != null){
                mSpotifyPlayer.onPauseSpotifyPlayer();
            }

            mExerciseAudioPlayer.start();
        });


        mExerciseAudioPlayer.setOnCompletionListener(mediaPlayer -> {

            if (mLocalMusicPlayer !=null && mLocalMusicPlayer.isPlaying()) {
                mLocalMusicPlayer.setVolume(VOLUME_FULL, VOLUME_FULL);
                mediaPlayer.release();
            }

            if (mSpotifyPlayer != null){
                mSpotifyPlayer.onPlayPlayer();
            }

        });
        
    }

    private void onSwipeMusicPreview(){

    }

    private void onSwipeMusicNext(){

    }

    @Override
    public void onSongName(String song_name) {
        view.updateSongName(song_name);
    }

    @Override
    public void onArtistName(String artist_name) {
        view.updateArtistName(artist_name);
    }


    public void startInicialTimer(int seconds){
        long sec = (seconds + 5) * 1000;

        mStartedInitialTimer = new CountDownTimer(sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                actual_start_time--;

                view.onMainCounterStarted(String.valueOf(actual_start_time));
            }
            public void onFinish() {}
        }.start();
    }


    private void startRestCountDownTimer(int rest){

        //set the actual rest var
        actualRest = rest;

        int total_secs = (rest + 5) * 1000;

        mRestTimer = new CountDownTimer(total_secs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //restar 
                actualRest--;

                //UI changes
                view.onRestCounterStarted(String.valueOf(actualRest));
            }
            public void onFinish() {}
        }.start();
    }



    public void showRestOverlayView(int rest_time){
        //CHANGES UI
        view.onShowRestOverlay();

        //set rest flag active
        isWorkoutRest = true;


        mMainTimer.cancel();

       //starting rest CountDownTimer
        startRestCountDownTimer(rest_time);
    }

    
    private int getTotalRepetitionTime(int position){
        return totalTime = (exercises.get(position).getRepetition() * mTempobyExercise) + 5;
    }

    private int getCurrentExerciseTempo(int position){
        return exercises.get(position).getTempo_positive()
                + exercises.get(position).getTempo_isometric()
                + exercises.get(position).getTempo_negative();
    }


    private void startMainCountDown(int seconds,int tempo){
        if (!isButtonPauseClicked && !isWorkoutRest){
            //Log.d(TAG,"Main CountDown: activado");

            int total_sec = seconds * 1000;
            int interval = tempo * 1000;

            mMainTimer = new CountDownTimer(total_sec, interval) {
                public void onTick(long millisUntilFinished) {


                    actualReps--;
                    view.onRepetitionCountdown(String.valueOf(actualReps));//restar reps de texto mostrado
                }
                public void onFinish() {}
            }.start();
        }
    }


   

    public void repetitionOperationLogic(CharSequence charSequence){

        actualReps = Integer.valueOf(charSequence.toString());

        if (actualReps == 0){

            if (exercise_position + 1 < exercises.size()){
                exercise_position++;
                
                
                
                if (exercises.size() > 1){

                    //set preview exercise button on
                    view.onPreviewExerciseUI();

                    if((exercise_position+1) == exercises.size()){

                        //set next exercise button on
                       view.onNextExerciseUI();
                    }
                }

                //when the repetition has finished
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

                    //workout finished
                    mMainTimer.cancel();


                    view.onWorkoutFinished();
                }
            }
        }

    }
    
    public void restOperationLogic(){
        
        //AFTER INITIAL TIMER FINISHED
        if (actual_start_time == 0 && isInitialCountDownActive){

            //SET false inital countdown
            isInitialCountDownActive = false;
            isMainCountDownActive = true;


            finishMainCountDownTimer();
            startMainCountDown(getTotalRepetitionTime(exercise_position),getCurrentExerciseTempo(exercise_position));

            
            view.onRestFinished();

        }else if (actualRest == 0 && isMainCountDownActive){

            isWorkoutRest = false;

            //restTimer.cancel();
           

           /* if (play_exercise_audio){
                playExerciseAudio(exercises.get(exercise_position).getExercise().getExercise_audio());
            }*/
            
            

            startMainCountDown(totalTime,totalTime);
            

            view.onWorkoutRestart(
                    exercises.get(exercise_position).getRepetition(),
                    exercises.get(exercise_position).getTempo_positive(),
                    exercises.get(exercise_position).getTempo_isometric(),
                    exercises.get(exercise_position).getTempo_negative());



            view.onRestFinished();
        }
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
                startInicialTimer(actual_start_time);
            }




        }else {
            Log.v(TAG,"currentRestTime: "+currentRestTime);
            startRestCountDownTimer(currentRestTime);

        }*/


    }
    
    
    public void playWorkout(){
        if (isWorkoutPaused){
            isButtonPauseClicked = false;



            resumeCountDown();
            startLocalMusicPlayer();
        }


        view.onWorkoutResume();
    }


    public void pauseWorkout(){
        if (!isWorkoutPaused){

            isButtonPauseClicked = true;


            pauseCountDown();
            pauseLocalMusicPlayer();
        }


        view.onWorkoutPaused();
    }



    public void finishWorkout(){

        //workout paused
        pauseWorkout();


        new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.title_dialog))
                .titleColor(context.getResources().getColor(R.color.colorPrimaryText))
                .contentColor(context.getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(context.getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(context.getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content(context.getResources().getString(R.string.content_dialog))
                .positiveText(context.getResources().getString(R.string.positive_dialog))

                .onPositive((dialog, which) -> {

                   /* dialog.dismiss();


                    //canceling music player
                    cancelLocalMusicPlayer();


                   view.onWorkoutFinished();*/

                })
                .negativeText(context.getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> {
                    
                   /* dialog.dismiss();

                    if (!isButtonPauseClicked){

                        resumeCountDown();
                        startLocalMusicPlayer();
                    }
*/

                }).show();
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


        if (isMainCountDownActive && mMainTimer != null){mMainTimer.cancel();}

        if (mExerciseAudioPlayer!=null){mExerciseAudioPlayer.release();}
*/
    }


   

}
