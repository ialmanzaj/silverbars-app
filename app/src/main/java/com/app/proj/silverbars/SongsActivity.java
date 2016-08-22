package com.app.proj.silverbars;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SongsActivity extends AppCompatActivity {
    private static final String TAG = "SongsActivity";
    private ListView ListMusic;
    private static String strSeparator = "__,__";
    private String[] items;
    private long[] selected;
    private ArrayList<File> mySongs;
    private Button clean,done;
    private String[] playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        Log.v(TAG,"SongsActivity: empezo");

        ListMusic = (ListView)findViewById(R.id.lvPlaylist);

        mySongs = findSongs(Environment.getExternalStorageDirectory());

        ArrayList<File> canciones;

        if (mySongs.size() > 0) {

            Log.v(TAG,"mySongs: "+ mySongs);
            canciones = DeleteNoteVoice(mySongs);

            items = new String[canciones.size()];

            for (int i = 0; i < canciones.size(); i++) {
                items[i] = SongName(canciones.get(i));
            }
            Log.v(TAG,"items: "+ Arrays.toString(items));



            ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items);
            ListMusic.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            ListMusic.setAdapter(adp);

            done = (Button)findViewById(R.id.done);
            done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int choice = ListMusic.getCount();
                        selected = new long[choice];
                        final SparseBooleanArray spa = ListMusic.getCheckedItemPositions();
                        if (spa.size() != 0) {
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
                            Log.v("Songs", Arrays.toString(playlist));
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("positions",playlist);
                            returnIntent.putExtra("songs",mySongs);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                        else
                            mySongs = null;
                    }
                });



        }
        else {
            finish();
            toast("No hay canciones");
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

    private ArrayList<File> DeleteNoteVoice(ArrayList<File> songs){
        ArrayList<String> canciones = new ArrayList<>();


        for (int a = 0;a<songs.size();a++){

            canciones.add(String.valueOf(songs.get(a)));

            canciones.get(a).split("/storage/emulated/0/");

            if (canciones.get(a).contains("/WhatsApp/Media/WhatsApp Audio/")){
                Log.v(TAG,"nota de voz"+songs.get(a));
                Log.v(TAG,"encontre nota de voz"+canciones.get(a));
                songs.remove(a);
            }

        }
        return songs;
    }

}
