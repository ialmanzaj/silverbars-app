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

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.presenters.SpotifyPresenter;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.app.proj.silverbars.Constants.REQUEST_CODE;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SpotifyActivity extends AppCompatActivity  {

    private static final String TAG = SpotifyActivity.class.getSimpleName();


    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.only_premium) LinearLayout mPremiumView;

    @BindView(R.id.done) Button mDoneButton;

    @BindView(R.id.playlists) LinearLayout playlists_layout;

    @BindView(R.id.error_layout)LinearLayout mErrorView;
    @BindView(R.id.loading)LinearLayout mLoadingView;
    @BindView(R.id.reload) Button mReloadButton;

    @BindView(R.id.music_selection) ListView mListMusicSelection;


    SpotifyPresenter mSpotifyPresenter;

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



        mReloadButton.setOnClickListener(view -> {


            mLoadingView.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);

            if (auth_error){
                mSpotifyPresenter.openLoginWindow(this);

            }else if(json_user_error){

                if (SpotifyToken != null){
                    mSpotifyPresenter.initService(SpotifyToken);
                }

            }else if (json_playlist_error){

                if (SpotifyToken != null){
                    getPlaylist(SpotifyToken);
                }

            }
        });


        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int choice = mListMusicSelection.getCount();
                long[] selected = new long[choice];
                final SparseBooleanArray spa = mListMusicSelection.getCheckedItemPositions();
                if (spa.size() != 0) {

                    String currentPlaylist = null;
                    int x = 0;
                    for (int i = 0; i < choice; i++) {
                        selected[i] = -1;
                    }
                    for (int i = 0; i < choice; i++) {
                        if (spa.get(i)) {
                            selected[i] = mListMusicSelection.getItemIdAtPosition(i);
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


        //open login screen
        mSpotifyPresenter.openLoginWindow(this);

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
                    mErrorView.setVisibility(View.VISIBLE);
                    auth_error = true;
                    break;
                default:
                    logStatus("TokenProvider result: " + response.getType());
                    mErrorView.setVisibility(View.VISIBLE);
                    auth_error = true;
            }
        }
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Spotify");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void onAuthenticationComplete(AuthenticationResponse authResponse) {

        mLoadingView.setVisibility(View.GONE);
        Log.v(TAG,"Got authentication token");

        SpotifyToken = authResponse.getAccessToken();

        getUserAuth(SpotifyToken);

        Log.v(TAG,"getAccessToken: "+authResponse.getAccessToken());
        Log.v(TAG,"getExpiresIn: "+authResponse.getExpiresIn());
    }


    private void putElementsinList(String[] items){
        mLoadingView.setVisibility(View.GONE);
        playlists_layout.setVisibility(View.VISIBLE);
        mDoneButton.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, items);
        mListMusicSelection.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListMusicSelection.setAdapter(adp);
    }



    private void logStatus(String status) {
        Log.i("SpotifySdkDemo", status);
    }


}