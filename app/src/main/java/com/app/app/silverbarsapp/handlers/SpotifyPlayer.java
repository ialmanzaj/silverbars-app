package com.app.app.silverbarsapp.handlers;

import android.content.Context;

/**
 * Created by isaacalmanza on 01/15/17.
 */

public class SpotifyPlayer {

    private SpotifyHandler mSpotifyHandler;

    public SpotifyPlayer(Context context){
        mSpotifyHandler = new SpotifyHandler(context);
    }

    public void init(){
        if (mSpotifyHandler.isSpotifyOpen()){
            mSpotifyHandler.playMusic();
        }
    }

    public void play(){
        if (mSpotifyHandler.isSpotifyOpen()){
            mSpotifyHandler.playMusic();
        }
    }


    public void stop(){
        if (mSpotifyHandler.isSpotifyOpen()){
            mSpotifyHandler.stopMusic();
        }
    }

    public void pause(){
        if (mSpotifyHandler.isSpotifyOpen()){
            mSpotifyHandler.pauseMusic();
        }
    }

    public void preview(){
        if (mSpotifyHandler.isSpotifyOpen()){
            mSpotifyHandler.skipPreview();
        }
    }

    public void next(){
        if (mSpotifyHandler.isSpotifyOpen()){
            mSpotifyHandler.skipNext();
        }
    }


}
