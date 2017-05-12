package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.UserPreferencesCallback;
import com.app.app.silverbarsapp.interactors.UserPreferencesInteractor;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.viewsets.UserPreferencesView;

/**
 * Created by isaacalmanza on 04/04/17.
 */
public class UserPreferencesPresenter extends BasePresenter implements UserPreferencesCallback {

    private UserPreferencesInteractor interactor;
    private UserPreferencesView view;

    public UserPreferencesPresenter(UserPreferencesView view,UserPreferencesInteractor interactor){
        this.interactor = interactor;
        this.view = view;
    }

    public void getMyProfile(){
        interactor.getMyProfile(this);
    }

    public void getMyWorkouts() { interactor.getMyWorkouts(this);}



    @Override
    public void onProfileSaved(Person person) {
        view.onProfileSaved(person);
    }

    @Override
    public void onWorkoutsSaved() {
        view.onWorkoutSaved();
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
