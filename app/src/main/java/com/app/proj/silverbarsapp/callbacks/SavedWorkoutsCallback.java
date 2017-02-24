package com.app.proj.silverbarsapp.callbacks;


import com.app.proj.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 02/10/17.
 */

public interface SavedWorkoutsCallback  {
    void onWorkouts(List<Workout> workouts);

    void onEmptyWorkouts();
}
