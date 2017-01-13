package com.app.proj.silverbars.viewsets;

import com.app.proj.silverbars.models.Exercise;

import java.util.List;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public interface CreateWorkoutFinalView extends BaseView {

    void displayExercises(List<Exercise> exercises);
}
