package com.app.proj.silverbars.viewsets;

/**
 * Created by isaacalmanza on 01/14/17.
 */

public interface WorkingOutView {

    void updateSongName(String song_name);
    void updateArtistName(String artist_name);

    void onPauseMusic();
    void onPlayMusic();


    void displayModalRest();

    void onRestCounterStarted(String second);

    void onMainCounterStarted(String second);

    void onRepetitionCountdown(String second);

    void onNextExercise();

    void onPreviewExercise();

    void onRepsFinished(int exercise_position);


    void onSetFinished(int set);

    void onWorkoutFinished();
}
