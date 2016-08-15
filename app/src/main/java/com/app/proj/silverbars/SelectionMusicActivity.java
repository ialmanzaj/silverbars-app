package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SelectionMusicActivity extends AppCompatActivity {
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

                try {
                    ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());

                    if (mySongs.size() > 0) {

                        String[] items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size(); i++) {
                            items[i] = SongName(mySongs.get(i));
                        }

                        ArrayAdapter<String> adp = new ArrayAdapter<String>(SelectionMusicActivity.this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items);
                        startActivityForResult(new Intent(getApplicationContext(), SongsActivity.class), 1);
                    }

                }catch (NullPointerException e){
                    toast("no hay canciones");
                }


            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), PlaylistPickerActivity.class), 1);

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
        String[] position;
        ArrayList<File> mySongs;
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("positions", position);
            returnIntent.putExtra("songs", mySongs);
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

    public ArrayList<File> findSongs(File root){

        ArrayList<File> al = new ArrayList<File>();
        if (root.listFiles() != null){
            File[] files = root.listFiles();
            for(File singleFile : files){
                if (singleFile.isDirectory() && !singleFile.isHidden()){
                    al.addAll(findSongs(singleFile));
                }
                else{
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        if (SongDuration(singleFile)!=null && Long.valueOf(SongDuration(singleFile))>150000)
                            al.add(singleFile);
                    }
                }
            }
        }
        return al;
    }

    private String SongDuration(File file){
        String duration = null;
        try{MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(this, uri);
            duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        }catch (Exception e){
            Log.v("Exception",e.toString());
        }
        return duration;
    }

    private String SongName(File file){
        String title = null;
        try{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(this, uri);
            title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }catch(Exception e){
            Log.v("Exception",e.toString());
        }
        return title;
    }




}
