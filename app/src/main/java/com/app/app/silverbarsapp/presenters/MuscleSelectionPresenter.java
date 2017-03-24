package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.MuscleSelectionCallback;
import com.app.app.silverbarsapp.interactors.MuscleSelectionInteractor;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.viewsets.MuscleSelectionView;

import java.util.List;

/**
 * Created by isaacalmanza on 03/20/17.
 */

public class MuscleSelectionPresenter extends BasePresenter implements MuscleSelectionCallback{

    private MuscleSelectionInteractor interactor;
    private MuscleSelectionView view;

    public MuscleSelectionPresenter(MuscleSelectionView view,MuscleSelectionInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getMuscles(){
        interactor.getMuscles(this);
    }


    @Override
    public void onMuscles(List<Muscle> muscles) {
        view.getMuscles(muscles);
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
    public void onServerError() {

    }

    @Override
    public void onNetworkError() {

    }


}
