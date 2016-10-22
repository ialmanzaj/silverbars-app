package com.app.proj.silverbars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SpotifyMusic extends AppCompatActivity implements  ConnectionStateCallback {

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


    private static final int REQUEST_CODE = 1337;
    private LinearLayout premium_error;

    String SpotifyToken;

    List<String> songs = new ArrayList<>();
    List<String> playlists = new ArrayList<>();

    Button done;
    LinearLayout playlists_layout,error_layout,progress_bar_;
    private ListView ListMusic;
    Button error_reload;

    Boolean auth_error = false,json_playlist_error = false,json_user_error = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_music);

        ListMusic = (ListView) findViewById(R.id.lvPlaylist);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Spotify");
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        premium_error = (LinearLayout) findViewById(R.id.only_premium);
        playlists_layout = (LinearLayout) findViewById(R.id.playlists);
        error_layout = (LinearLayout) findViewById(R.id.error_layout);
        error_reload = (Button) findViewById(R.id.error_reload_workout);

        progress_bar_ = (LinearLayout) findViewById(R.id.progress_bar_);

        error_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar_.setVisibility(View.VISIBLE);
                error_layout.setVisibility(View.GONE);

                if (auth_error){
                    openLoginWindow();

                }else if(json_user_error){

                    if (SpotifyToken != null){
                        getUserAuth(SpotifyToken);
                    }

                }else if (json_playlist_error){

                    if (SpotifyToken != null){
                        getPlaylist(SpotifyToken);
                    }

                }
            }
        });

        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int choice = ListMusic.getCount();
                long[] selected = new long[choice];
                final SparseBooleanArray spa = ListMusic.getCheckedItemPositions();
                if (spa.size() != 0) {

                    String currentPlaylist = null;
                    int x = 0;
                    for (int i = 0; i < choice; i++) {
                        selected[i] = -1;
                    }
                    for (int i = 0; i < choice; i++) {
                        if (spa.get(i)) {
                            selected[i] = ListMusic.getItemIdAtPosition(i);
                        }
                    }
                    for (int j = 0; j < playlists.size(); j++) {
                        if (j == selected[j]) {
                            Log.v(TAG, "playlist: " + playlists.get(j));
                            currentPlaylist = playlists.get(j);
                            x++;
                        }
                    }


                    Log.v("playlist elegido", currentPlaylist);
                    Log.v("playlists", String.valueOf(playlists));


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("playlist_spotify", currentPlaylist);
                    returnIntent.putExtra("token", SpotifyToken);
                    setResult(RESULT_OK, returnIntent);
                    finish();

                }
            }
        });


        openLoginWindow();

    }

    Boolean USERPREMIUM = false;

    private void getUserAuth(final String Token) {

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(Token);
        SpotifyService spotify = api.getService();


        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "failure" + spotifyError);
                error_layout.setVisibility(View.VISIBLE);
                json_user_error = true;
            }
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                Log.v(TAG, "userPrivate.product: " + userPrivate.product);

                if (Objects.equals(userPrivate.product, "premium")) {
                    USERPREMIUM = true;
                    getPlaylist(Token);

                }else {
                    premium_error.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void getPlaylist(String Token) {

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(Token);
        SpotifyService spotify = api.getService();

        spotify.getMyPlaylists(new SpotifyCallback<Pager<PlaylistSimple>>() {

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {

                String[] items = new String[playlistSimplePager.items.size()];
                for (int a = 0; a < playlistSimplePager.items.size(); a++) {
                    Log.v(TAG, "playlistSimplePager:" + playlistSimplePager.items.get(a).uri);
                    items[a] = playlistSimplePager.items.get(a).name;
                    playlists.add(playlistSimplePager.items.get(a).uri);
                }

                putElementsinList(items);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "SpotifyError: " + spotifyError);
                error_layout.setVisibility(View.VISIBLE);
                json_playlist_error = true;
            }
        });
        spotify.getMySavedTracks(new SpotifyCallback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> savedTrackPager, retrofit.client.Response response) {
                Log.v("Response size", String.valueOf(response.getHeaders().size()));
                Log.v("Pager size", String.valueOf(savedTrackPager.items.size()));

                String[] items = new String[savedTrackPager.items.size()];

                for (int a = 0; a < savedTrackPager.items.size(); a++) {
                    items[a] = savedTrackPager.items.get(a).track.name;
                    songs.add(savedTrackPager.items.get(a).track.uri);
                    Log.v(TAG, "SAVED: " + savedTrackPager.items.get(a).track.name);
                }
            }
            @Override
            public void failure(SpotifyError error) {
                Log.e(TAG, "SpotifyError: " + error);
                error_layout.setVisibility(View.VISIBLE);
                json_playlist_error = true;
            }
        });

    }

    private void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;
                case ERROR:
                    logStatus("TokenProvider error: " + response.getError());
                    error_layout.setVisibility(View.VISIBLE);
                    auth_error = true;
                    break;
                default:
                    logStatus("TokenProvider result: " + response.getType());
                    error_layout.setVisibility(View.VISIBLE);
                    auth_error = true;
            }
        }
    }


    private void onAuthenticationComplete(AuthenticationResponse authResponse) {

        progress_bar_.setVisibility(View.GONE);
        Log.v(TAG,"Got authentication token");

        SpotifyToken = authResponse.getAccessToken();

        getUserAuth(SpotifyToken);

        Log.v(TAG,"getAccessToken: "+authResponse.getAccessToken());
        Log.v(TAG,"getExpiresIn: "+authResponse.getExpiresIn());
    }


    private void putElementsinList(String[] items){
        progress_bar_.setVisibility(View.GONE);
        playlists_layout.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, items);
        ListMusic.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ListMusic.setAdapter(adp);
    }

    private void logStatus(String status) {
        Log.i("SpotifySdkDemo", status);
    }

    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming","user-library-read"})
                .build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
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
        error_layout.setVisibility(View.VISIBLE);
        auth_error = true;
    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "Temporary error occurred");
        error_layout.setVisibility(View.VISIBLE);
        auth_error = true;

    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);

    }


}