package com.app.app.silverbarsapp.viewsets;

import kaaes.spotify.webapi.android.models.UserPrivate;

/**
 * Created by isaacalmanza on 01/21/17.
 */

public interface SpotifyView  extends BaseView{

    void onMyProfile(UserPrivate userPrivate);

    void getMyPlaylist(String[] playlist);

    void displayMyTracks(String[] tracks);
}
