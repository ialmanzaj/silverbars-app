package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.LoginCallback;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.interactors.LoginInteractor;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.viewsets.LoginView;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class LoginPresenter extends BasePresenter  implements LoginCallback{

    private LoginView view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginView view,LoginInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getAccessToken(String facebook_token){
        interactor.getAccessToken(this,facebook_token);
    }

    public void saveProfile(ProfileFacebook profile){
        interactor.saveProfile(profile,this);
    }

    @Override
    public void onToken(AccessToken accessToken) {
        view.displayToken(accessToken);
    }


    @Override
    public void onProfileCreated(boolean created) {
        view.profileCreated(created);
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
