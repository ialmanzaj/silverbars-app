package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.ExerciseListCallback;
import com.app.app.silverbarsapp.interactors.ExerciseListInteractor;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.viewsets.ExerciseListView;

import java.util.List;

/**
 * Created by isaacalmanza on 01/30/17.
 */

public class ExerciseListPresenter extends BasePresenter implements ExerciseListCallback {


    private ExerciseListView view;
    private ExerciseListInteractor interactor;

    public ExerciseListPresenter(ExerciseListView view,ExerciseListInteractor interactor){
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
    public void onRestart() {

    }

    @Override
    public void onDestroy() {

    }





}
