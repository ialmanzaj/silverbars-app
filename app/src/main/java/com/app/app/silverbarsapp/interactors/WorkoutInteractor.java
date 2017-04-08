package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.DatabaseQueries;
import com.app.app.silverbarsapp.callbacks.WorkoutCallback;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkoutInteractor {

    private static final String TAG = WorkoutInteractor.class.getSimpleName();

    private DatabaseHelper helper;
    private DatabaseQueries queries;

    public WorkoutInteractor(DatabaseHelper helper){
        this.helper = helper;
        queries = new DatabaseQueries(helper);
    }

    public void saveWorkout(com.app.app.silverbarsapp.models.Workout workout, WorkoutCallback callback) throws SQLException {
        queries.saveWorkout(workout);
        callback.onWorkout(true);
    }

    public void setWorkoutOn(int workout_id) throws SQLException {
        queries.setWorkoutOn(workout_id);
    }

    public boolean isWorkoutExist(int workout_id) throws SQLException {
        return queries.isWorkoutExist(workout_id);
    }

    public boolean isWorkoutSaved(int workout_id) throws SQLException {
        return queries.isWorkoutSaved(workout_id);
    }


    public void setWorkoutOff(int workout_id) throws SQLException {
        queries.setWorkoutOff(workout_id);
    }




}
