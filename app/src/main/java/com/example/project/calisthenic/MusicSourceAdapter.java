package com.example.project.calisthenic;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by roberto on 5/25/2016.
 */
public class MusicSourceAdapter extends ArrayAdapter<String> {

    private static final String[] SOURCES = {"Local", "SoundCloud", "Spotify"};
    private static final int[] IMG = {R.drawable.local48, R.drawable.soundcloud48, R.drawable.spotify48};

    private final AppCompatActivity context;

    public MusicSourceAdapter(AppCompatActivity context) {
        super(context, R.layout.music_source_row_layout);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_source_row_layout, parent, false);
        ImageView img = (ImageView) row.findViewById(R.id.img);
        TextView tv = (TextView) row.findViewById(R.id.text);

        img.setImageResource(IMG[position]);
        tv.setText(SOURCES[position]);

        return row;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
