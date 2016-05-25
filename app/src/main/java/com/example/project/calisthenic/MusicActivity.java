package com.example.project.calisthenic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MusicActivity extends AppCompatActivity {

    private int Reps = 0, Tempo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (i.hasExtra("reps") && i.hasExtra("tempo")){
            Reps = b.getInt("reps");
            Tempo = b.getInt("tempo");
        }

        setContentView(R.layout.activity_music);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String[] options;

        ListView MusicOptions = (ListView) findViewById(R.id.music_options);

        options = new String[]{"MUSIC FROM MUSIC LIBRARY", "MUSIC FROM SPOTIFY", "MUSIC FROM SOUNDCLOUD"};

        ArrayAdapter<String> OptionsMusic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,  options);

        MusicOptions.setAdapter(OptionsMusic);

        MusicOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent intent = new Intent(MusicActivity.this, PlaylistPickerActivity.class);
                        intent.putExtra("reps",Reps);
                        intent.putExtra("tempo",Tempo);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        break;

                }
            }
        });
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }


}
