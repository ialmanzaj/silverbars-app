package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Workout;

/**
 * Created by isaacalmanza on 01/29/17.
 */

public interface CreateWorkoutFinalCallback extends ServerCallback {
    //database
    void onWorkoutDatabaseCreated();

    //api
    void onWorkoutApiCreated(Workout workout);
    void onExerciseApiCreated(ExerciseRep exercise);
}
