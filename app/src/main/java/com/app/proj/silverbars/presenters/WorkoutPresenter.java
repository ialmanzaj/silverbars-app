package com.app.proj.silverbars.presenters;

import com.app.proj.silverbars.callbacks.WorkoutCallback;
import com.app.proj.silverbars.interactors.WorkoutInteractor;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.viewsets.WorkoutView;

import java.sql.SQLException;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkoutPresenter extends BasePresenter implements WorkoutCallback {

    private WorkoutInteractor interactor;
    private WorkoutView view;


    public WorkoutPresenter(WorkoutView view, WorkoutInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }



    public boolean isWorkoutExist(int workout_id) throws SQLException {
        return interactor.isWorkoutExist(workout_id);
    }

    public void saveWorkout(Workout workout) throws SQLException {
        interactor.saveWorkout(workout,this);
    }

    @Override
    public void onWorkout(boolean created) {
        view.onWorkout(created);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onRestart() {

    }


    @Override
    public void onDestroy() {
        //interactor.onDestroy();
    }


}
