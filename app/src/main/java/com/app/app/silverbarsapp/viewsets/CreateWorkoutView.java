package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public interface CreateWorkoutView extends BaseView{

    void displayExercises(List<Exercise> exercises);
}
