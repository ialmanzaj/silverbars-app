package com.app.proj.silverbarsapp.callbacks;

import kaaes.spotify.webapi.android.models.UserPrivate;

/**
 * Created by isaacalmanza on 01/21/17.
 */

public interface CustomSpotifyCallback extends ServerCallback{

    void onMyProfile(UserPrivate userPrivate);

    void onMyPlaylist(String[] playlist);

    void onMyTracks(String[] tracks);
}
