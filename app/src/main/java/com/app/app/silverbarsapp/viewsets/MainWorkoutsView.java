package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.Workout;

import java.util.List;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public interface MainWorkoutsView extends BaseView {

    void displayWorkouts(List<Workout> workouts);
}
