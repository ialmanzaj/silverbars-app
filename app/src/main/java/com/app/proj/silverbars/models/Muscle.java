package com.app.proj.silverbars.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/06/16.
 */

public class Muscle implements Parcelable{


    @SerializedName("muscle")
    private String muscle;

    @SerializedName("muscle_activation")
    private int muscle_activation;

    @SerializedName("classification")
    private String classification;

    private int progression_level;

    public Muscle(String muscle,int muscle_activation,String classification){
        this.muscle = muscle;
        this.muscle_activation = muscle_activation;
        this.classification = classification;
        this.progression_level = 0;
    }

    public int getMuscle_activation() {
        return muscle_activation;
    }

    public String getClassification() {
        return classification;
    }


    public void setMuscle_activation(int muscle_activation) {
        this.muscle_activation = muscle_activation;
    }


    public String getMuscle() {
        return muscle;
    }

    public void setProgression_level(int progression_level) {
        this.progression_level = progression_level;
    }

    public int getProgression_level() {
        return progression_level;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }


    public String getMuscleName() {
        return muscle;
    }


    protected Muscle(Parcel in) {
        muscle = in.readString();
        muscle_activation = in.readInt();
        classification = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(muscle);
        parcel.writeInt(muscle_activation);
        parcel.writeString(classification);
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

    @Override
    public int describeContents() {
        return 0;
    }


}
