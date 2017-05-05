package com.app.app.silverbarsapp.utils;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by isaacalmanza on 05/01/17.
 */
public class SpotifyActions {

    private static final String TAG = SpotifyActions.class.getSimpleName();

    private static final String PACKAGE_SPOTIFY = "com.spotify.music";
    private static final String SPOTIFY_MAIN_ACTIVITY = "com.spotify.music.MainActivity";

    private Context context;

    public SpotifyActions(Context context){
        this.context = context;
    }

    public boolean isSpotifyOpen(){
       return new Utilities().isAppRunning(context,PACKAGE_SPOTIFY);
    }

    public void launch(){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_SPOTIFY);
        if (intent == null){
            try {
                // Bring user to the market or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + PACKAGE_SPOTIFY));
                context.startActivity(intent);

            }catch (ActivityNotFoundException e){}
        }else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void searchArtist(String artist) {
        Log.d(TAG,"searchArtist");
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName(PACKAGE_SPOTIFY, SPOTIFY_MAIN_ACTIVITY));
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
        i.setComponent(new ComponentName(PACKAGE_SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
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
        i.setComponent(new ComponentName(PACKAGE_SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
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
        i.setComponent(new ComponentName(PACKAGE_SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
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
        i.setComponent(new ComponentName(PACKAGE_SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
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
        i.setComponent(new ComponentName(PACKAGE_SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            context.sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            context.sendOrderedBroadcast(i, null);
        }
    }

}
