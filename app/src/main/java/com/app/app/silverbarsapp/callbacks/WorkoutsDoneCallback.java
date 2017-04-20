package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.List;

/**
 * Created by isaacalmanza on 04/07/17.
 */

public interface WorkoutsDoneCallback extends ServerCallback{
    void onWorkoutsDone(List<WorkoutDone> workouts);
}
