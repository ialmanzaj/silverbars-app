package com.app.proj.silverbarsapp.viewsets;


import com.app.proj.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public interface UserWorkoutsView {
    void onWorkouts(List<Workout> user_workouts);
    void onEmptyWorkouts();
}

