package com.app.proj.silverbars.presenters;

import com.app.proj.silverbars.callbacks.SavedWorkoutsCallback;
import com.app.proj.silverbars.interactors.SavedWorkoutsInteractor;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.viewsets.SavedWorkoutsView;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 02/10/17.
 */
public class SavedWorkoutsPresenter  extends BasePresenter implements SavedWorkoutsCallback {


    private SavedWorkoutsInteractor interactor;
    private SavedWorkoutsView view;

    public SavedWorkoutsPresenter(SavedWorkoutsView view,SavedWorkoutsInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getWorkouts() throws SQLException {
        interactor.getWorkout(this);
    }

    @Override
    public void onWorkouts(List<Workout> workouts) {
        view.onWorkouts(workouts);
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
        //interactor.onDestroy();
    }


}
