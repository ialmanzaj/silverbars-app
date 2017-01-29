package com.app.proj.silverbars.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class Exercise implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("exercise_name")
    private String exercise_name;

    @SerializedName("level")
    private String level;

    @SerializedName("type_exercise")
    private String[] type_exercise;

    @SerializedName("exercise_audio")
    private String exercise_audio;

    @SerializedName("exercise_image")
    private String exercise_image;

    @SerializedName("muscles")
    private Muscle[] muscles;


    public Exercise(){}



    protected Exercise(Parcel in) {
        id = in.readInt();
        exercise_name = in.readString();
        level = in.readString();
        type_exercise = in.createStringArray();
        muscles = in.createTypedArray(Muscle.CREATOR);
        exercise_audio = in.readString();
        exercise_image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(exercise_name);
        parcel.writeString(level);
        parcel.writeStringArray(type_exercise);
        parcel.writeTypedArray(muscles,flags);
        parcel.writeString(exercise_audio);
        parcel.writeString(exercise_image);
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };


    public int getExerciseId() {
        return id;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public String getLevel() {
        return level;
    }

    public String[] getTypes_exercise() {
        return type_exercise;
    }

    public Muscle[] getMuscles() {return muscles;}

    public String getExercise_audio() {
        return exercise_audio;
    }

    public String getExercise_image() {
        return exercise_image;
    }



    @Override
    public int describeContents() {
        return 0;
    }


}
