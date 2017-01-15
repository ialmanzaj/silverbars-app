package com.app.proj.silverbars.viewsets;

/**
 * Created by isaacalmanza on 01/14/17.
 */

public interface WorkingOutView {

    void updateSongName(String song_name);
    void updateArtistName(String artist_name);

    void onPauseMusic();
    void onPlayMusic();
}
