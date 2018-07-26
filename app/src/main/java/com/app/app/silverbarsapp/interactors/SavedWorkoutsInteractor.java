package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.callbacks.SavedWorkoutsCallback;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;
import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.models.Workout;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 02/10/17.
 */

public class SavedWorkoutsInteractor {

    private static final String TAG = SavedWorkoutsInteractor.class.getSimpleName();

    private DatabaseQueries queries;

    public SavedWorkoutsInteractor(DatabaseHelper helper){
        queries = new DatabaseQueries(helper);
    }

    public void getWorkout(SavedWorkoutsCallback callback) throws SQLException {
        if (queries.checkSavedWorkoutsExist() && queries.checkSavedWorkoutsIsOn()){

            List<Workout> saved_workouts = queries.getSavedWorkouts();
            callback.onWorkouts(saved_workouts);

        }else {
            callback.onEmptyWorkouts();
        }
    }



}
