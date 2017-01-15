package com.app.proj.silverbars.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.app.proj.silverbars.utils.Utilities;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.app.proj.silverbars.Constants.CLIENT_ID;

/**
 * Created by isaacalmanza on 01/15/17.
 */

public class CustomSpotifyPlayer implements Player.NotificationCallback, ConnectionStateCallback {


    private static final String TAG = CustomSpotifyPlayer.class.getSimpleName();

    //spotify vars
    private SpotifyPlayer mSpotifyPlayer;
    private PlaybackState mCurrentPlaybackState;
    private BroadcastReceiver mNetworkStateReceiver;
    private Metadata mMetadata;

    //spotify vars
    private String spotify_playlist;
    private Context context;
    private String spotify_token;

    private Utilities utilities = new Utilities();


    public void CustomSpotifyPlayer(Context context,String spotify_token,String spotify_playlist){
        this.context = context;
        this.spotify_token = spotify_token;
        this.spotify_playlist = spotify_playlist;

    }

    public void setupSpotifyPlayer(){
        configPlayerSpotify(spotify_token);
    }

    public void onPauseSpotifyPlayer(){
        if (mCurrentPlaybackState.isPlaying){
            mSpotifyPlayer.pause(mOperationCallback);
        }
    }

    private void startSpotifyPlayer(String uri_song) {
        logSpotityStatus("Starting playback for " + uri_song);
        mSpotifyPlayer.playUri(mOperationCallback,uri_song,0,0);
    }


    public void onPlaySpotifyPlayer(){
        if (mCurrentPlaybackState.isActiveDevice){

            resumeSpotifyPlayer();

        }else {

            //restarting
            startSpotifyPlayer(spotify_playlist);

            //update music ui
            view.onPlayMusic();
        }
    }

    private void resumeSpotifyPlayer(){
        mSpotifyPlayer.resume(mOperationCallback);

        //update music ui
        view.onPlayMusic();
    }


    private void configPlayerSpotify(String spotify_token){
        if (mSpotifyPlayer == null) {
            Config playerConfig = new Config(context.getApplicationContext(), spotify_token, CLIENT_ID);

            mSpotifyPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {

                    initPlayer();

                }
                @Override
                public void onError(Throwable error) {
                    Log.e(TAG,"Error in initialization: " + error.getMessage());
                }
            });
        } else {
            mSpotifyPlayer.login(spotify_token);
        }
    }



    private void initPlayer(){
        Log.d(TAG,"-- Player initialized --");
        mSpotifyPlayer.setConnectivityStatus(mOperationCallback,utilities.getNetworkConnectivity(context));
        mSpotifyPlayer.addConnectionStateCallback(this);
        mSpotifyPlayer.addNotificationCallback(this);

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


    @Override
    public void onPlaybackError(Error error) {
        logSpotityStatus("Player error: " + error);
    }

    private void logSpotityStatus(String status) {
        Log.i("SpotifySdkDemo", status);
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
    public void onPlaybackEvent(PlayerEvent event) {

        logSpotityStatus("Player event: " + event);
        mCurrentPlaybackState = mSpotifyPlayer.getPlaybackState();
        mMetadata = mSpotifyPlayer.getMetadata();
        logSpotityStatus("Player state: " + mCurrentPlaybackState);
        logSpotityStatus("Metadata: " + mMetadata);

        updateView();

    }

    private void updateView() {
        Log.d(TAG,"mCurrentPlaybackState: "+mCurrentPlaybackState);
        Log.d(TAG,"isLoggedIn: "+isLoggedIn());

        if (mMetadata != null && mMetadata.currentTrack != null) {


            //UPDATE UI
            view.updateSongName(mMetadata.currentTrack.name);
            view.updateSongName(mMetadata.currentTrack.artistName);
            //final String durationStr = String.format(" (%dms)", mMetadata.currentTrack.durationMs);
        }
    }


    private boolean isLoggedIn() {
        return mSpotifyPlayer != null && mSpotifyPlayer.isLoggedIn();
    }


    public void onResume(){
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mSpotifyPlayer != null) {
                    Connectivity connectivity = utilities.getNetworkConnectivity(context);
                    Log.d(TAG,"Network state changed: " + connectivity.toString());
                    mSpotifyPlayer.setConnectivityStatus(mOperationCallback,connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetworkStateReceiver, filter);

        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.addNotificationCallback(this);
            mSpotifyPlayer.addConnectionStateCallback(this);
        }

    }

    public void onPause(){
        context.unregisterReceiver(mNetworkStateReceiver);
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.removeNotificationCallback(this);
            mSpotifyPlayer.removeConnectionStateCallback(this);
        }
    }



    public void onDestroy(){
        if (mCurrentPlaybackState.isActiveDevice){
            Spotify.destroyPlayer(this);
        }
    }

}
