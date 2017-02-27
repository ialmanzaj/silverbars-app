package com.app.app.silverbarsapp.callbacks;


import com.app.app.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 02/16/17.
 */

public interface UserWorkoutsCallback {
    void onWorkouts(List<Workout> user_workouts);
    void onEmptyWorkouts();
}
