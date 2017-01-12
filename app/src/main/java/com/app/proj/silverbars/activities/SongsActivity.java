package com.app.proj.silverbars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;




public class SongsActivity extends AppCompatActivity {


    private static final String TAG = SongsActivity.class.getSimpleName();


    private ListView ListMusic;


    private long[] selected;
    private Button clean;
    private String[] playlist;

    ArrayList<File> canciones_url;

    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        utilities = new Utilities();


        ListMusic = (ListView)findViewById(R.id.lvPlaylist);

        ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());

        Log.v(TAG,"mySongs list: "+ mySongs);

        if (mySongs.size() > 0) {

            canciones_url = DeleteNoteVoice(mySongs);

            if (canciones_url.size() > 0){

                String[] items = new String[canciones_url.size()];

                for (int i = 0; i < canciones_url.size(); i++) {
                    items[i] = utilities.removeLastMp3(utilities.getSongName(this,canciones_url.get(i)));
                }

                //Log.v(TAG,"items: "+ Arrays.toString(items));

                ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items);
                ListMusic.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                ListMusic.setAdapter(adp);


                Button done = (Button) findViewById(R.id.done);
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
                            for(int j = 0; j < canciones_url.size(); j++){
                                if (j == selected[j]){
                                    playlist[x] = utilities.getSongName(SongsActivity.this,canciones_url.get(j));
                                    x++;
                                }
                            }

                            Log.v("playlist", Arrays.toString(playlist));
                            Log.v("songs", String.valueOf(canciones_url));

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("positions",playlist);
                            returnIntent.putExtra("songs",canciones_url);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                        else
                            canciones_url = null;
                    }
                });

            }


        }
        else {
            finish();


            utilities.toast(this,"No hay canciones");
        }
    }



    public ArrayList<File> findSongs(File root){

        ArrayList<File> songs = new ArrayList<>();
        if (root.listFiles() != null){
            File[] files = root.listFiles();
            for(File singleFile : files){
                if (singleFile.isDirectory() && !singleFile.isHidden()){
                    songs.addAll(findSongs(singleFile));
                }
                else{
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        if (utilities.getSongDuration(this,singleFile)!=null && Long.valueOf(utilities.getSongDuration(this,singleFile))>150000)
                            songs.add(singleFile);
                    }
                }
            }
        }
        return songs;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private ArrayList<File> DeleteNoteVoice(ArrayList<File> songs){

        ArrayList<String> canciones = new ArrayList<>();
        ArrayList<File>   songs_urls = new ArrayList<>();
        for (int a = 0;a<songs.size();a++){canciones.add(songs.get(a).getPath());}

        for (int b = 0;b<canciones.size();b++){
            canciones.get(b).split("/storage/emulated/0/");

            if (!canciones.get(b).contains("WhatsApp/Media/WhatsApp Audio/AUD-")){


                Log.d(TAG,"otras canciones: "+songs.get(b));


                try {


                    songs_urls.add(songs.get(b));


                }catch (NullPointerException e){
                    Log.e(TAG,"NullPointerException",e);
                }


            }

        }
        return songs_urls;
    }

}
