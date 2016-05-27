package com.example.project.calisthenic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlaylistPickerActivity extends AppCompatActivity {
    private ListView ListMusic;
    private String[] items, songs;
    private Button clean,done;
    private long[] selected;
    private ArrayList<File> mySongs;
    private int Reps = 0, Tempo = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (intent.hasExtra("reps") && intent.hasExtra("tempo")){
            Reps = b.getInt("reps");
            Tempo = b.getInt("tempo");
        }

        setContentView(R.layout.activity_playlist__picker);


        ListMusic = (ListView)findViewById(R.id.lvPlaylist);

        clean = (Button)findViewById(R.id.clean);

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mySongs = findSongs(Environment.getExternalStorageDirectory());
        if (mySongs.size() > 0) {
            items = new String[mySongs.size()];
            for (int i = 0; i < mySongs.size(); i++) {

                items[i] = mySongs.get(i).getName();
            }
            ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items);


            ListMusic.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            ListMusic.setAdapter(adp);

            done = (Button)findViewById(R.id.done);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int choice = ListMusic.getCount();
                    selected = new long[choice];
                    songs = new String[choice];
                    SparseBooleanArray spa = ListMusic.getCheckedItemPositions();
                    if (spa.size() < 1) {
                        mySongs = null;
                    }
                    for (int i = 0; i < choice; i++) {
                        selected[i] = -1;
                    }
                    for (int i = 0; i < choice; i++) {
                        if (spa.get(i)) {
                            selected[i] = ListMusic.getItemIdAtPosition(i);
                        }
                    }

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("positions",selected);
                    returnIntent.putExtra("songs",mySongs);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public ArrayList<File> findSongs(File root){
        ArrayList<File> al = new ArrayList<File>();

        if (root.listFiles() != null){
            File[] files = root.listFiles();
//            Log.v("Files",files+", ");
            for(File singleFile : files){
                if (singleFile.isDirectory() && !singleFile.isHidden()){
                    al.addAll(findSongs(singleFile));
                }
                else{
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        al.add(singleFile);
                    }
                }
            }
        }


        return al;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private String SongName(File file){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(file);
        mediaMetadataRetriever.setDataSource(this, uri);
        String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        return title;
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
