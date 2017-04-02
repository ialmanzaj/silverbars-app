package com.app.app.silverbarsapp.presenters;

import android.util.Log;

import com.app.app.silverbarsapp.callbacks.LoginCallback;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.interactors.LoginInteractor;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.viewsets.LoginView;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class LoginPresenter extends BasePresenter  implements LoginCallback{

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginView view;
    private LoginInteractor interactor;

    private ProfileTracker mProfileTracker;
    private ProfileFacebook profileFacebook;

    public LoginPresenter(LoginView view,LoginInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void onLoginSuccess(LoginResult loginResult){

        getAccessToken(loginResult.getAccessToken().getToken());

        profileFacebook = new ProfileFacebook();

        if (mProfileTracker == null){
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    // profile2 is the new profile

                    Log.v("facebook - profile id", currentProfile.getId());
                    Log.v("facebook - profile", currentProfile.getFirstName());
                    Log.v("facebook - profile link", String.valueOf(currentProfile.getLinkUri()));

                    profileFacebook.setId(Long.parseLong(currentProfile.getId()));
                    profileFacebook.setFirst_name(currentProfile.getFirstName());
                    profileFacebook.setLast_name(currentProfile.getLastName());

                    //save in database
                    saveProfile(profileFacebook);
                }
            };
        }
    }

    private void saveProfile(ProfileFacebook profile){
        Log.d(TAG,"saveProfile ");
        interactor.saveProfile(profile);
    }

    private void getAccessToken(String facebook_token){
        interactor.getAccessToken(this,facebook_token);
    }

    @Override
    public void onToken(AccessToken accessToken) {
        view.displayToken(accessToken,profileFacebook.getFirst_name());
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
