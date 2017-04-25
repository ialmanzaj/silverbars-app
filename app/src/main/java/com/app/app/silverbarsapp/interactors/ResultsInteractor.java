package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ResultsCallback;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public class ResultsInteractor {

    private static final String TAG = ResultsInteractor.class.getSimpleName();

    private MainService mainService;
    private DatabaseQueries queries;

    public ResultsInteractor(DatabaseHelper helper,MainService mainService){
        this.mainService = mainService;
        queries = new DatabaseQueries(helper);
    }

    public void createWorkoutDone(int workout_id, int sets, String total_time, ResultsCallback callback) throws SQLException {
        mainService.createWorkoutDone(
                new DateTime().toString(),
                workout_id,
                queries.getMyProfile().getId(),
                sets,
                total_time
       ).enqueue(new Callback<WorkoutDone>() {
            @Override
            public void onResponse(Call<WorkoutDone> call, Response<WorkoutDone> response) {
                if(response.isSuccessful()){

                    callback.onWorkoutDone(response.body());

                }else {
                    Log.e(TAG,"error "+response.errorBody()+" code "+response.code());
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<WorkoutDone> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
                callback.onNetworkError();
            }
        });
    }

    public void saveExerciseProgressions(
            int my_workout_done_id, ArrayList<com.app.app.silverbarsapp.models.ExerciseProgression> exercises,
            ResultsCallback callback) throws SQLException {

        int my_id = queries.getMyProfile().getId();

        Observable.from(exercises)
                .flatMap(exercise ->  mainService.saveExerciseProgression(
                        my_workout_done_id,
                        my_id,
                        exercise.getExercise().getId(),
                        0,
                        exercise.getTotal_repetition(),
                        exercise.getRepetitions_done(),
                        exercise.getTotal_seconds(),
                        exercise.getSeconds_done(),
                        exercise.getTotal_weight(),
                        new DateTime().toString()
                )) .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onSavedExerciseProgress,
                        error -> {Log.e(TAG, "onFailure error ", error);callback.onNetworkError();}
                );
    }


    public void getProgressions(List<ExerciseProgression> exercises, ResultsCallback callback) throws SQLException {
        ArrayList<ExerciseProgression> progressions = new ArrayList<>();
       for (ExerciseProgression exercise: exercises){

           ExerciseProgression exerciseProgression = queries.getLastProgressionByExerciseId(exercise.getExercise().getId());

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
