package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.Workout;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public interface CreateWorkoutFinalView extends BaseView {
    void displayWorkoutApiCreated(Workout workout);
    void displayWorkoutDatabaseCreated();
}
