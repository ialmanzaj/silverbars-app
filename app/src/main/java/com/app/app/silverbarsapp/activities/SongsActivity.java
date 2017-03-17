package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.utils.Utilities;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SongsActivity extends AppCompatActivity {

    private static final String TAG = SongsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.music_selection) ListView mListSongsView;
    @BindView(R.id.done) Button mDoneButton;

    @BindView(R.id.empty_state) LinearLayout mEmptyStateView;

    private ArrayList<File> local_audio;
    private ArrayList<File> songs = new ArrayList<>();

    private Utilities  utilities = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        ButterKnife.bind(this);

        local_audio = utilities.findSongs(this, Environment.getExternalStorageDirectory());

        setupToolbar();
        songValidation();
        setupAdapter();
    }

    private void setupAdapter(){
        String[] songs_names = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            songs_names[i] = utilities.removeLastMp3(utilities.getSongName(this,songs.get(i)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, songs_names);

        mListSongsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListSongsView.setAdapter(adapter);
    }

    private void songValidation(){

        if (local_audio.size() < 1) {
            onEmptyViewOn();
            return;
        }

        songs = utilities.deleteVoiceNote(local_audio);

        if (songs.size() < 1){
            onEmptyViewOn();
            return;
        }


    }



    @OnClick(R.id.done)
    public void doneButton(){

        songValidation();

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


            Intent returnIntent = new Intent();
            returnIntent.putExtra("positions", playlist);
            returnIntent.putExtra("songs",songs);
            setResult(RESULT_OK, returnIntent);
            finish();

        } else
            songs = null;
    }

    private void setupToolbar(){
        if ( mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Song selection");
        }
    }

    private void onEmptyViewOn(){
        mEmptyStateView.setVisibility(View.VISIBLE);
    }

    private void onEmptyViewOff(){
        mEmptyStateView.setVisibility(View.GONE);
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
