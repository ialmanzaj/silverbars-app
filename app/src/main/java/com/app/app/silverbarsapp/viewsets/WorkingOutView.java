package com.app.app.silverbarsapp.viewsets;

/**
 * Created by isaacalmanza on 01/14/17.
 */

public interface WorkingOutView {

    void onCountDownReady(int seconds);
    void onCountDownWorking(String second);

    //music interfaces
    void updateSongName(String song_name);
    void updateArtistName(String artist_name);


    void onStartChronometer();
    void onStopChronometer();

    void onWorkoutReady();
    void onResumeWorkout();
    void onPauseWorkout();
    void onFinishWorkout();

    void onPauseMusic();
    void onPlayMusic();

    //counter
    void onRestCounterStarted(String second);

    void onChangeToExercise(int exercise_position);
    void onSetFinished(int set);

    void onOverlayViewOn();
    void onOverlayViewOff();
}
