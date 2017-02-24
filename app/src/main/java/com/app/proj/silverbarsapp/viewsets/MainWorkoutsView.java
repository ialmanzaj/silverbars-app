package com.app.proj.silverbarsapp.viewsets;

import com.app.proj.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public interface MainWorkoutsView extends BaseView {

    void displayWorkouts(List<Workout> workouts);
}
