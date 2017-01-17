package com.app.proj.silverbars;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.app.proj.silverbars.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by isaacalmanza on 01/15/17.
 */

public class MusicPlayer {

    private static final String TAG = MusicPlayer.class.getSimpleName();

    private  MediaPlayer mLocalMusicPlayer;


    private Utilities utilities = new Utilities();
    private Context context;
    private MusicCallback callback;


    private String[] song_names;
    private ArrayList<File> songs_file;


    public MusicPlayer(Context context,String[] song_names,ArrayList<File> songs_files,MusicCallback callback){
        this.context = context;
        this.song_names = song_names;
        this.songs_file = songs_files;
        this.callback = callback;
    }

    public void setup(int position_song,boolean forward){
        setupMusicPlayer(getPlaylist(song_names, songs_file),position_song,forward);
    }

    private void setupMusicPlayer(ArrayList<File> playlist,int position_song,boolean forward){
        
        Uri song_url;

        if (playlist.size() > 0){

            if (forward){
                position_song = (position_song+1) % playlist.size();
            }else {
                position_song = (position_song-1) % playlist.size();
            }

            Log.i(TAG,"position_song: "+position_song);

            song_url = Uri.parse(playlist.get(position_song).toString());

            String songName = utilities.getSongName(context,playlist.get(position_song));
            String artist = utilities.SongArtist(context,playlist.get(position_song));

            //update UI
            callback.onSongName(songName);
            callback.onArtistName(artist);


            //music player starting
            mLocalMusicPlayer = MediaPlayer.create(context.getApplicationContext(),song_url);
        }


        mLocalMusicPlayer.setOnCompletionListener(mediaPlayer -> setupMusicPlayer(playlist,0,true));
    }

    public void setVolume(float leftVolume,float rightVolume){mLocalMusicPlayer.setVolume(leftVolume,rightVolume);}

    public void pausePlayer(){
        mLocalMusicPlayer.pause();
    }

    public void startPlayer(){
        mLocalMusicPlayer.start();
    }

    public void stopPlayer(){
        mLocalMusicPlayer.stop();
    }

    public void cancelPlayer(){mLocalMusicPlayer.release();}
    
    
    public boolean isPlaying(){
        return mLocalMusicPlayer.isPlaying();
    }


    private ArrayList<File> getPlaylist(String[] song_names,ArrayList<File> songs_files){

        ArrayList<File> playlist = new ArrayList<>();

        for(int position = 0; position < songs_files.size(); position++){
            for(int z = 0; z < song_names.length; z++){

                if (Objects.equals(song_names[z], utilities.getSongName(context,songs_files.get(position)))){
                    z++;

                    playlist.add(songs_files.get(position));
                }

            }
        }

        return playlist;
    }
}
