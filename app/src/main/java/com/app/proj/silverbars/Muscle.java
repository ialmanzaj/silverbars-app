package com.app.proj.silverbars;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/06/16.
 */

public class Muscle implements Parcelable{


    @SerializedName("muscle")
    String muscle;

    @SerializedName("muscle_activation")
    int muscle_activation;

    @SerializedName("classification")
    String classification;


    public Muscle(String muscle,int muscle_activation,String classification){
        this.muscle = muscle;
        this.muscle_activation = muscle_activation;
        this.classification = classification;
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


    public int getMuscle_activation() {
        return muscle_activation;
    }

    public String getClassification() {
        return classification;
    }

    public String getMuscleName() {
        return muscle;
    }


    @Override
    public int describeContents() {
        return 0;
    }


}
