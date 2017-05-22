package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.MainCallback;
import com.app.app.silverbarsapp.interactors.MainInteractor;
import com.app.app.silverbarsapp.viewsets.MainView;

/**
 * Created by isaacalmanza on 05/16/17.
 */

public class MainPresenter extends BasePresenter implements MainCallback {

    MainInteractor interactor;
    MainView view;

    public MainPresenter(MainView view,MainInteractor interactor){
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
