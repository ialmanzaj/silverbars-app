package com.app.proj.silverbarsapp.callbacks;


import com.app.proj.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 02/16/17.
 */

public interface UserWorkoutsCallback {
    void onWorkouts(List<Workout> user_workouts);
    void onEmptyWorkouts();
}
