package com.app.proj.silverbars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.app.proj.silverbars.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

import static com.app.proj.silverbars.Constants.CLIENT_ID;
import static com.app.proj.silverbars.Constants.REDIRECT_URI;
import static com.app.proj.silverbars.Constants.REQUEST_CODE;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SpotifyActivity extends AppCompatActivity  {

    private static final String TAG = SpotifyActivity.class.getSimpleName();


    @BindView(R.id.toolbar) Toolbar myToolbar;

    @BindView(R.id.only_premium) LinearLayout premium_error;
    @BindView(R.id.done) Button done;
    @BindView(R.id.playlists) LinearLayout playlists_layout;
    @BindView(R.id.error_layout)LinearLayout error_layout;
    @BindView(R.id.progress_bar_)LinearLayout progress_bar_;
    @BindView(R.id.lvPlaylist) ListView ListMusic;
    @BindView(R.id.error_reload_workout) Button error_reload;



    String SpotifyToken;

    List<String> songs = new ArrayList<>();
    List<String> playlists = new ArrayList<>();

    Boolean USERPREMIUM = false;
    Boolean auth_error = false,json_playlist_error = false,json_user_error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_music);


        setupToolbar();



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

    private void setupToolbar(){
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Spotify");

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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





}