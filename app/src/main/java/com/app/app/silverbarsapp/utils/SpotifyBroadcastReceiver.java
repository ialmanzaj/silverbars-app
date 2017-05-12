package com.app.app.silverbarsapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.app.silverbarsapp.models.Metadata;
import com.michaelflisar.rxbus.RXBus;

import static com.app.app.silverbarsapp.Constants.BroadcastTypes.METADATA_CHANGED;
import static com.app.app.silverbarsapp.Constants.BroadcastTypes.METADATA_CHANGED_1;
import static com.app.app.silverbarsapp.Constants.BroadcastTypes.PLAYBACK_STATE_CHANGED;


/**
 * Created by isaacalmanza on 05/01/17.
 */

public class SpotifyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = SpotifyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case METADATA_CHANGED_1:
                Log.d(TAG,"METADATA_CHANGED_1");
                RXBus.get().sendEvent(getMetaData(intent),METADATA_CHANGED,true);
                break;
            case METADATA_CHANGED:
                Log.d(TAG,"METADATA_CHANGED");
                RXBus.get().sendEvent(getMetaData(intent),METADATA_CHANGED,true);
                break;
            case PLAYBACK_STATE_CHANGED:
                Log.d(TAG,"PLAYBACK_STATE_CHANGED");
                boolean playing = intent.getBooleanExtra("playing", false);
                RXBus.get().sendEvent(getMetadata(context,playing),PLAYBACK_STATE_CHANGED,true);
                break;
            default:
                break;
        }
    }

    private Metadata getMetadata(Context context,boolean playing){
        Metadata metadata = new Metadata();
        metadata.setContext(context);
        metadata.setPlaying(playing);
        return metadata;
    }

    private Metadata getMetaData(Intent intent){
        Metadata metadata = new Metadata();

        String artistName = intent.getStringExtra("artist");
        String albumName = intent.getStringExtra("album");
        String trackName = intent.getStringExtra("track");
        int trackLengthInSec = intent.getIntExtra("length", 0);

        metadata.setArtistName(artistName);
        metadata.setAlbumName(albumName);
        metadata.setTrackName(trackName);

        return metadata;
    }






}
