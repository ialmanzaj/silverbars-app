package com.app.proj.silverbars.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.proj.silverbars.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class SelectionMusicActivity extends AppCompatActivity {


    private static final String TAG = SelectionMusicActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar myToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_music);


        RelativeLayout songsButton = (RelativeLayout) findViewById(R.id.songs);
        RelativeLayout playlistButton = (RelativeLayout) findViewById(R.id.playlist);
        RelativeLayout spotifyButton = (RelativeLayout) findViewById(R.id.spotify);
        RelativeLayout soundcloudButton = (RelativeLayout) findViewById(R.id.soundcloud);


        songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(SelectionMusicActivity.this, SongsActivity.class), 1);

            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectionMusicActivity.this, PlaylistPickerActivity.class), 1);

            }
        });
        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectionMusicActivity.this, SpotifyActivity.class), 1);

            }
        });
        soundcloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupToolbar(){
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.music_selection));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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




}
