package com.app.proj.silverbarsapp.viewsets;

import com.app.proj.silverbarsapp.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public interface CreateWorkoutView extends BaseView{

    void displayExercises(List<Exercise> exercises);
}
