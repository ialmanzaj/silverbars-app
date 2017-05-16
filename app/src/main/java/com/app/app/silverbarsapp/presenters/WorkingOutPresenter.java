package com.app.app.silverbarsapp.presenters;

import android.content.Context;

import com.app.app.silverbarsapp.handlers.MusicHandler;
import com.app.app.silverbarsapp.handlers.WorkoutHandler;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter implements MusicHandler.MusicEvents, WorkoutHandler.WorkoutEvents {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();

    private Utilities utilities = new Utilities();

    private WorkingOutView view;
    private Context context;

    public WorkingOutPresenter(Context context,WorkingOutView view){
        this.context = context;
        this.view = view;
    }

    //exercise audio flag
    private boolean isAudioExerciseActive;

    //handle the workout logic
    private WorkoutHandler mWorkoutHandler;

    //handle the music logic
    private MusicHandler mMusicHandler;


    public void setInitialSetup(ArrayList<ExerciseRep> exercises,boolean exerciseActive,int sets,int restbyexercise,int restbyset){
        isAudioExerciseActive = exerciseActive;
        mWorkoutHandler = new WorkoutHandler(this,exercises,sets,restbyexercise,restbyset);
        mMusicHandler = new MusicHandler(context,this);
    }

    public void setupSpotifyPlayer(){
        mMusicHandler.setupSpotifyPlayer();
    }

    public void setupMusicPlayerLocal(ArrayList<File> songs_selected){
        mMusicHandler.setupMusicPlayerLocal(songs_selected);
    }

    public ArrayList<ExerciseRep> getExercises(){
        return mWorkoutHandler.getExercises();
    }

    public boolean isWorkoutPaused() {
        return mWorkoutHandler.isWorkoutPaused();
    }

    /**
     *
     *
     *   Music actions
     *<p>
     *
     *
     *
     *
     *
     */

    public void playMusic(){mMusicHandler.playMusic();}

    public void pauseMusic(){mMusicHandler.pauseMusic();}

    public void previewMusic(){mMusicHandler.skipPreview();}

    public void nextMusic(){mMusicHandler.skipNext();}


    /**
     *
     *
     *
     *
     *    Workout Main actions
     *<p>
     *
     *
     *
     *
     */

    public void playWorkout(){mWorkoutHandler.playWorkout();}

    public void pauseWorkout(){
        mWorkoutHandler.pauseWorkout();
    }

    public void nextExercise(){
        mWorkoutHandler.nextExercise();
    }

    public void startRest(){
        mWorkoutHandler.startRest();
    }

    public void skipRest(){mWorkoutHandler.restFinished();}

    public void saveTime(long time){
        mWorkoutHandler.saveTimePerExercise(time);
    }

    public void stopWorkout(long time){
        //Log.d(TAG,"stopWorkout");
        saveTime(time);
        pauseWorkout();
        startRest();
    }


    /**
     *
     *    Clean all
     *<p>
     *
     */

    private void destroy(){
        mMusicHandler.destroy();
        mWorkoutHandler.destroy();
    }


    /**
     *
     *
     *    Workout events
     *<p>
     *
     *
     *
     */

    @Override
    public void onCountDownWorking(String second) {
        view.onCountDownWorking(second);
    }

    @Override
    public void onWorkoutPlayed(ExerciseRep exercise) {
        if (utilities.checkIfRep(exercise)){
            view.onStartChronometer();
        }

       // mMusicHandler.playMusic();
        view.onResumeWorkout();
    }

    @Override
    public void onWorkoutPaused(ExerciseRep exercise) {
        if (utilities.checkIfRep(exercise)){
            view.onStopChronometer();
        }

        //mMusicHandler.stopMusic();
        view.onPauseWorkout();
    }

    @Override
    public void onWorkoutFinished() {
        //Log.d(TAG,"onWorkoutFinished");
        view.onFinishWorkout();
    }

    @Override
    public void onRestStarted() {
        view.onOverlayViewOn();
    }

    @Override
    public void onRestWorking(int rest) {
        //UI changes
        view.onRestCounterStarted(String.valueOf(rest));

        //rest listener
        mWorkoutHandler.setRestListener(rest);
    }

    @Override
    public void onRestFinished(Exercise exercise) {
        view.onOverlayViewOff();
        view.onRestFinished();

        //play exercise audio
        if (isAudioExerciseActive){
            //playing Exercise Audio
           //mMusicHandler.playExerciseAudio(exercise.getExercise_audio());
        }
    }

    @Override
    public void onChangeToNextExercise(int exercise_position) {
        view.onChangeToExercise(exercise_position);

        //reset the workout UI
        view.onWorkoutReady();
    }

    @Override
    public void onChangeToNextSet(int current_set) {
        view.onSetFinished(current_set);

        //reset the workout UI
        view.onWorkoutReady();
    }


    /**
     *
     *
     *    Music events
     *<p>
     *
     *
     *
     */

    @Override
    public void updateSongName(String song_name) {
        view.updateSongName(song_name);
    }

    @Override
    public void updateArtistName(String artist_name) {
        view.updateArtistName(artist_name);
    }

    @Override
    public void onMusicPlayed() {
        view.onPlayMusic();
    }

    @Override
    public void onMusicPaused() {
        view.onPauseMusic();
    }


    /**
     *
     *
     *
     *    Activity lifecycle
     *<p>
     *
     *
     *
     *
     *
     */

    @Override
    public void onStart() {
        //Log.d(TAG,"onStart");
        //playWorkout();
    }

    @Override
    public void onResume() {
        // Log.d(TAG,"onResume");
        //playWorkout();
    }

    @Override
    public void onPause() {
        // Log.d(TAG,"onPause");
        //pauseWorkout();
    }

    @Override
    public void onRestart() {
        //Log.d(TAG,"onRestart");
        //playWorkout();
    }

    @Override
    public void onStop() {
        //Log.d(TAG,"onStop");
        //pauseWorkout();
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG,"onDestroy");
        destroy();
    }

}
