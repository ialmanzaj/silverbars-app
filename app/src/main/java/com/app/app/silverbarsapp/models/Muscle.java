package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/06/16.
 */
public class Muscle implements Parcelable{

    @SerializedName("id")
    private int id;

    @SerializedName("muscle_name")
    private String muscle_name;

    public Muscle(){}

    public Muscle(String muscle_name){
        this.muscle_name = muscle_name;
    }


    protected Muscle(Parcel in) {
        id = in.readInt();
        muscle_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(muscle_name);
    }

    public static final Creator<Muscle> CREATOR = new Creator<Muscle>() {
        @Override
        public Muscle createFromParcel(Parcel in) {
            return new Muscle(in);
        }

        @Override
        public Muscle[] newArray(int size) {
            return new Muscle[size];
        }
    };


    public String getMuscle_name() {
        return muscle_name;
    }


    public int getId() {
        return id;
    }

    public int describeContents() {
        return 0;
    }



}
