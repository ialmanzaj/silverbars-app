package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.ArrayList;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public interface ResultsCallback extends ServerCallback{
    void onWorkoutDone(WorkoutDone workout);
    void onSavedExerciseProgress(ExerciseProgression exerciseProgression);


    void isEmptyProgression();
    void onExerciseProgression(ArrayList<ExerciseProgression> exercisesProgression);

}
