package com.app.proj.silverbars.viewsets;

import kaaes.spotify.webapi.android.models.UserPrivate;

/**
 * Created by isaacalmanza on 01/21/17.
 */

public interface SpotifyView {

    void onMyProfile(UserPrivate userPrivate);

    void getMyPlaylist(String[] playlist);

    void displayMyTracks(String[] tracks);
}
