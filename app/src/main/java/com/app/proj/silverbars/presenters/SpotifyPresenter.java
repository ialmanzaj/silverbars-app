package com.app.proj.silverbars.presenters;

import android.app.Activity;
import android.util.Log;

import com.app.proj.silverbars.callbacks.CustomSpotifyCallback;
import com.app.proj.silverbars.interactors.SpotifyInteractor;
import com.app.proj.silverbars.viewsets.SpotifyView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;

import kaaes.spotify.webapi.android.models.UserPrivate;

import static com.app.proj.silverbars.Constants.CLIENT_ID;
import static com.app.proj.silverbars.Constants.REDIRECT_URI;
import static com.app.proj.silverbars.Constants.REQUEST_CODE;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class SpotifyPresenter extends BasePresenter implements ConnectionStateCallback,CustomSpotifyCallback {


    private static final String TAG = SpotifyPresenter.class.getSimpleName();

    private SpotifyInteractor interactor;
    private SpotifyView view;


    public SpotifyPresenter(SpotifyView view, SpotifyInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }


    public void openLoginWindow(Activity activity) {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming","user-library-read"})
                .build();
        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }


    public void onAuthenticationComplete(AuthenticationResponse authResponse) {
        Log.v(TAG,"Got authentication token");

        String token  = authResponse.getAccessToken();


        interactor.initService(token);
        interactor.getMyProfile(this);
    }


    public void getMyPlaylist(){
        interactor.getMyPlaylists(this);
    }

    public void getMyTracks(){
        interactor.getMyTracks(this);
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
    public void onDestroy() {

    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d(TAG, "Login failed");
    }


    @Override
    public void onTemporaryError() {
        Log.d(TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);
    }


    @Override
    public void onMyProfile(UserPrivate userPrivate) {
        view.onMyProfile(userPrivate);
    }

    @Override
    public void onMyPlaylist(String[] playlist) {
        view.getMyPlaylist(playlist);
    }

    @Override
    public void onMyTracks(String[] tracks) {
        view.displayMyTracks(tracks);
    }
}
