package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.ProgressionCallback;
import com.app.app.silverbarsapp.interactors.ProgressionInteractor;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import java.util.List;

/**
 * Created by isaacalmanza on 02/28/17.
 */

public class ProgressionPresenter extends BasePresenter implements ProgressionCallback {

    private static final String TAG = ProgressionPresenter.class.getSimpleName();

    private ProgressionInteractor interactor;
    private ProgressionView view;

    public ProgressionPresenter(ProgressionView view,ProgressionInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getMuscleProgressions(){
        interactor.getMuscleProgressions(this);
    }

    public void getExerciseProgression(){
        interactor.getExerciseProgression(this);
    }

    @Override
    public void emptyProgress() {
        view.emptyProgress();
    }

    @Override
    public void onProgression(List<ExerciseProgression> progressions) {
        view.displayProgressions(progressions);
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
