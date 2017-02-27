package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public interface MainWorkoutsCallback extends ServerCallback{

    void onWorkoutsFound(List<Workout> workouts);


}
