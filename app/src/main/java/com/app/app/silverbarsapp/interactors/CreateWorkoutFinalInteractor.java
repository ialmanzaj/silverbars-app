package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.CreateWorkoutFinalCallback;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public class CreateWorkoutFinalInteractor {

    private static final String TAG = CreateWorkoutFinalInteractor.class.getSimpleName();

    private MainService mainService;
    private DatabaseQueries queries;
    private DatabaseHelper helper;

    public CreateWorkoutFinalInteractor(DatabaseHelper helper, MainService mainService){
        this.helper = helper;
        this.mainService = mainService;
        this.queries = new DatabaseQueries(helper);
    }

    public void saveWorkoutDatabase(com.app.app.silverbarsapp.models.Workout workout,CreateWorkoutFinalCallback callback) throws SQLException {
        Log.d(TAG,"id "+workout.getId());
        queries.insertUserWorkout(workout);
        callback.onWorkoutDatabaseCreated();
    }

    public void saveWorkoutInApi(Workout workout, CreateWorkoutFinalCallback callback) throws SQLException {
        mainService.createMyWorkout(
                helper.getMyProfile().queryForAll().get(0).getId(),
                workout.getWorkout_name(),
                null,
                workout.getSets(),
                workout.getLevel(),
                workout.getMainMuscle()
        ).enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful()){

                    Workout workout = response.body();
                    Log.d(TAG,"id"+workout.getId());

                   callback.onWorkoutApiCreated(workout);

                }else {
                    Log.e(TAG, "onFailure: "+response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
            }
        });
    }

    public void insertExercisesRepsApi(ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> exercises, CreateWorkoutFinalCallback callback){
        Observable.from(exercises)
                .flatMap(exercise ->
                        mainService.insertExercisesReps(
                        exercise.getExercise().getId(),
                                exercise.getWorkout(),
                                exercise.getRepetition(),
                                exercise.getSeconds(),
                                exercise.getWeight()
                        ))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onExerciseApiCreated,
                        error ->
                            Log.e(TAG, "insertExercisesRepsApi error ", error)
                );
    }

    public void unsuscribe(){

    }

}
