package com.app.app.silverbarsapp.handlers;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;

import com.app.app.silverbarsapp.utils.Utilities;

import static com.app.app.silverbarsapp.Constants.SPOTIFY_MAIN_ACTIVITY;
import static com.app.app.silverbarsapp.Constants.SPOTIFY_PACKAGE;

/**
 * Created by isaacalmanza on 05/01/17.
 */
public class SpotifyHandler {

    private static final String TAG = SpotifyHandler.class.getSimpleName();
    private Context context;
    
    private boolean isSpotifyLaunched;

    public SpotifyHandler(Context context){
        this.context = context;
    }

    public boolean isSpotifyOpen(){
       return new Utilities().isAppRunning(context,SPOTIFY_PACKAGE);
    }

    public boolean existSpotify(){
        Intent intent = getSpotifyIntent();
        return intent != null;
    }

    private Intent getSpotifyIntent(){
        return context.getPackageManager().getLaunchIntentForPackage(SPOTIFY_PACKAGE);
    }

    public void launch(){
        if (existSpotify()) {

            //flag to know if we are launching the app
            isSpotifyLaunched = true;
            Intent intent = getSpotifyIntent();

            //launch spotify main activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


        } else {
            // Bring user to the market or let them choose an app?

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + SPOTIFY_PACKAGE));
                context.startActivity(intent);

            } catch (ActivityNotFoundException e) {}
        }
    }

    public void searchArtist(String artist) {
        Log.d(TAG,"searchArtist");
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName(SPOTIFY_PACKAGE, SPOTIFY_MAIN_ACTIVITY));
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        intent.putExtra(SearchManager.QUERY, artist);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public void playMusic() {
        Log.d(TAG,"playMusic");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(SPOTIFY_PACKAGE, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
            context.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
            context.sendOrderedBroadcast(i, null);
        }
    }

    public void stopMusic() {
        Log.d(TAG,"stopMusic");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(SPOTIFY_PACKAGE, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP));
            context.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP));
            context.sendOrderedBroadcast(i, null);
        }
    }

    public void pauseMusic() {
        Log.d(TAG,"pauseMusic");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(SPOTIFY_PACKAGE, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
            context.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
            context.sendOrderedBroadcast(i, null);
        }
    }

    public void skipNext() {
        Log.d(TAG,"skipNext");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(SPOTIFY_PACKAGE, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
            context.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
            context.sendOrderedBroadcast(i, null);
        }
    }


    public void skipPreview() {
        Log.d(TAG,"skipPreview");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(SPOTIFY_PACKAGE, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            context.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            context.sendOrderedBroadcast(i, null);
        }
    }

    public boolean isSpotifyLaunched() {
        return isSpotifyLaunched;
    }

    public void setSpotifyLaunched(boolean spotifyLaunched) {
        isSpotifyLaunched = spotifyLaunched;
    }

}
