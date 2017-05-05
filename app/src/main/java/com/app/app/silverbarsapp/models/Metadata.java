package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 05/03/17.
 */

public class Metadata implements Parcelable{

    private String artistName;
    private String albumName;
    private String trackName;
    private int trackLengthInSec;
    private boolean playing;


    public Metadata(){}

    protected Metadata(Parcel in) {
        artistName = in.readString();
        albumName = in.readString();
        trackName = in.readString();
        trackLengthInSec = in.readInt();
        playing = in.readByte() != 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(albumName);
        dest.writeString(trackName);
        dest.writeInt(trackLengthInSec);
        dest.writeByte((byte) (playing ? 1 : 0));
    }

    public static final Creator<Metadata> CREATOR = new Creator<Metadata>() {
        @Override
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        @Override
        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setTrackLengthInSec(int trackLengthInSec) {
        this.trackLengthInSec = trackLengthInSec;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public int getTrackLengthInSec() {
        return trackLengthInSec;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public boolean isPlaying() {
        return playing;
    }

}
