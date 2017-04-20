package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 02/28/17.
 */

public class MuscleExercise implements Parcelable {

    @SerializedName("muscle")
    private String muscle;

    @SerializedName("muscle_activation")
    private int muscle_activation;

    @SerializedName("classification")
    private String classification;

    private int progression_level;


    public MuscleExercise(){}


    protected MuscleExercise(Parcel in) {
        muscle = in.readString();
        muscle_activation = in.readInt();
        classification = in.readString();
        progression_level = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(muscle);
        dest.writeInt(muscle_activation);
        dest.writeString(classification);
        dest.writeInt(progression_level);
    }

    public static final Creator<MuscleExercise> CREATOR = new Creator<MuscleExercise>() {
        @Override
        public MuscleExercise createFromParcel(Parcel in) {
            return new MuscleExercise(in);
        }

        @Override
        public MuscleExercise[] newArray(int size) {
            return new MuscleExercise[size];
        }
    };

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public int getMuscle_activation() {
        return muscle_activation;
    }

    public void setMuscle_activation(int muscle_activation) {
        this.muscle_activation = muscle_activation;
    }

    public int getProgression_level() {
        return progression_level;
    }

    public void setProgression_level(int progression_level) {
        this.progression_level = progression_level;
    }

    public String getClassification() {
        return classification;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
