package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public interface CreateWorkoutCallback extends ServerCallback {

    void onExercises(List<Exercise> exercises);
}
