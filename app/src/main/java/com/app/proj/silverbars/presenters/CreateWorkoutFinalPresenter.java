package com.app.proj.silverbars.presenters;

import com.app.proj.silverbars.callbacks.CreateWorkoutFinalCallback;
import com.app.proj.silverbars.interactors.CreateWorkoutFinalInteractor;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.viewsets.CreateWorkoutFinalView;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public class CreateWorkoutFinalPresenter extends BasePresenter implements CreateWorkoutFinalCallback {


    private CreateWorkoutFinalView view;
    private CreateWorkoutFinalInteractor interactor;


    public CreateWorkoutFinalPresenter(CreateWorkoutFinalView view,CreateWorkoutFinalInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void saveWorkout(Workout workout){
        interactor.insertWorkout(workout,this);
    }

    @Override
    public void onWorkoutCreated(boolean created) {
        view.onWorkoutCreated(created);
    }


    @Override
    public void onWorkoutError() {
        view.onWorkoutError();
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



}
