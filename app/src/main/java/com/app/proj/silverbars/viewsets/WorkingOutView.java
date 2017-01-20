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
    void onMainCounterStarted(String second);
    void onRepetitionCountdown(String second);

    void onNextExerciseUI();
    void onPreviewExerciseUI();


    void onChangeToExercise(int exercise_position);

    void onRepsFinished(int exercise_position);
    void onSetFinished(int set);

    void onShowRestOverlay();
    void onRestFinished();

    void onWorkoutRestart(int rep,int tempo_positive,int tempo_isometric,int tempo_negative);


    void onWorkoutResume();
    void onWorkoutPaused();
    void onWorkoutFinished();
}
