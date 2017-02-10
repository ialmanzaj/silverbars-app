package com.app.proj.silverbars.viewsets;


import com.app.proj.silverbars.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 02/10/17.
 */

public interface SavedWorkoutsView {
    void onWorkouts(List<Workout> workouts);
    void onEmptyWorkouts();
}
