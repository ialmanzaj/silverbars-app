package com.app.proj.silverbars.interactors;

import android.util.Log;

import com.app.proj.silverbars.callbacks.CustomSpotifyCallback;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class SpotifyInteractor {

    private static final String TAG = SpotifyInteractor.class.getSimpleName();

    private SpotifyApi spotifyApi;
    private  SpotifyService service;


    public SpotifyInteractor(){
        this.spotifyApi = new SpotifyApi();
    }

    public  void initService(String token){
        spotifyApi.setAccessToken(token);
        service = spotifyApi.getService();
    }


    public void getMyProfile(CustomSpotifyCallback callback){
        service.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void failure(SpotifyError spotifyError) {

            }
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                callback.onMyProfile(userPrivate);
            }
        });
    }

    public void getMyPlaylists(CustomSpotifyCallback callback){
        service.getMyPlaylists(new SpotifyCallback<Pager<PlaylistSimple>>() {

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {

                String[] playlist = new String[playlistSimplePager.items.size()];

                for (int a = 0; a < playlistSimplePager.items.size(); a++) {
                    //Log.d(TAG, "playlistSimplePager:" + playlistSimplePager.items.get(a).uri);

                    playlist[a] = playlistSimplePager.items.get(a).uri;

                    //playlists.add(playlistSimplePager.items.get(a).uri);
                }

                callback.onMyPlaylist(playlist);

                //putElementsinList(items);
            }
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "SpotifyError: " + spotifyError);

                callback.onServerError();
            }
        });
    }


    public void getMyTracks(CustomSpotifyCallback callback){
        service.getMySavedTracks(new SpotifyCallback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> savedTrackPager, retrofit.client.Response response) {
                //Log.v("Response size", String.valueOf(response.getHeaders().size()));
                //Log.v("Pager size", String.valueOf(savedTrackPager.items.size()));

                String[] tracks = new String[savedTrackPager.items.size()];

                for (int a = 0; a < savedTrackPager.items.size(); a++) {
                    tracks[a] = savedTrackPager.items.get(a).track.uri;
                    //songs.add(savedTrackPager.items.get(a).track.uri);
                    Log.d(TAG, "SAVED: " + savedTrackPager.items.get(a).track.name);
                }


                //return tracks
                callback.onMyTracks(tracks);

            }
            @Override
            public void failure(SpotifyError error) {
                Log.e(TAG, "SpotifyError: " + error);

                callback.onServerError();
            }
        });
    }


}
