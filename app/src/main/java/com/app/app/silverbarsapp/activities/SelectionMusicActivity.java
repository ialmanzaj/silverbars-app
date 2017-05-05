package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.app.app.silverbarsapp.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class SelectionMusicActivity extends AppCompatActivity {

    private static final String TAG = SelectionMusicActivity.class.getSimpleName();

    private static final int LOCAL_MUSIC = 1;
    private static final int SPOTIFY_MUSIC = 2;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.songs) RelativeLayout mLocalSongsSelectionButton;
    @BindView(R.id.playlist) RelativeLayout mCreateLocalPlaylistButton;
    @BindView(R.id.spotify) RelativeLayout mSpotifyMusicButton;
    @BindView(R.id.soundcloud) RelativeLayout soundcloudButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_music);
        ButterKnife.bind(this);

        setupToolbar();

        mLocalSongsSelectionButton.setOnClickListener(v -> startActivityForResult(new Intent(this, SongsActivity.class), LOCAL_MUSIC));
        mCreateLocalPlaylistButton.setOnClickListener(v -> startActivityForResult(new Intent(this, PlaylistPickerActivity.class), LOCAL_MUSIC));
    }

    private void setupToolbar(){
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Music Selection");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCAL_MUSIC){
            if (resultCode == RESULT_OK){

                if (data.hasExtra("songs") && data.hasExtra("positions")){

                    ArrayList<File> mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
                    String[] position = data.getStringArrayExtra("positions");

                    Intent return_intent = new Intent();
                    return_intent.putExtra("positions", position);
                    return_intent.putExtra("songs", mySongs);
                    setResult(RESULT_OK, return_intent);
                    finish();
                }
            }
        }
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
