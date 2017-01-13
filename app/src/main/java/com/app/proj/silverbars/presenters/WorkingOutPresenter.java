package com.app.proj.silverbars.presenters;

import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter implements Player.NotificationCallback, ConnectionStateCallback {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();

    public WorkingOutPresenter(){

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

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
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {
        logSpotityStatus("Player error: " + error);
    }

    private void logSpotityStatus(String status) {
        Log.i("SpotifySdkDemo", status);
    }
}
