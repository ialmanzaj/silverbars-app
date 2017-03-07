package com.app.app.silverbarsapp.viewsets;

/**
 * Created by isaacalmanza on 01/14/17.
 */

public interface WorkingOutView {

    void onWorkoutReady();
    void onResumeWorkout();
    void onPauseWorkout();
    void onFinishWorkout();

    void onPauseMusic();
    void onPlayMusic();


    //counter
    void onRestCounterStarted(String second);

    void updateSongName(String song_name);
    void updateArtistName(String artist_name);


    void onChangeToExercise(int exercise_position);
    void onSetFinished(int set);

    void updateToExercise(int rep, boolean isRepOrSecond);

    void onNextExercise();
    void onPreviewExercise();

    void onOverlayViewOn();
    void onOverlayViewOff();

}
