package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerSpotifyComponent;
import com.app.app.silverbarsapp.modules.SpotifyModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.SpotifyPresenter;
import com.app.app.silverbarsapp.viewsets.SpotifyView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import kaaes.spotify.webapi.android.models.UserPrivate;

import static com.app.app.silverbarsapp.Constants.REQUEST_CODE;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class SpotifyActivity extends BaseActivity implements SpotifyView{

    private static final String TAG = SpotifyActivity.class.getSimpleName();

    @Inject
    SpotifyPresenter mSpotifyPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.music_selection) ListView mListMusicSelection;
    @BindView(R.id.done) Button mDoneButton;
    @BindView(R.id.playlists) LinearLayout playlists_layout;

    @BindView(R.id.error_view)LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReloadButton;
    @BindView(R.id.loading)LinearLayout mLoadingView;

    @BindView(R.id.only_premium) LinearLayout mPremiumView;

    private ArrayAdapter<String> adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_spotify_music;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mSpotifyPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerSpotifyComponent
                .builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .spotifyModule(new SpotifyModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();

        //open login screen
        mSpotifyPresenter.openLoginWindow(this);

        setupAdapter();

        mReloadButton.setOnClickListener(view -> {});
        mDoneButton.setOnClickListener(view -> {});
    }

    public void setupToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Spotify");
        }
    }

    private void setupAdapter(){
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1);
        mListMusicSelection.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListMusicSelection.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                case TOKEN:
                    mSpotifyPresenter.onAuthenticationComplete(response);
                    break;
                case ERROR:
                    logStatus("TokenProvider error: " + response.getError());
                    onErrorViewOn();
                    break;
                default:
                    logStatus("TokenProvider result: " + response.getType());
            }
        }
    }

    @Override
    public void onMyProfile(UserPrivate userPrivate) {

        //loading off
        onLoadingOff();

        if (Objects.equals(userPrivate.product, "premium")) {
            onPremiumOn();
            return;
        }

        // flag set
        boolean isPremium = true;

        //get my playlist and my tracks
        mSpotifyPresenter.getMyPlaylist();
        mSpotifyPresenter.getMyTracks();
    }


    @Override
    public void getMyPlaylist(String[] playlist) {
        onLoadingOff();

        adapter.addAll(playlist);

        onMusicViewOn();
    }

    @Override
    public void displayMyTracks(String[] tracks) {
        onLoadingOff();
        adapter.addAll(tracks);
        onMusicViewOn();
    }

    @Override
    public void displayNetworkError() {}

    @Override
    public void displayServerError() {}


    private void onMusicViewOn(){
        playlists_layout.setVisibility(View.VISIBLE);
        mDoneButton.setVisibility(View.VISIBLE);
    }

    private void onLoadingOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onPremiumOn(){
        mPremiumView.setVisibility(View.VISIBLE);
    }

    private void logStatus(String status) {
        Log.i("SpotifySdkDemo", status);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

