package com.app.proj.silverbars.presenters;

import com.app.proj.silverbars.callbacks.MainWorkoutsCallback;
import com.app.proj.silverbars.interactors.MainWorkoutsInteractor;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.viewsets.MainWorkoutsView;

import java.util.List;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class MainWorkoutsPresenter extends BasePresenter implements MainWorkoutsCallback{


    private static final String TAG = MainWorkoutsPresenter.class.getSimpleName();

    private MainWorkoutsView view;
    private MainWorkoutsInteractor interactor;


    public MainWorkoutsPresenter(MainWorkoutsView view,MainWorkoutsInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void fetchTimelineAsync() {
        interactor.getWorkouts(this);
    }


    public void getMyWorkout(){
        interactor.getWorkouts(this);
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

    @Override
    public void onWorkoutsFound(List<Workout> workouts) {
        view.displayWorkouts(workouts);
    }


    @Override
    public void onServerError() {
        view.displayServerError();
    }

    @Override
    public void onNetworkError() {
        view.displayNetworkError();
    }


}
