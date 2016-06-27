package com.app.project.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.project.silverbars.R;
import com.app.project.silverbars.Track;

/**
 * Created by roberto on 26/05/16.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.VH> {

    private final AppCompatActivity context;
    private final Track[] tracks;

    public static class VH extends RecyclerView.ViewHolder {

        TextView tvArtist;
        TextView tvTitle;

        public VH(View v) {
            super(v);

            tvArtist = (TextView) v.findViewById(R.id.tv_artist);
            tvTitle  = (TextView) v.findViewById(R.id.tv_title);
        }
    }

    public TrackAdapter(AppCompatActivity context, Track[] tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_row_layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH vh, int position) {
        Track t = tracks[position];
        vh.tvArtist.setText(t.user.username);
        vh.tvTitle.setText(t.title);
    }

    @Override
    public int getItemCount() {
        return tracks.length;
    }
}
