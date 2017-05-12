package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.callbacks.ExerciseDetailCallback;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 04/18/17.
 */

public class ExerciseDetailInteractor {

    private DatabaseQueries queries;

    public ExerciseDetailInteractor(DatabaseHelper helper){
        queries = new DatabaseQueries(helper);
    }

    public void getProgressions(int exercise_id, ExerciseDetailCallback callback) throws SQLException {
        List<ExerciseProgression> progressions = queries.getExerciseProgressionsById(exercise_id);
        callback.onProgressions(progressions);
    }


}
