package com.app.proj.silverbars.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.app.proj.silverbars.activities.WorkingOutActivity;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.app.proj.silverbars.Constants.CLIENT_ID;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter implements Player.NotificationCallback, ConnectionStateCallback {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();

    public WorkingOutPresenter(){

    }

    private void configPlayerSpotify(String token){
        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), token, CLIENT_ID);

            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    Log.d(TAG,"-- Player initialized --");
                    mPlayer.setConnectivityStatus(mOperationCallback,getNetworkConnectivity(WorkingOutActivity.this));
                    mPlayer.addConnectionStateCallback(WorkingOutActivity.this);
                    mPlayer.addNotificationCallback(WorkingOutActivity.this);
                }
                @Override
                public void onError(Throwable error) {
                    Log.e(TAG,"Error in initialization: " + error.getMessage());
                }
            });
        } else {
            mPlayer.login(Token);
        }
    }

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            logSpotityStatus("OK!");
        }

        @Override
        public void onError(Error error) {
            logSpotityStatus("ERROR:" + error);
        }
    };


    public void startPlaySpotify(String uri_song) {
        logSpotityStatus("Starting playback for " + uri_song);
        mPlayer.playUri(mOperationCallback,uri_song,0,0);
    }

    private void updateView() {
        Log.d(TAG,"mCurrentPlaybackState: "+mCurrentPlaybackState);
        Log.d(TAG,"isLoggedIn: "+isLoggedIn());

        if (mMetadata != null && mMetadata.currentTrack != null) {
            song_name.setText(mMetadata.currentTrack.name);
            artist_name.setText(mMetadata.currentTrack.artistName);
            //final String durationStr = String.format(" (%dms)", mMetadata.currentTrack.durationMs);
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart");

        if (onPause){
            ResumeCountDown();
        }

    }



    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");

        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    Log.d(TAG,"Network state changed: " + connectivity.toString());
                    mPlayer.setConnectivityStatus(mOperationCallback,connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(this);
            mPlayer.addConnectionStateCallback(this);
        }

        if (onPause){
            ResumeCountDown();
        }

    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");

        PauseCountDown();
        onPause = true;

        unregisterReceiver(mNetworkStateReceiver);
        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(this);
            mPlayer.removeConnectionStateCallback(this);
        }
    }

    @Override
    public void onDestroy() {
        ScreenOff();

        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isActiveDevice){
            Spotify.destroyPlayer(this);}

        if (MAIN_TIMER && main_timer != null){main_timer.cancel();}

        if (media!=null){media.release();}

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

        logSpotityStatus("Player event: " + event);
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        mMetadata = mPlayer.getMetadata();
        logSpotityStatus("Player state: " + mCurrentPlaybackState);
        logSpotityStatus("Metadata: " + mMetadata);

        updateView();

    }

    @Override
    public void onPlaybackError(Error error) {
        logSpotityStatus("Player error: " + error);
    }

    private void logSpotityStatus(String status) {
        Log.i("SpotifySdkDemo", status);
    }
}
