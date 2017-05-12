package com.app.app.silverbarsapp.handlers;

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


    private MusicPlayerLocal mMusicPlayerLocal;
    private SpotifyPlayer mSpotifyPlayer;
    private MediaPlayer mExerciseAudioPlayer;


    private Context context;
    private MusicEvents listener;

    public MusicHandler(Context context,MusicEvents listener){
        this.context = context;
        this.listener = listener;
    }

    /**
     *
     *
     *
     *
     *  Setup all the handlers
     *<p>
     *
     *
     *
     *
     *
     *
     *
     */

    public void setupMusicPlayerLocal(ArrayList<File> songs_selected){
        if (mMusicPlayerLocal == null){
            mMusicPlayerLocal = new MusicPlayerLocal(context,songs_selected,this);
        }
    }

    public void setupSpotifyPlayer(){
        mSpotifyPlayer = new SpotifyPlayer(context);
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


    /**
     *
     *
     *
     *
     *
     *  Music actions control
     *<p>
     *
     *
     *
     *
     *
     *
     *
     *
     */

    public void playMusic(){
        if (isLocalPlayerAvailable()){
            Log.d(TAG,"Play local player");
            //control the player
            mMusicPlayerLocal.play();


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
            //control de player
            mMusicPlayerLocal.pause();

            listener.onMusicPaused();

        }else if (isSpotifyPlayerAvailable()){

            mSpotifyPlayer.pause();

            //event ui on music paused
            listener.onMusicPaused();
        }
    }
    
    public void skipNext(){
        if (isLocalPlayerAvailable()) {
            //control de player
            mMusicPlayerLocal.skipNext();
        }else if (isSpotifyPlayerAvailable()){
            mSpotifyPlayer.next();

        }
    }

    public void skipPreview(){
        if (isLocalPlayerAvailable()) {
            //control de player
            mMusicPlayerLocal.skipPreview();
        }else if (isSpotifyPlayerAvailable()){
            mSpotifyPlayer.preview();

        }
    }

    public void playExerciseAudio(String exercise_audio_file){
        //Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

        setupExercisePlayer(exercise_audio_file);

        mExerciseAudioPlayer.setOnPreparedListener(player -> {
            //Log.e("mExerciseAudioPlayer","ready!");

            if (isLocalPlayerAvailable() && mMusicPlayerLocal.isPlaying())
                mMusicPlayerLocal.setVolume(0.04f,0.04f);



            mExerciseAudioPlayer.start();
        });

        mExerciseAudioPlayer.setOnCompletionListener(mediaPlayer -> {

            if (isLocalPlayerAvailable() && mMusicPlayerLocal.isPlaying()) {
                mMusicPlayerLocal.setVolume(VOLUME_FULL, VOLUME_FULL);
                mediaPlayer.release();
            }

            if (isSpotifyPlayerAvailable()){
            }

        });
    }



    /**
     *
     *
     *
     *  Check all the handlers are alive
     *<p>
     *
     *
     *
     *
     */

    private boolean isExerciseAudioPlayerAvailable(){
        return mExerciseAudioPlayer != null;
    }

    private boolean isLocalPlayerAvailable(){
        return  mMusicPlayerLocal != null;
    }

    private boolean isSpotifyPlayerAvailable(){
        return  mSpotifyPlayer != null;
    }




    /**
     *
     *
     *
     *  Clean all
     *<p>
     *
     *
     *
     *
     */

    private void destroySpotifyPlayer(){
        if (isExerciseAudioPlayerAvailable()){mSpotifyPlayer.stop();}
    }

    private void destroyExerciseAudioPlayer(){
        if (isExerciseAudioPlayerAvailable()){mExerciseAudioPlayer.release();}
    }

    private void destroyMusicPlayerLocal(){
        if (isLocalPlayerAvailable()){mMusicPlayerLocal.release();}
    }

    public void destroy(){
        destroySpotifyPlayer();
        destroyMusicPlayerLocal();
        destroyExerciseAudioPlayer();
    }

    /**
     *
     *
     *
     *  Music events
     *<p>
     *
     *
     *
     *
     */

    @Override
    public void onSongName(String song_name) {
        listener.updateSongName(song_name);
    }

    @Override
    public void onArtistName(String artist_name) {
        listener.updateArtistName(artist_name);
    }




    public interface MusicEvents {

        void updateSongName(String song_name);
        void updateArtistName(String artist_name);

        void onMusicPlayed();
        void onMusicPaused();
    }

}
