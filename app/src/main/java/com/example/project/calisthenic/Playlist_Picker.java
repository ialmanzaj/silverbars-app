package com.example.project.calisthenic;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;

public class Playlist_Picker extends AppCompatActivity {
    ListView lv;
    String[] items;
    Button clean,done;
    long[] selected;
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


        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i = 0; i<mySongs.size(); i++){
//            toast(mySongs.get(i).getName());
            items[i]= mySongs.get(i).getName().toString().replace(".mp3","").replace(".mp3","");

        }

//        boton = (Button) findViewById(R.id.boton);
//
//        boton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                modal();
//            }
//        });



        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,android.R.id.text1,items);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adp);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int choice = lv.getCount();
                selected = new long[choice];
                for (int i = 0; i<choice ; i++){
                    selected[i]=-1;
                }
                SparseBooleanArray spa = lv.getCheckedItemPositions();
                for (int i = 0; i < choice; i++){
                    if (spa.get(i)){
                        selected[i] = lv.getItemIdAtPosition(i);
                    }
                }

                startActivity(new Intent(getApplicationContext(),Workout.class).putExtra("pos",selected).putExtra("songlist",mySongs) );
                finish();
//                Toast.makeText(getApplicationContext(),
//
//                        selected,
//
//                        Toast.LENGTH_LONG).show();
            }
        });
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                startActivity(new Intent(getApplicationContext(),WorkingOut.class).putExtra("pos",i).putExtra("songlist",mySongs) );
//            }
//        });
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
}
