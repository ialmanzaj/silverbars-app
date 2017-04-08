package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 11/13/16.
 */

public class MuscleProgression implements Parcelable{

    @SerializedName("muscle")
    private Muscle muscle;

    @SerializedName("person")
    private int person;

    @SerializedName("muscle_activation_progress")
    private int muscle_activation_progress;

    @SerializedName("level")
    private int level;

    @SerializedName("date")
    private String date;


    public MuscleProgression(){}


    protected MuscleProgression(Parcel in) {
        muscle = in.readParcelable(Muscle.class.getClassLoader());
        person = in.readInt();
        muscle_activation_progress = in.readInt();
        level = in.readInt();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(muscle, flags);
        dest.writeInt(person);
        dest.writeInt(muscle_activation_progress);
        dest.writeInt(level);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MuscleProgression> CREATOR = new Creator<MuscleProgression>() {
        @Override
        public MuscleProgression createFromParcel(Parcel in) {
            return new MuscleProgression(in);
        }

        @Override
        public MuscleProgression[] newArray(int size) {
            return new MuscleProgression[size];
        }
    };

    public Muscle getMuscle() {
        return muscle;
    }

    public int getPerson() {
        return person;
    }

    public int getMuscle_activation_progress() {
        return muscle_activation_progress;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMuscle_activation_progress(int muscle_activation_progress) {
        this.muscle_activation_progress = muscle_activation_progress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}