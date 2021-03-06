package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.ProfileCallback;
import com.app.app.silverbarsapp.interactors.ProfileInteractor;
import com.app.app.silverbarsapp.viewsets.ProfileView;

import java.sql.SQLException;

import okhttp3.ResponseBody;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class ProfilePresenter extends BasePresenter implements ProfileCallback{

    private ProfileInteractor interactor;
    private ProfileView view;

    public ProfilePresenter(ProfileView view,ProfileInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getProfileImg(String id) throws SQLException {
        interactor.getProfileImage(id,this);
    }

    @Override
    public void getProfileImg(ResponseBody img) {
        view.displayProfileImg(img);
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
