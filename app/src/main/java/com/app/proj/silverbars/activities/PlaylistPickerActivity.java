package com.app.proj.silverbars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class PlaylistPickerActivity extends AppCompatActivity {

    private static final String TAG = PlaylistPickerActivity.class.getSimpleName();


    @BindView(R.id.playlist) ListView ListPlaylist;
    
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.done) Button mDoneBt;
    @BindView(R.id.create_playlist)ImageView mCreatePlaylistBt;


    @BindView(R.id.empty_state) LinearLayout mEmptyStateView;


    private String[] save_playlist;
    
    private long[] selected;
    

    String[] position;

    private String Playlist_name;
    private String[] playlist;


    private ArrayList<File> local_audio;
    private ArrayList<File> songs;


    private Utilities utilities = new Utilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist__picker);
        ButterKnife.bind(this);


        //mCreatePlaylistBt.setOnClickListener(v -> dialog());

        local_audio = utilities.findSongs(this,Environment.getExternalStorageDirectory());

        songValidation();



      /*  String[] items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = SongName(mySongs.get(i));
        }
        getUsersPlaylist(1);
        */




       /* mDoneBt.setOnClickListener(view -> {
            final int selected1 = ListPlaylist.getCheckedItemPosition();
            Log.v(TAG,"selected: "+ selected1);

           *//* if(selected != -1){
                MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
                int pos = ListPlaylist.getCheckedItemPosition();
                String[] result = database.getPlaylist(pos+1);
                String[] positions = convertStringToArray(result[2]);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("positions",positions);
                returnIntent.putExtra("songs",mySongs);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            else{
                mySongs = null;
                finish();
            }*//*
        });*/

    }

    private void songValidation(){

        if (local_audio.size() < 1) {
            onEmptyStateOn();
            return;
        }

        songs = utilities.deleteVoiceNote(local_audio);

        if (songs.size() < 1){
            onEmptyStateOn();
            return;
        }

    }

    private void onEmptyStateOn(){
        mEmptyStateView.setVisibility(View.VISIBLE);
    }

    private void onEmptyStateOff(){
        mEmptyStateView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){

           /* mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
            //MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
            //database.insertPlaylist(Playlist_name,convertArrayToString(position),1);
            toast(Playlist_name);
            getUsersPlaylist(1);
            toast("Activity result");
            */

        } else if (requestCode == 1 && resultCode == RESULT_CANCELED) {

        }
    }



  /*  public String[] getPlaylist(int userId){
        String[] result;
        //MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
        result = database.getUserPlaylists(userId);
        Log.v("Result",Arrays.toString(result));
        return result;
    }*/

    /*private void getUsersPlaylist(int userId){
        if (getPlaylist(userId)!=null){
            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, getPlaylist(1));
            ListPlaylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            ListPlaylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            ListPlaylist.setAdapter(adp2);
        }
        else{
            String[] noResult = new String[1];
            noResult[0] = getResources().getString(R.string.empty_playlist);
            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, noResult);
            ListPlaylist.setAdapter(adp2);
        }
    }*/

    private void dialog(){
/*
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.create_playlist))
                .content(getResources().getString(R.string.title_playlist))
                .negativeText(getResources().getString(R.string.cancel))
                .onNegative((dialog, which) -> dialog.dismiss())
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.playlist_name),null, (dialog, input) -> {
                    Playlist_name = input.toString(); // Do something
                    if (Objects.equals(Playlist_name, "")){
                        Playlist_name = "Playlist 1";
                    }
                    Intent i = new Intent(PlaylistPickerActivity.this, SongsActivity.class);
                    startActivityForResult(i,1);
                }).show();
        */

    }


}
