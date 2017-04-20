package com.app.app.silverbarsapp.viewsets;

/**
 * Created by isaacalmanza on 01/14/17.
 */

public interface WorkingOutView {
    //music ui events controllers
    void onPauseMusic();
    void onPlayMusic();

    //music interfaces
    void updateSongName(String song_name);
    void updateArtistName(String artist_name);

    //main chronometer
    void onStartChronometer();
    void onStopChronometer();

    //and countdown
    void onCountDownWorking(String second);

    //workout UI events
    void onWorkoutReady();
    void onResumeWorkout();
    void onPauseWorkout();
    void onFinishWorkout();

    //rest events
    void onRestCounterStarted(String second);
    void onRestFinished();

    void onChangeToExercise(int exercise_position);
    void onSetFinished(int set);

    //overlay events
    void onOverlayViewOn();
    void onOverlayViewOff();
}
