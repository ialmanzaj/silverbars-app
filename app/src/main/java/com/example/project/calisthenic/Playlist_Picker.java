package com.example.project.calisthenic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.File;
import java.util.ArrayList;

public class Playlist_Picker extends AppCompatActivity {
    ListView lv;
    String[] items, songs;
    Button clean,done;
    long[] selected;
    private ArrayList<File> mySongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist__picker);
        lv = (ListView)findViewById(R.id.lvPlaylist);
        clean = (Button)findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        done = (Button)findViewById(R.id.done);

        if (findSongs(Environment.getExternalStorageDirectory()) != null){
            mySongs = findSongs(Environment.getExternalStorageDirectory());
            items = new String[mySongs.size()];
            for (int i = 0; i<mySongs.size(); i++){
                items[i]= mySongs.get(i).getName().toString().replace(".mp3","").replace(".mp3","");
            }
        }


        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,android.R.id.text1,items);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adp);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int choice = lv.getCount();
                selected = new long[choice];
                songs = new String[choice];

                for (int i = 0; i<choice ; i++){
                    selected[i]=-1;
                }
                SparseBooleanArray spa = lv.getCheckedItemPositions();
                for (int i = 0; i < choice; i++){
                    if (spa.get(i)){
                        selected[i] = lv.getItemIdAtPosition(i);
//                        songs[i] = lv.getItem
                    }
                }
//                for (int i = 0; i < choice; i++){
//                    toast(String.valueOf(selected[i]));
//                }
//               new LovelyStandardDialog(getBaseContext())
//                        .setTopColorRes(R.color.colorAccent)
//                        .setButtonsColorRes(R.color.colorPrimary)
//                        .setIcon(R.drawable.ic_mic_black_24dp)
//                        .setTitle("Save Playlist")
//                        .setMessage("Would you like to save this playlist for a future use?")
//                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                new LovelyTextInputDialog(getBaseContext(), R.style.EditTextTintTheme)
//                                        .setTopColorRes(R.color.colorAccent)
//                                        .setTitle("Save Playlist")
//                                        .setMessage("Name your playlist")
//                                        .setIcon(R.drawable.ic_mic_black_24dp)
//                                        .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
//                                            @Override
//                                            public void onTextInputConfirmed(String text) {
////                                                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .show();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null)
//                        .show();
                startActivity(new Intent(getApplicationContext(),Workout.class).putExtra("pos",selected).putExtra("songlist",mySongs) );
                finish();
            }
        });
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

    }

    public ArrayList<File> findSongs(File root){
        ArrayList<File> al = new ArrayList<File>();

        File[] files = root.listFiles();
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

        return al;
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
