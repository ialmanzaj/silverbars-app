package com.app.app.silverbarsapp.utils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 05/07/17.
 */

public class MusicService extends IntentService {
    public static final String ACTION_COMPLETED = "COMPLETED";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public MusicService() {
        super("MusicService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Utilities utilities = new Utilities();

        ArrayList<File> audio_local = utilities.findSongs(this, Environment.getExternalStorageDirectory());
        ArrayList<File> songs = utilities.getSongs(audio_local);

        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_COMPLETED);
        bcIntent.putExtra("songs",songs);
        sendBroadcast(bcIntent);
    }
}
