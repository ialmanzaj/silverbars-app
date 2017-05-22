package com.app.app.silverbarsapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.utils.MusicService;
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

    private Utilities utilities = new Utilities();
    private ArrayList<File> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        //bind view
        ButterKnife.bind(this);
        setupToolbar();

        //service to know all the music in the phone
        startService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_COMPLETED);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MusicService.ACTION_COMPLETED)) {
                songs = (ArrayList<File>) intent.getSerializableExtra("songs");
                setupAdapter(songs);
            }
        }
    };

    private void setupAdapter(ArrayList<File> songs){
        if (songs.size() > 0) {

            String[] songs_names = utilities.getSongsNamesBySongsFiles(this,songs);
            mListSongsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mListSongsView.setAdapter(new ArrayAdapter<>(this, R.layout.simple_list_item_multiple_choice, android.R.id.text1, songs_names));

        }else
            onEmptyViewOn();
    }

    @OnClick(R.id.done)
    public void doneButton(){
        int[] playlist = utilities.getPositionsSelected(mListSongsView,songs.size());

        if (playlist == null){
            utilities.toast(this,getString(R.string.activity_song_error_no_music));
            return;
        }

        Intent return_Intent = new Intent();
        return_Intent.putExtra("songs",utilities.getFilesSelectedByPositions(playlist,songs));
        setResult(RESULT_OK, return_Intent);
        finish();
    }

    private void setupToolbar(){
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.activity_song_title));
        }
    }


    /**
     *
     *
     *
     *
     *     UI events
     *<p>
     *
     *
     *
     *
     *
     */

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
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
