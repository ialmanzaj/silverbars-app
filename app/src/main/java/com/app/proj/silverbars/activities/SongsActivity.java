package com.app.proj.silverbars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SongsActivity extends AppCompatActivity {


    private static final String TAG = SongsActivity.class.getSimpleName();

    @BindView(R.id.music_selection) ListView mListSongsView;
    @BindView(R.id.done) Button done;
    
    
    ArrayList<File> songs = new ArrayList<>();
    
    
    private Utilities  utilities = new Utilities();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        ButterKnife.bind(this);
        

        ArrayList<File> local_songs = utilities.findSongs(this,Environment.getExternalStorageDirectory());

        
        if (local_songs.size() < 0) {
            utilities.toast(this,"No hay canciones");
            finish();
            return;
        }


        songs = utilities.deleteVoiceNote(local_songs);


        if (songs.size() < 0){
            utilities.toast(this,"No hay canciones");
            return;
        }


        String[] songs_names = new String[songs.size()];

        for (int i = 0; i < songs.size(); i++) {
            songs_names[i] = utilities.removeLastMp3(utilities.getSongName(this,songs.get(i)));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1, songs_names);


        mListSongsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListSongsView.setAdapter(adapter);
    }
    
    @OnClick(R.id.done)
    public void done(){
        
        if (songs.size() < 0){
           return;
        }

        int choice = mListSongsView.getCount();

        long[] selected = new long[choice];

        final SparseBooleanArray spa = mListSongsView.getCheckedItemPositions();

        if (spa.size() != 0) {
            String[] playlist = new String[mListSongsView.getCheckedItemCount()];
            int x = 0;
            for (int i = 0; i < choice; i++) {
                selected[i] = -1;
            }
            for (int i = 0; i < choice; i++) {
                if (spa.get(i)) {
                    selected[i] = mListSongsView.getItemIdAtPosition(i);
                }
            }
            for(int j = 0; j < songs.size(); j++){
                if (j == selected[j]){
                    playlist[x] = utilities.getSongName(SongsActivity.this,songs.get(j));
                    x++;
                }
            }

            //Log.v("playlist", Arrays.toString(playlist));
            //Log.v("songs", String.valueOf(songs));

            Intent returnIntent = new Intent();
            returnIntent.putExtra("positions", playlist);
            returnIntent.putExtra("songs",songs);
            setResult(RESULT_OK, returnIntent);
            finish();



        } else
            songs = null;
           
    }


}
