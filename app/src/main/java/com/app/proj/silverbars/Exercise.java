package com.app.proj.silverbars;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Exercise implements Parcelable {

    @SerializedName("id")
    int id;

    @SerializedName("exercise_name")
    String exercise_name;

    @SerializedName("level")
    String level;

    @SerializedName("type_exercise")
    String[] type_exercise;

    @SerializedName("exercise_audio")
    String exercise_audio;

    @SerializedName("exercise_image")
    String exercise_image;

    @SerializedName("muscles")
    Muscle[] muscles;


    public Exercise(){}

    public Exercise(int id, String exercise_name, String level, String[] type_exercise, Muscle[] muscles, String exercise_audio, String exercise_image) {
        this.id = id;
        this.exercise_name = exercise_name;
        this.level = level;
        this.type_exercise = type_exercise;
        this.muscles = muscles;
        this.exercise_audio = exercise_audio;
        this.exercise_image = exercise_image;
    }

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
