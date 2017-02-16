package com.app.proj.silverbars.callbacks;


import com.app.proj.silverbars.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 02/16/17.
 */

public interface UserWorkoutsCallback {
    void onWorkouts(List<Workout> user_workouts);
    void onEmptyWorkouts();
}
