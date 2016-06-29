package com.app.project.silverbars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectionMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_music);


        GridLayout grid = (GridLayout) findViewById(R.id.gridlayout);

        LinearLayout songsButton = (LinearLayout) findViewById(R.id.songs);
        LinearLayout playlistButton = (LinearLayout) findViewById(R.id.playlist);
        LinearLayout spotifyButton = (LinearLayout) findViewById(R.id.spotify);
        LinearLayout soundcloudButton = (LinearLayout) findViewById(R.id.soundcloud);

        songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlaylistPickerActivity.class));
            }
        });
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        soundcloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
