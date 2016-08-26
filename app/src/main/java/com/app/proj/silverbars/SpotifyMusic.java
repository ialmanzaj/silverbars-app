package com.app.proj.silverbars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;

/*import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;*/

public class SpotifyMusic extends AppCompatActivity implements  Player.NotificationCallback, ConnectionStateCallback {

    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "20823679749441aeacf4e601f7d12270";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "testschema://callback";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_SONG_URI = "spotify:track:6KywfgRqvgvfJc3JRwaZdZ";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_SONG_MONO_URI = "spotify:track:1FqY3uJypma5wkYw66QOUi";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_SONG_48kHz_URI = "spotify:track:3wxTNS3aqb9RbBLZgJdZgH";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_PLAYLIST_URI = "spotify:user:sqook:playlist:0BZvnsfuqmnLyj6WVRuSte";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_ALBUM_URI = "spotify:album:2lYmxilk8cXJlxxXmns1IU";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_QUEUE_SONG_URI = "spotify:track:5EEOjaJyWvfMglmEwf9bG3";
    private static final String TAG = "SpotifyMusic";


    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;
    private BroadcastReceiver mNetworkStateReceiver;

    private static final int REQUEST_CODE = 1337;
    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);


    public static Intent createIntent(Context context) {
        return new Intent(context, SpotifyMusic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_music);

        Intent intent = getIntent();
        String token = intent.getStringExtra(EXTRA_TOKEN);
        SavedTracks(token);


        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();


        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

//        mActionListener = new SearchPresenter(this, this);
//        mActionListener.init(token);
//
//        // Setup search field
//        final SearchView searchView = (SearchView) findViewById(R.id.search_view);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                mActionListener.search(query);
//                searchView.clearFocus();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });


        // Setup search results list

        RecyclerView resultsList = (RecyclerView) findViewById(R.id.search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(mLayoutManager);
//        resultsList.setAdapter(mAdapter);
//        resultsList.addOnScrollListener(mScrollListener);
    }

    public void SavedTracks(String Token){
        SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
        api.setAccessToken(Token);

        SpotifyService spotify = api.getService();

        spotify.getMySavedTracks(new SpotifyCallback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> savedTrackPager, retrofit.client.Response response) {
                Log.v("Response size",String.valueOf(response.getHeaders().size()));
                Log.v("Pager size",String.valueOf(savedTrackPager.items.size()));
            }

            @Override
            public void failure(SpotifyError error) {
                // handle error
            }
        });
    }

    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Set up the broadcast receiver for network events. Note that we also unregister
        // this receiver again in onPause().
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    Log.v(TAG,"Network state changed: " + connectivity.toString());
                    mPlayer.setConnectivityStatus(connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(SpotifyMusic.this);
            mPlayer.addConnectionStateCallback(SpotifyMusic.this);
        }
    }



    private void updateView() {
        boolean loggedIn = isLoggedIn();
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        Log.v(TAG,"Got authentication token");
        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
            // the second argument in order to refcount it properly. Note that the method
            // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
            // one passed in here. If you pass different instances to Spotify.getPlayer() and
            // Spotify.destroyPlayer(), that will definitely result in resource leaks.
            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    Log.v(TAG,"-- Player initialized --");
                    mPlayer.setConnectivityStatus(getNetworkConnectivity(SpotifyMusic.this));
                    mPlayer.addNotificationCallback(SpotifyMusic.this);
                    mPlayer.addConnectionStateCallback(SpotifyMusic.this);
                    // Trigger UI refresh
                    updateView();
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG,"Error in initialization: " + error.getMessage());
                }
            });
        } else {
            mPlayer.login(authResponse.getAccessToken());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == REQUEST_CODE) {


            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.v(TAG,"Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.v(TAG,"Auth result: " + response.getType());
            }


        }
    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }

    public void onLoginButtonClicked(View view) {
        if (!isLoggedIn()) {
            Log.v(TAG,"Logging in");
            openLoginWindow();
        } else {
            mPlayer.logout();
        }
    }

    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"})
                .build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");

    }

    @Override
    public void onLoginFailed(int i) {
        Log.d("MainActivity", "Login failed");

    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");

    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());

    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());

    }


}