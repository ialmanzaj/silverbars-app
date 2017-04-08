package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.WorkoutsDoneCallback;
import com.app.app.silverbarsapp.interactors.WorkoutsDoneInteractor;
import com.app.app.silverbarsapp.viewsets.WorkoutsDoneView;

/**
 * Created by isaacalmanza on 04/06/17.
 */

public class WorkoutsDonePresenter extends BasePresenter implements WorkoutsDoneCallback{

    private WorkoutsDoneView view;
    private WorkoutsDoneInteractor interactor;

    public WorkoutsDonePresenter(WorkoutsDoneView view,WorkoutsDoneInteractor interactor){
        this.view = view;
        this.interactor = interactor;
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
