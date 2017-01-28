package com.app.proj.silverbars.presenters;

import com.app.proj.silverbars.callbacks.CreateWorkoutCallback;
import com.app.proj.silverbars.interactors.CreateWorkoutInteractor;
import com.app.proj.silverbars.models.Exercise;
import com.app.proj.silverbars.viewsets.CreateWorkoutView;

import java.util.List;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class CreateWorkoutPresenter extends BasePresenter implements CreateWorkoutCallback {


    private CreateWorkoutView view;
    private CreateWorkoutInteractor interactor;

    public CreateWorkoutPresenter(CreateWorkoutView view,CreateWorkoutInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }


    public void getExercises(){
        interactor.getExercises(this);
    }


    @Override
    public void onExercises(List<Exercise> exercises) {
        view.displayExercises(exercises);
    }




    @Override
    public void onServerError() {
        view.displayServerError();
    }

    @Override
    public void onNetworkError() {
        view.displayNetworkError();
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
    public void onDestroy() {

    }



}
