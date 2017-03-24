package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.interactors.ResultsInteractor;
import com.app.app.silverbarsapp.viewsets.ResultsView;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public class ResultsPresenter extends BasePresenter   {

    private static final String TAG = ResultsPresenter.class.getSimpleName();

    private ResultsView view;
    private ResultsInteractor interactor;

    public ResultsPresenter(ResultsView view,ResultsInteractor interactor){
        this.view = view;
        this.interactor = interactor;
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
