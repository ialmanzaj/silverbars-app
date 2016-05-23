package com.example.project.calisthenic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_music);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String[] options;

        ListView MusicOptions = (ListView) findViewById(R.id.music_options);

        options = new String[]{"SELECT FROM MUSIC LIBRARY", "SELECT FROM SPOTIFY", "SELECT FROM SOUNDCLOUD"};

        ArrayAdapter<String> OptionsMusic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,  options);

        MusicOptions.setAdapter(OptionsMusic);
    }


}
