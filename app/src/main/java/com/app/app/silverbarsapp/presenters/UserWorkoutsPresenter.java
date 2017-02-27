package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.UserWorkoutsCallback;
import com.app.app.silverbarsapp.interactors.UserWorkoutsInteractor;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.viewsets.UserWorkoutsView;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public class UserWorkoutsPresenter extends BasePresenter implements UserWorkoutsCallback {

    private UserWorkoutsView view;
    private UserWorkoutsInteractor interactor;

    public UserWorkoutsPresenter(UserWorkoutsView view,UserWorkoutsInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getMyWorkouts() throws SQLException {
        interactor.getWorkout(this);
    }


    @Override
    public void onWorkouts(List<Workout> user_workouts) {
        view.onWorkouts(user_workouts);
    }

    @Override
    public void onEmptyWorkouts() {
        view.onEmptyWorkouts();
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
