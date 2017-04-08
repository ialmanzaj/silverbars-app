package com.app.app.silverbarsapp.presenters;

import android.util.Log;

import com.app.app.silverbarsapp.callbacks.CreateWorkoutFinalCallback;
import com.app.app.silverbarsapp.interactors.CreateWorkoutFinalInteractor;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutFinalView;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public class CreateWorkoutFinalPresenter extends BasePresenter implements CreateWorkoutFinalCallback {

    private static final String TAG = CreateWorkoutFinalPresenter.class.getSimpleName();

    private CreateWorkoutFinalView view;
    private CreateWorkoutFinalInteractor interactor;

    private ArrayList<ExerciseRep> exercises;
    private Workout workout;

    public CreateWorkoutFinalPresenter(CreateWorkoutFinalView view,CreateWorkoutFinalInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void saveWorkoutDatabase(Workout workout) throws SQLException {
        interactor.saveWorkoutDatabase(workout,this);
    }

    public void saveWorkoutApi(Workout workout) throws SQLException {
        this.exercises = workout.getExercises();
        interactor.saveWorkoutInApi(workout,this);
    }


    @Override
    public void onWorkoutApiCreated(Workout workout) {
        this.workout = workout;


        Log.d(TAG,"workout: "+workout.getId());
        saveExercisesRepsApi(getExercisesReadyForApi(exercises,workout.getId()));
    }

    @Override
    public void onExerciseApiCreated(ExerciseRep exercise) {
        if (exercise.getExercise().getId() == exercises.get(exercises.size()-1).getExercise().getId()){
            workout.setExercises(exercises);
            view.displayWorkoutApiCreated(workout);
        }
    }

    @Override
    public void onWorkoutDatabaseCreated() {
        view.displayWorkoutDatabaseCreated();
    }

    private void saveExercisesRepsApi(ArrayList<ExerciseRep> exercises){
        interactor.insertExercisesRepsApi(exercises,this);
    }

    private ArrayList<ExerciseRep> getExercisesReadyForApi(ArrayList<ExerciseRep> exercises,int  workout_id){
        for (ExerciseRep exercise: exercises){
            exercise.setWorkout(workout_id);
        }
        return exercises;
    }

    @Override
    public void onStart() {}

    @Override
    public void onStop() {}

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onRestart() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onServerError() {
        view.displayServerError();
    }

    @Override
    public void onNetworkError() {
        view.displayNetworkError();
    }
}
