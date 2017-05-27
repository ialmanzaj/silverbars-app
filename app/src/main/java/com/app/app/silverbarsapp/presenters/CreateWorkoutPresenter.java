package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.CreateWorkoutCallback;
import com.app.app.silverbarsapp.interactors.CreateWorkoutInteractor;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutView;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class CreateWorkoutPresenter extends BasePresenter implements CreateWorkoutCallback {

    private static final String TAG = CreateWorkoutPresenter.class.getSimpleName();

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
