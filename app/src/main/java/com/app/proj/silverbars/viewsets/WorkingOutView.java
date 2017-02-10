package com.app.proj.silverbars.viewsets;

/**
 * Created by isaacalmanza on 01/14/17.
 */

public interface WorkingOutView {

    void updateSongName(String song_name);
    void updateArtistName(String artist_name);

    void onPauseMusic();
    void onPlayMusic();

    void onRestCounterStarted(String second);
    void onInitialCounterStarted(String second);
    void onRepetitionCountdown(String second);

    void onNextExerciseUi();
    void onPreviewExerciseUi();

    void onNextExercise();
    void onPreviewExercise();

    void onChangeToExercise(int exercise_position);

    void onRepsFinished(int exercise_position);
    void onSetFinished(int set);

    void onOverlayViewOn();
    void onOverlayViewOff();

    void onWorkoutRestart(int rep,int tempo_positive,int tempo_isometric,int tempo_negative);


    void onWorkoutResume();
    void onWorkoutPaused();
    void onWorkoutFinished();
}
