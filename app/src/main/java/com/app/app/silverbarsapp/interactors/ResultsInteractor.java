package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ResultsCallback;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;
import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;

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

    private int mProfileId;
    private String today;

    public ResultsInteractor(DatabaseHelper helper,MainService mainService){
        this.mainService = mainService;
        queries = new DatabaseQueries(helper);
    }

    public void createWorkoutDone(int workout_id, int sets, String total_time, boolean isMyWorkout,ResultsCallback callback) throws SQLException {
        Callback<WorkoutDone> workout_done_Callback = new Callback<WorkoutDone>() {
            @Override
            public void onResponse(Call<WorkoutDone> call, Response<WorkoutDone> response) {
                if(response.isSuccessful()){
                    callback.onWorkoutDone(response.body());
                }else {
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<WorkoutDone> call, Throwable t) {
                callback.onNetworkError();
            }
        };


        today =  new Filter().formatDayTime(new DateTime());
        mProfileId = queries.getMyProfile().getId();


        if (isMyWorkout){
            mainService.createWorkoutDoneMyWorkout(
                    today,
                    workout_id,
                    mProfileId,
                    sets,
                    total_time).enqueue(workout_done_Callback);
        }else {
            mainService.createWorkoutDoneWorkout(
                    today,
                    workout_id,
                    mProfileId,
                    sets,
                    total_time).enqueue(workout_done_Callback);
        }
    }

    public void saveExerciseProgressions(
            int my_workout_done_id,int sets,ArrayList<com.app.app.silverbarsapp.models.ExerciseProgression> exercises,
            ResultsCallback callback) throws SQLException {

        Observable.from(exercises)
                .flatMap(exercise ->
                        mainService.saveExerciseProgression(
                        my_workout_done_id,
                                mProfileId,
                        exercise.getExercise().getId(),
                        exercise.getTotal_time(),
                        (exercise.getTotal_repetition() * sets),
                        exercise.getRepetitions_done(),
                        (exercise.getTotal_seconds() * sets),
                        exercise.getSeconds_done(),
                        exercise.getTotal_weight(),
                        today)
                )
                .subscribeOn(Schedulers.newThread())
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
