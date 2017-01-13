package com.app.proj.silverbars.callbacks;

import com.app.proj.silverbars.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public interface MainWorkoutsCallback extends ServerCallback{

    void onWorkoutsFound(List<Workout> workouts);


}
