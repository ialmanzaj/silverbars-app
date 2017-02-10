package com.app.proj.silverbars.presenters;

import com.app.proj.silverbars.callbacks.LoginCallback;
import com.app.proj.silverbars.interactors.LoginInteractor;
import com.app.proj.silverbars.models.AccessToken;
import com.app.proj.silverbars.viewsets.LoginView;

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


    @Override
    public void onToken(AccessToken accessToken) {
        view.displayToken(accessToken);
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
