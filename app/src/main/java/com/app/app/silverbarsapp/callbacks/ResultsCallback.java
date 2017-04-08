package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.List;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public interface ResultsCallback extends ServerCallback{
    void onWorkoutDone(WorkoutDone workout);

    void isEmptyProgression();
    void onExerciseProgression(List<ExerciseProgression> exercisesProgression);
}
