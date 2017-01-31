package com.app.proj.silverbars.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.app.proj.silverbars.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class SelectionMusicActivity extends AppCompatActivity {


    private static final String TAG = SelectionMusicActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.songs) RelativeLayout songsButton;
    @BindView(R.id.playlist) RelativeLayout playlistButton;
    @BindView(R.id.spotify) RelativeLayout spotifyButton;
    @BindView(R.id.soundcloud) RelativeLayout soundcloudButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_music);
        ButterKnife.bind(this);

        setupToolbar();


        songsButton.setOnClickListener(v ->
                startActivityForResult(new Intent(SelectionMusicActivity.this, SongsActivity.class), 1));
        playlistButton.setOnClickListener(v ->
                startActivityForResult(new Intent(SelectionMusicActivity.this, PlaylistPickerActivity.class), 1));
        spotifyButton.setOnClickListener(v ->
                startActivityForResult(new Intent(SelectionMusicActivity.this, SpotifyActivity.class), 1));
    }



    private void setupToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){


            if (data.hasExtra("playlist_spotify") && data.hasExtra("token")){

                String playlist_spotify = data.getStringExtra("playlist_spotify");
                String token =  data.getStringExtra("token");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("playlist_spotify",playlist_spotify);
                returnIntent.putExtra("token",token);
                setResult(RESULT_OK,returnIntent);
                finish();

            }else if (data.hasExtra("songs") && data.hasExtra("positions")){

                ArrayList<File> mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
                String[] position = data.getStringArrayExtra("positions");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("positions", position);
                returnIntent.putExtra("songs", mySongs);
                setResult(RESULT_OK, returnIntent);
                finish();

            }


        }
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
