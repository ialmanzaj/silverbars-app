package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class PlaylistPickerActivity extends AppCompatActivity {
    private ListView ListMusic, ListPlaylist;
    private String[] items, songs, save_playlist;
    private Button clean,done;
    private long[] selected;
    private ArrayList<File> mySongs;
    private int Reps = 0, Tempo = 0;
    private String Playlist_name;
    private String[] playlist;
    private static String strSeparator = "__,__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playlist__picker);

        ListMusic = (ListView)findViewById(R.id.lvPlaylist);
        ListPlaylist = (ListView)findViewById(R.id.SavedPlaylist);

        clean = (Button)findViewById(R.id.clean);

        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost4);
        tabHost2.setup();

        TabHost.TabSpec data1 = tabHost2.newTabSpec("Songs");
        TabHost.TabSpec data2 = tabHost2.newTabSpec("Playlists");

        data1.setIndicator("Songs");
        data1.setContent(R.id.tab1);

        data2.setIndicator("Playlists");
        data2.setContent(R.id.tab2);

        tabHost2.addTab(data1);
        tabHost2.addTab(data2);

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mySongs = findSongs(Environment.getExternalStorageDirectory());
        if (mySongs.size() > 0) {
            items = new String[mySongs.size()];
            for (int i = 0; i < mySongs.size(); i++) {
                items[i] = SongName(mySongs.get(i));
            }
            ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items);
            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, getPlaylist());


            ListPlaylist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            ListPlaylist.setAdapter(adp2);
            ListMusic.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            ListMusic.setAdapter(adp);

            done = (Button)findViewById(R.id.done);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int choice = ListMusic.getCount();
                    selected = new long[choice];
                    songs = new String[choice];
                    final SparseBooleanArray spa = ListMusic.getCheckedItemPositions();
                    if (spa.size() < 1) {
                        mySongs = null;
                        for (int i = 0; i < choice; i++) {
                            selected[i] = -1;
                        }
                        for (int i = 0; i < choice; i++) {
                            if (spa.get(i)) {
                                selected[i] = ListMusic.getItemIdAtPosition(i);
                            }
                        }
                    }
                    else {
                        playlist = new String[ListMusic.getCheckedItemCount()];
                        int x = 0;
                        for (int i = 0; i < choice; i++) {
                            selected[i] = -1;
                        }
                        for (int i = 0; i < choice; i++) {
                            if (spa.get(i)) {
                                selected[i] = ListMusic.getItemIdAtPosition(i);
                            }
                        }
                        for(int j = 0; j < mySongs.size(); j++){
                            if (j == selected[j]){
                                playlist[x] = SongName(mySongs.get(j));
                                x++;
                            }
                        }
                        new MaterialDialog.Builder(PlaylistPickerActivity.this)
                                .title("Create a Playlist")
                                .content("Would you like to create a playlist with the selected songs?")
//                                .positiveText("Done")
                                .negativeText("Cancel")
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        for (int i = 0; i < choice; i++) {
//                                            if (spa.get(i)) {
//                                                songs[i] = ListMusic.getItemAtPosition(i).toString();
//                                                Log.v("Song", songs[i]);
//                                            }
//                                        }
//                                        dialog.dismiss();
//                                        finish();
//                                    }
//                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        toast("Cancel");
                                    }
                                })
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .input("Playlist name",null, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                       Playlist_name = input.toString(); // Do something
                                        if (Objects.equals(Playlist_name, "")){
                                            Playlist_name = "Playlist 1";
                                        }
                                        MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
                                        database.insertPlaylist(Playlist_name,convertArrayToString(playlist),1);
                                        toast(Playlist_name);
                                    }
                                }).show();
                    }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("positions",selected);
                    returnIntent.putExtra("songs",mySongs);
                    setResult(RESULT_OK, returnIntent);


                }
            });
        }
        else {
            done = (Button)findViewById(R.id.done);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
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

    public String[] getPlaylist(){
        String[] result;
        MySQLiteHelper database = new MySQLiteHelper(PlaylistPickerActivity.this);
        result = database.getPlaylist(1);

        return result;
    }
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
