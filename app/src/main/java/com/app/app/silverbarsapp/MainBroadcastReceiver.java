package com.app.app.silverbarsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.app.silverbarsapp.models.Metadata;


/**
 * Created by isaacalmanza on 05/01/17.
 */

public class MainBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MainBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");
        String action = intent.getAction();
        Log.d(TAG,"action"+action);

        switch (action) {
            case Constants.BroadcastTypes.METADATA_CHANGED_1:
                Log.d(TAG,"METADATA_CHANGED 1");

                getMetaData(intent);

                break;
            case Constants.BroadcastTypes.METADATA_CHANGED:
                Log.d(TAG,"METADATA_CHANGED");

                getMetaData(intent);

                break;
            case Constants.BroadcastTypes.PLAYBACK_STATE_CHANGED:
                Log.d(TAG,"PLAYBACK_STATE_CHANGED");
                boolean playing = intent.getBooleanExtra("playing", false);

                Metadata metadata = new Metadata();
                metadata.setPlaying(playing);
                MyRxBus.instanceOf().setString(metadata);

                MyRxBus.instanceOf().setContext(context);
                break;
            case Constants.BroadcastTypes.QUEUE_CHANGED:
                Log.d(TAG,"QUEUE_CHANGED");
                break;
        }

    }

    private void getMetaData(Intent intent){
        Log.d(TAG,"getMetaData ");

        Metadata metadata = new Metadata();

        String artistName = intent.getStringExtra("artist");
        String albumName = intent.getStringExtra("album");
        String trackName = intent.getStringExtra("track");
        int trackLengthInSec = intent.getIntExtra("length", 0);

        metadata.setArtistName(artistName);
        metadata.setAlbumName(albumName);
        metadata.setTrackName(trackName);

        MyRxBus.instanceOf().setString(metadata);
    }







}
