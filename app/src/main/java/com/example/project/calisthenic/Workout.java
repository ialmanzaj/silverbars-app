package com.example.project.calisthenic;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class Workout extends AppCompatActivity {

    ImageView star_on, star_off;
    Button workout, playlist;
    ArrayList<File> mySongs, play_list;
    long[] position;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.activity_workout, container, false);

        star_off = (ImageView) findViewById(R.id.star_off);
        star_on = (ImageView) findViewById(R.id.star_on);
        playlist = (Button) findViewById(R.id.playlist);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                S_playlist();
            }
        });
        workout =(Button) findViewById(R.id.workout);
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                working();
            }
        });
        star_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Favorite(view);
            }
        });

        star_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Unfavorite(view);
            }
        });

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (getIntent().hasExtra("songlist") && getIntent().hasExtra("pos")){
            mySongs = (ArrayList) b.getParcelableArrayList("songlist");
            position = b.getLongArray("pos");
        }

//        return rootView;
    }

    public void Favorite(View v){
        star_off.setVisibility(v.GONE);
        star_on.setVisibility(v.VISIBLE);
    }

    public void Unfavorite(View v){
        star_on.setVisibility(v.GONE);
        star_off.setVisibility(v.VISIBLE);
    }

    public void working() {
        Intent intent = new Intent(this, WorkingOut.class);
        intent.putExtra("pos",position);
        intent.putExtra("songlist",mySongs);
        startActivity(intent);

    }

    public void S_playlist() {
        Intent intent = new Intent(this, Playlist_Picker.class);
        startActivity(intent);
        finish();
    }

}
