package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 04/17/17.
 */

public class Skill implements Parcelable{

    private int id;
    private String type;
    private int progress;

    public Skill(){}

    public Skill(String type,int progress){
        this.type = type;
        this.progress = progress;
    }

    protected Skill(Parcel in) {
        id = in.readInt();
        type = in.readString();
        progress = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeInt(progress);
    }

    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        @Override
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        @Override
        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
