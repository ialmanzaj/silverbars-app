package com.app.project.silverbars;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
//import com.spotify.sdk.android.authentication.AuthenticationClient;
//import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class PlaylistPickerActivity extends AppCompatActivity {
    private ListView ListMusic, ListPlaylist;
    private String[] items, songs, save_playlist;
    private Button clean,done;
    private long[] selected;
    private ArrayList<File> mySongs;
    private int Reps = 0, Tempo = 0;
    private String Playlist_name;
    private String[] playlist, position;

    private static String strSeparator = "__,__";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playlist__picker);
        ListPlaylist = (ListView)findViewById(R.id.SavedPlaylist);
        done = (Button)findViewById(R.id.done);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_add_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(PlaylistPickerActivity.this)
                        .title("Create a Playlist")
                        .content("Type the name of your playlist")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {dialog.dismiss();}
                        })
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Playlist name",null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                Playlist_name = input.toString(); // Do something
                                if (Objects.equals(Playlist_name, "")){
                                    Playlist_name = "Playlist 1";
                                }
                                Intent i = new Intent(PlaylistPickerActivity.this, SongsActivity.class);
                                startActivityForResult(i,1);
//                                MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
//                                database.insertPlaylist(Playlist_name,convertArrayToString(playlist),1);
//                                Log.v("Playlist",Arrays.toString(playlist));
//                                toast(Playlist_name);
//                                Intent returnIntent = new Intent();
//                                returnIntent.putExtra("positions",playlist);
//                                returnIntent.putExtra("songs",mySongs);
//                                setResult(RESULT_OK, returnIntent);
//                                finish();
                            }
                        }).show();
            }
        });

        clean = (Button)findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });

        mySongs = findSongs(Environment.getExternalStorageDirectory());
        if (mySongs.size() > 0) {
            items = new String[mySongs.size()];
            for (int i = 0; i < mySongs.size(); i++) {
                items[i] = SongName(mySongs.get(i));
            }
            getUsersPlaylist(1);

        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int spa2 = ListPlaylist.getCheckedItemPosition();
                if(spa2 != -1){
                    MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
                    int pos = ListPlaylist.getCheckedItemPosition();
                    String[] result = database.getPlaylist(pos+1);
                    String[] songs = convertStringToArray(result[2]);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("positions",songs);
                    returnIntent.putExtra("songs",mySongs);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                else{
                    mySongs = null;
                    finish();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
            MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
            database.insertPlaylist(Playlist_name,convertArrayToString(position),1);
            toast(Playlist_name);
            getUsersPlaylist(1);
            toast("Activity result");
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

    @Override
    public void onBackPressed(){
        finish();
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
    private String SongArtist(File file){
        String artist = null;
        try{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(this, uri);
            artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }catch(Exception e){
            Log.v("Exception",e.toString());
        }
        return artist;
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

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public String[] getPlaylist(int userId){
        String[] result;
        MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
        result = database.getUserPlaylists(userId);
        Log.v("Result",Arrays.toString(result));
        return result;
    }
    public static String[] convertStringToArray(String str){
        return str.split(strSeparator);
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getUsersPlaylist(int userId){
        if (getPlaylist(userId)!=null){
            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, getPlaylist(1));
            ListPlaylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            ListPlaylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ListPlaylist.setAdapter(adp2);
        }
        else{
            String[] noResult = new String[1];
            noResult[0] = "No Playlist";
            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, noResult);
            ListPlaylist.setAdapter(adp2);
        }
    }
}
