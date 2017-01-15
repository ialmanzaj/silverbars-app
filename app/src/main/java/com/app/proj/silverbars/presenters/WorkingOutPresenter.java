package com.app.proj.silverbars.presenters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.app.proj.silverbars.activities.WorkingOutActivity;
import com.app.proj.silverbars.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter  {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();


    private WorkingOutView view;


    //
    private final float VOLUME_FULL = (float)(Math.log(100)/Math.log(100));;
    private final float VOLUME_HALF = (float)(Math.log(100-90)/Math.log(100));


    private  MediaPlayer mLocalMusicPlayer;
    private MediaPlayer mExerciseAudioPlayer;



    public WorkingOutPresenter(WorkingOutView view){
        this.view = view;
    }


    private ArrayList<File> getPlaylist(String[] song_names,ArrayList<File> songs_files){

        ArrayList<File> playlist = new ArrayList<>();

        for(int position = 0; position < songs_files.size(); position++){
            for(int z = 0; z < song_names.length; z++){

                if (Objects.equals(song_names[z], utilities.getSongName(this,songs_files.get(position)))){
                    z++;

                    playlist.add(songs_files.get(position));
                }

            }
        }

        return playlist;
    }



    private void setupMusicPlayer(Context context,ArrayList<File> playlist){

        int x = 0;
        Uri url_player;

        int playlist_size = playlist.size();

        if (playlist_size > 1){

            x = (x+1) % playlist.size();

            url_player = Uri.parse(playlist.get(x).toString());


            String songName = utilities.getSongName(context,playlist.get(x));
            String artist = utilities.SongArtist(context,playlist.get(x));


            //update UI
            view.updateSongName(songName);
            view.updateArtistName(artist);
            

            //music player starting
            mLocalMusicPlayer = MediaPlayer.create(context.getApplicationContext(),url_player);
            mLocalMusicPlayer.start();


        }else{

            url_player = Uri.parse(playlist.get(x).toString());


            mLocalMusicPlayer = MediaPlayer.create(context.getApplicationContext(),url_player);
            mLocalMusicPlayer.start();
        }


        mLocalMusicPlayer.setOnCompletionListener(mediaPlayer -> setupMusicPlayer(context,playlist));

    }

    private void setupExercisePlayer(Context context,String exercise_audio_file){
        mExerciseAudioPlayer = new MediaPlayer();
        

        if (mExerciseAudioPlayer.isPlaying()) {
            mExerciseAudioPlayer.stop();
            mExerciseAudioPlayer.release();
            mExerciseAudioPlayer = new MediaPlayer();
        }


        try {


            String[] mp3dir = exercise_audio_file.split("/SilverbarsMp3/");
            mExerciseAudioPlayer = MediaPlayer.create(context, Uri.parse(context.getFilesDir()+"/SilverbarsMp3/"+mp3dir[1]));


        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }


    private void playExerciseAudio(Context context,String exercise_audio_file){
        Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

        setupExercisePlayer(context,exercise_audio_file);


        mExerciseAudioPlayer.setOnPreparedListener(player -> {
            Log.e("mExerciseAudioPlayer","ready!");

          
            if (mLocalMusicPlayer.isPlaying())
                mLocalMusicPlayer.setVolume(0.04f,0.04f);
            


           onPauseSpotify();

            mExerciseAudioPlayer.start();

        });

        mExerciseAudioPlayer.setOnCompletionListener(mediaPlayer -> {

            if (mLocalMusicPlayer !=null && mLocalMusicPlayer.isPlaying()) {
                mLocalMusicPlayer.setVolume(VOLUME_FULL, VOLUME_FULL);
                mediaPlayer.release();
            }

            if (mCurrentPlaybackState.isActiveDevice){
                mediaPlayer.release();
                mSpotifyPlayer.resume(mOperationCallback);
            }

        });
        
    }


    private void pauseLocalMusic(){
        mLocalMusicPlayer.pause();
    }


    private void cancelLocalMusic(){
        mLocalMusicPlayer.release();
    }

    private void startLocalMusic(){
        mLocalMusicPlayer.start();
    }

    private void onMusicPreview(){

        if (Music_Spotify){

            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                mSpotifyPlayer.skipToNext(mOperationCallback);

            }else {

                startPlaySpotify(spotify_playlist);
                onPlayMusicPlayerUI();
            }

        } else if (Songs_from_Phone) {


            int playlist_size = playlist.size();



            if (playlist_size > 1 && x + 1 < playlist_size) {
                mPlaybutton.setVisibility(View.GONE);
                mPausebutton.setVisibility(View.VISIBLE);
                mLocalMusicPlayer.stop();
                mLocalMusicPlayer.release();
                x = (x + 1) % playlist.size();
                u = Uri.parse(playlist.get(x).toString());

                String songName = utilities.getSongName(WorkingOutActivity.this,playlist.get(x));

                mSongName.setText(songName);

                String artist = utilities.SongArtist(WorkingOutActivity.this,playlist.get(x));

                mArtistName.setText(artist);

                mLocalMusicPlayer = MediaPlayer.create(getApplicationContext(), u);
                mLocalMusicPlayer.start();
            } else {



                mPlaybutton.setVisibility(View.GONE);
                mPausebutton.setVisibility(View.VISIBLE);



                mLocalMusicPlayer.stop();
                mLocalMusicPlayer.release();
                u = Uri.parse(playlist.get(x).toString());
                mLocalMusicPlayer = MediaPlayer.create(getApplicationContext(), u);
                mLocalMusicPlayer.start();


            }
        }



    }

    private void onMusicNext(){

        if (Music_Spotify){

            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                mSpotifyPlayer.skipToPrevious(mOperationCallback);

            }else {

                startPlaySpotify(spotify_playlist);
                onPlayMusicPlayerUI();
            }


        } else if (Songs_from_Phone){

            int playlist_size = playlist.size();
            if (playlist_size > 1 && (x-1) >= 0 ){
                mPlaybutton.setVisibility(View.GONE);
                mPausebutton.setVisibility(View.VISIBLE);
                mLocalMusicPlayer.stop();

                cancelLocalMusic();

                x = (x-1)%playlist.size();
                u = Uri.parse(playlist.get(x).toString());

                String songName = utilities.getSongName(WorkingOutActivity.this,playlist.get(x));
                mSongName.setText(utilities.removeLastMp3(songName));
                String artist = utilities.SongArtist(WorkingOutActivity.this,playlist.get(x));

                mArtistName.setText(artist);
                mLocalMusicPlayer = MediaPlayer.create(getApplicationContext(),u);

                startLocalMusic();
            } else {
                mPlaybutton.setVisibility(View.GONE);
                mPausebutton.setVisibility(View.VISIBLE);
                mLocalMusicPlayer.stop();
                cancelLocalMusic();
                u = Uri.parse(playlist.get(x).toString());
                mLocalMusicPlayer = MediaPlayer.create(getApplicationContext(),u);
                startLocalMusic();
            }
        }


    }


    public void onStart() {
        Log.d(TAG,"onStart");

        if (onPause){
            ResumeCountDown();
        }

    }



    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");




        if (onPause){
            ResumeCountDown();
        }

    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");

        PauseCountDown();
        onPause = true;


    }

    @Override
    public void onDestroy() {
        ScreenOff();


        if (MAIN_TIMER && main_timer != null){main_timer.cancel();}

        if (mExerciseAudioPlayer!=null){mExerciseAudioPlayer.release();}

    }
}
