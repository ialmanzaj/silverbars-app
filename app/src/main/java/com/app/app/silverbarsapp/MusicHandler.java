package com.app.app.silverbarsapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.app.app.silverbarsapp.callbacks.MusicCallback;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 03/14/17.
 */

public class MusicHandler implements MusicCallback {

    private static final String TAG = MusicHandler.class.getSimpleName();


    //exercise audio constants
    private final float VOLUME_FULL = (float)(Math.log(100)/Math.log(100));;
    private final float VOLUME_HALF = (float)(Math.log(100-90)/Math.log(100));


    private Context context;

    private SpotifyPlayerImpl mSpotifyPlayer;
    private MusicPlayer mLocalPlayer;

    //exercise audio
    private MediaPlayer mExerciseAudioPlayer;


    private MusicEvents listener;

    public MusicHandler(Context context,MusicEvents listener){
        this.context = context;
        this.listener = listener;
    }


    @Override
    public void onSongName(String song_name) {
        listener.updateSongName(song_name);
    }

    @Override
    public void onArtistName(String artist_name) {
        listener.updateArtistName(artist_name);
    }




    /**
     *    music methods
     *<p>
     *
     *
     */
    public void createSpotifyPlayer(String spotify_token, String spotify_playlist){
        if (mSpotifyPlayer == null){
            mSpotifyPlayer = new SpotifyPlayerImpl(context,spotify_token,spotify_playlist,this);
            mSpotifyPlayer.setup();
        }
    }

    public void createLocalMusicPlayer(String[] song_names,ArrayList<File> songs_files){
        if (mLocalPlayer == null){
            mLocalPlayer = new MusicPlayer(context,song_names,songs_files,this);
            mLocalPlayer.setup(0,true);
        }
    }


    public void playMusic(){
        if (isLocalPlayerAvailable()){
            mLocalPlayer.play();
            //event ui on music playing
            listener.onMusicPlayed();
        }else if (isSpotifyPlayerAvailable()){
            mSpotifyPlayer.play();
            //event ui on music playing
            listener.onMusicPlayed();
        }
    }

    public void pauseMusic(){
        if (isLocalPlayerAvailable()){
            mLocalPlayer.pause();
            //event ui on music paused
            listener.onMusicPaused();
        }else if (isSpotifyPlayerAvailable()){
            mSpotifyPlayer.pause();
            //event ui on music paused
            listener.onMusicPaused();
        }
    }


    public void playExerciseAudio(String exercise_audio_file){
        //Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

        setupExercisePlayer(exercise_audio_file);

        mExerciseAudioPlayer.setOnPreparedListener(player -> {
            Log.e("mExerciseAudioPlayer","ready!");

            if (isLocalPlayerAvailable() && mLocalPlayer.isPlaying())
                mLocalPlayer.setVolume(0.04f,0.04f);

            if (isSpotifyPlayerAvailable()){
                mSpotifyPlayer.pause();
            }

            mExerciseAudioPlayer.start();
        });

        mExerciseAudioPlayer.setOnCompletionListener(mediaPlayer -> {

            if (isLocalPlayerAvailable() && mLocalPlayer.isPlaying()) {
                mLocalPlayer.setVolume(VOLUME_FULL, VOLUME_FULL);
                mediaPlayer.release();
            }

            if (isSpotifyPlayerAvailable()){
                mSpotifyPlayer.play();
            }

        });
    }

    private void setupExercisePlayer(String exercise_audio_file){
        mExerciseAudioPlayer = new MediaPlayer();

        if (mExerciseAudioPlayer.isPlaying()) {
            mExerciseAudioPlayer.stop();
            mExerciseAudioPlayer.release();

            mExerciseAudioPlayer = new MediaPlayer();
        }

        try {
            String[] mp3dir = exercise_audio_file.split("/SilverbarsMp3/");
            mExerciseAudioPlayer = MediaPlayer.create(context, Uri.parse(context.getFilesDir()+"/SilverbarsMp3/"+mp3dir[1]));

        } catch (NullPointerException e) {
            Log.e(TAG,"NullPointerException",e);
        }
    }


    private boolean isExerciseAudioPlayerAvailable(){
        return mExerciseAudioPlayer != null;
    }

    private boolean isLocalPlayerAvailable(){
        return  mLocalPlayer != null;
    }

    private boolean isSpotifyPlayerAvailable(){
        return  mSpotifyPlayer != null;
    }


    private void destroySpotifyPlayer(){
        if (isSpotifyPlayerAvailable()){mSpotifyPlayer.onDestroy();}
    }

    private void destroyExerciseAudioPlayer(){
        if (isExerciseAudioPlayerAvailable()){mExerciseAudioPlayer.release();}
    }

    public void destroy(){
        destroySpotifyPlayer();
        destroyExerciseAudioPlayer();
    }



    public interface MusicEvents {

        void updateSongName(String song_name);
        void updateArtistName(String artist_name);

        void onMusicPlayed();
        void onMusicPaused();
    }

}
