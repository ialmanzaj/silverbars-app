package com.app.proj.silverbarsapp.callbacks;

import com.app.proj.silverbarsapp.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/27/17.
 */

public interface ExerciseListCallback extends ServerCallback {
    void onExercises(List<Exercise> exercises);

}
