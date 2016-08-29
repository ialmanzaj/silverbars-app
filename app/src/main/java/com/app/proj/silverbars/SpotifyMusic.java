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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.SavedTrack;
import retrofit.client.Response;


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


    String SpotifyToken;

    List<String> songs = new ArrayList<>();
    List<String> playlists = new ArrayList<>();


    private ListView ListMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_music);

        ListMusic = (ListView)findViewById(R.id.lvPlaylist);


        Button done = (Button) findViewById(R.id.done);

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
                            Log.v(TAG,"playlist: "+playlists.get(j));
                            currentPlaylist = playlists.get(j);
                            x++;
                        }
                    }


                    Log.v("playlist elegido", currentPlaylist);
                    Log.v("playlists", String.valueOf(playlists));



                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("playlist_spotify",currentPlaylist);
                    returnIntent.putExtra("token",SpotifyToken);
                    setResult(RESULT_OK, returnIntent);
                    finish();

                }
            }
        });

        openLoginWindow();

    }



    public void SavedTracks(String Token){
        SpotifyApi api = new SpotifyApi();

        api.setAccessToken(Token);

        SpotifyService spotify = api.getService();

        spotify.getMyPlaylists(new SpotifyCallback<Pager<PlaylistSimple>>() {

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {

                String[] items = new String[playlistSimplePager.items.size()];

                for (int a = 0;a<playlistSimplePager.items.size();a++){
                    Log.v(TAG,"playlistSimplePager:"+playlistSimplePager.items.get(a).uri);

                    items[a] = playlistSimplePager.items.get(a).name;
                    playlists.add(playlistSimplePager.items.get(a).uri);

                }

                putElementsinList(items);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG,"SpotifyError: "+spotifyError);
            }

        });
        spotify.getMySavedTracks(new SpotifyCallback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> savedTrackPager, retrofit.client.Response response) {
                Log.v("Response size",String.valueOf(response.getHeaders().size()));
                Log.v("Pager size",String.valueOf(savedTrackPager.items.size()));

                String[] items = new String[savedTrackPager.items.size()];

                for (int a = 0;a<savedTrackPager.items.size();a++){
                    items[a] = savedTrackPager.items.get(a).track.name;
                    songs.add(savedTrackPager.items.get(a).track.uri);
                    Log.v(TAG,"SAVED: "+savedTrackPager.items.get(a).track.name);
                }

            }
            @Override
            public void failure(SpotifyError error) {
               Log.e(TAG,"SpotifyError: "+error);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

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
                    logStatus("Auth error: " + response.getError());
                    break;
                default:
                    logStatus("Auth result: " + response.getType());
            }


        }
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        Log.v(TAG,"Got authentication token");

        SavedTracks(authResponse.getAccessToken());
        SpotifyToken = authResponse.getAccessToken();

        Log.v(TAG,"getAccessToken: "+authResponse.getAccessToken());
        Log.v(TAG,"getExpiresIn: "+authResponse.getExpiresIn());
        Log.v(TAG,"class: "+authResponse.getAccessToken().getClass().getName());

    }




    private void putElementsinList(String[] items){

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items);
        ListMusic.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
    protected void onDestroy() {

        super.onDestroy();
    }

}