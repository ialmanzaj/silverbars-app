package com.app.proj.silverbars.callbacks;

import com.app.proj.silverbars.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/29/17.
 */

public interface CreateWorkoutFinalCallback extends ServerCallback {

    void onExercises(List<Exercise> exercises);
}
