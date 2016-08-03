package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Music selection");

            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

        RelativeLayout songsButton = (RelativeLayout) findViewById(R.id.songs);
        RelativeLayout playlistButton = (RelativeLayout) findViewById(R.id.playlist);
        RelativeLayout spotifyButton = (RelativeLayout) findViewById(R.id.spotify);
        RelativeLayout soundcloudButton = (RelativeLayout) findViewById(R.id.soundcloud);

//        songsButton.getLayoutParams().width = deviceWidth(SelectionMusicActivity.this);
//        songsButton.getLayoutParams().height = deviceHeight(SelectionMusicActivity.this);
//
//        playlistButton.getLayoutParams().width = deviceWidth(SelectionMusicActivity.this);
//        playlistButton.getLayoutParams().height = deviceHeight(SelectionMusicActivity.this);
//
//        spotifyButton.getLayoutParams().width = deviceWidth(SelectionMusicActivity.this);
//        spotifyButton.getLayoutParams().height = deviceHeight(SelectionMusicActivity.this);
//
//        soundcloudButton.getLayoutParams().width = deviceWidth(SelectionMusicActivity.this);
//        soundcloudButton.getLayoutParams().height = deviceHeight(SelectionMusicActivity.this);

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

    public static int deviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.x)/2;
    }

    public static int deviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.y)/2;
    }

}
