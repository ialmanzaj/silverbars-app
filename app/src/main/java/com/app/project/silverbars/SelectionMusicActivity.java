package com.app.project.silverbars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SelectionMusicActivity extends AppCompatActivity {
    private String[] position;
    private ArrayList<File> mySongs;
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
                startActivityForResult(new Intent(getApplicationContext(), SongsActivity.class),1);
            }
        });
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), PlaylistPickerActivity.class),1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("positions",position);
            returnIntent.putExtra("songs",mySongs);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            mySongs = null;
            position = null;
            toast("No result");
        }
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

}
