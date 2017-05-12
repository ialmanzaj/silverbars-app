package com.app.app.silverbarsapp.handlers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.app.app.silverbarsapp.callbacks.MusicCallback;
import com.app.app.silverbarsapp.utils.Utilities;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/15/17.
 */

public class MusicPlayerLocal {

    private static final String TAG = MusicPlayerLocal.class.getSimpleName();

    private MediaPlayer mMusicPlayerLocal;

    private Utilities utilities = new Utilities();

    private Context context;
    private MusicCallback callback;
    
    private ArrayList<File> mSongsSelected;

    private Utilities.HandlerMover mHandlerMover;

    public MusicPlayerLocal(Context context,ArrayList<File> songs_selected, MusicCallback callback){
        this.context = context;
        mSongsSelected = songs_selected;
        this.callback = callback;
        mHandlerMover = new Utilities.HandlerMover(songs_selected.size());
        setupPlayerReady(0);
    }

    public void pause(){
        mMusicPlayerLocal.pause();
    }

    public void play(){
        mMusicPlayerLocal.start();
    }

    public boolean isPlaying(){
        return mMusicPlayerLocal.isPlaying();
    }

    public void skipNext(){
        int position = mHandlerMover.moveNext();
        if (mHandlerMover.allowToMove(position)){
            onChangeToTrack(position);
        }
    }

    public void skipPreview(){
        int position = mHandlerMover.movePreview();
        if (mHandlerMover.allowToMove(position)){
            onChangeToTrack(position);
        }
    }

    private void setupPlayerReady(int position){
        //start player
        startPlayer(getSongUri(position));

        //update UI
        callback.onSongName(utilities.getSongName(context,mSongsSelected.get(position)));
        callback.onArtistName(utilities.SongArtist(context,mSongsSelected.get(position)));
    }

    private Uri getSongUri(int position){
        return Uri.parse(mSongsSelected.get(position).toString());
    }

    private void startPlayer(Uri uri){
        mMusicPlayerLocal = MediaPlayer.create(context.getApplicationContext(),uri);
        mMusicPlayerLocal.setOnCompletionListener(mediaPlayer -> onSongCompleted());
    }

    private void onSongCompleted(){
        Utilities.HandlerMover handlerMover = new Utilities.HandlerMover(mSongsSelected.size());
        int position = handlerMover.moveNext();
        if (handlerMover.allowToMove(position)){
            onChangeToTrack(position);
        }
    }

    private void onChangeToTrack(int position){
        restartPlayer();
        setupPlayerReady(position);
        mMusicPlayerLocal.start();
    }

    private void restartPlayer(){
        mMusicPlayerLocal.stop();
        mMusicPlayerLocal.release();
    }

    public void setVolume(float leftVolume,float rightVolume){mMusicPlayerLocal.setVolume(leftVolume,rightVolume);}

    public void release(){
        mMusicPlayerLocal.release();
    }
    
}
