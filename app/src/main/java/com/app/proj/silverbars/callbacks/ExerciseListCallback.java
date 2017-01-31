package com.app.proj.silverbars.callbacks;

import com.app.proj.silverbars.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/27/17.
 */

public interface ExerciseListCallback extends ServerCallback {
    void onExercises(List<Exercise> exercises);

}
