package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.List;

/**
 * Created by isaacalmanza on 04/07/17.
 */

public interface WorkoutsDoneView extends BaseView{
    void onWorkoutsDone(List<WorkoutDone> workouts);
}
