package com.app.app.silverbarsapp.viewsets;


import com.app.app.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 02/10/17.
 */

public interface SavedWorkoutsView {
    void onWorkouts(List<Workout> workouts);
    void onEmptyWorkouts();
}
