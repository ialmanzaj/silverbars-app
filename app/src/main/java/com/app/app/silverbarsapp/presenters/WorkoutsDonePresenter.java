package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.WorkoutsDoneCallback;
import com.app.app.silverbarsapp.interactors.WorkoutsDoneInteractor;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.viewsets.WorkoutsDoneView;

import java.util.List;

/**
 * Created by isaacalmanza on 04/06/17.
 */

public class WorkoutsDonePresenter extends BasePresenter implements WorkoutsDoneCallback{

    private static final String TAG = WorkoutsDonePresenter.class.getSimpleName();

    private WorkoutsDoneView view;
    private WorkoutsDoneInteractor interactor;

    public WorkoutsDonePresenter(WorkoutsDoneView view,WorkoutsDoneInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getWorkoutsDone(){
        interactor.getMyWorkoutsDone(this);
    }

    @Override
    public void onWorkoutsDone(List<WorkoutDone> workouts) {
        view.onWorkoutsDone(workouts);
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
