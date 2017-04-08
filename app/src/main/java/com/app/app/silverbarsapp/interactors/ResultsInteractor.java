package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ResultsCallback;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public class ResultsInteractor {

    private static final String TAG = ResultsInteractor.class.getSimpleName();

    private DatabaseHelper databaseHelper;
    private MainService mainService;
    private DatabaseQueries queries;

    public ResultsInteractor(DatabaseHelper helper,MainService mainService){
        this.databaseHelper = helper;
        this.mainService = mainService;
        queries = new DatabaseQueries(helper);
    }



    public void createWorkoutDone(int workout_id, int sets, String total_time, ResultsCallback callback) throws SQLException {
        mainService.createWorkoutDone(
                new DateTime().toString(),
                workout_id,
                databaseHelper.getMyProfile().queryForAll().get(0).getId(),
                sets,
                total_time
       ).enqueue(new Callback<WorkoutDone>() {
            @Override
            public void onResponse(Call<WorkoutDone> call, Response<WorkoutDone> response) {
                if(response.isSuccessful()){
                    callback.onWorkoutDone(response.body());
                }else {
                    Log.e(TAG,""+response.errorBody()+" code "+response.code());
                }
            }
            @Override
            public void onFailure(Call<WorkoutDone> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
            }
        });
    }


    public void getProgressions(List<ExerciseRep> exercises, ResultsCallback callback) throws SQLException {
        List<ExerciseProgression> progressions = new ArrayList<>();
       for (ExerciseRep exercise: exercises){

           ExerciseProgression exerciseProgression =
                   queries.searchExerciseProgressionByExerciseid(exercise.getExercise().getId());

           if (exerciseProgression != null){
               progressions.add(exerciseProgression);
           }
       }

        if (progressions.size() <= 0){
           callback.isEmptyProgression();
       }else {
           callback.onExerciseProgression(progressions);
       }

    }



}
