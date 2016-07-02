package com.app.project.silverbars;

import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.SearchManager;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.View;

/*import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;*/

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.*;
import kaaes.spotify.webapi.android.models.Track;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyMusic extends AppCompatActivity {

    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";

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


}