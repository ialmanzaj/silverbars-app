package com.app.app.silverbarsapp.viewsets;

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

    void hideNextExercisebutton();
    void showNextExercisebutton();

    void showPrevExercisebutton();
    void hidePrevExercisebutton();

    void onNextExercise();
    void onPreviewExercise();

    void onChangeToExercise(int exercise_position);

    void onRepsFinished(int exercise_position);
    void onSetFinished(int set);

    void onOverlayViewOn();
    void onOverlayViewOff();

    void onChangeExerciseValues(int rep, int tempo_positive, int tempo_isometric, int tempo_negative,boolean isRepOrSecond);


    void onWorkoutResume();
    void onWorkoutPaused();
    void onWorkoutFinished();
}
