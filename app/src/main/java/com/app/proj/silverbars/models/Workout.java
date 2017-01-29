package com.app.proj.silverbars.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class Workout implements Parcelable {


    @SerializedName("id")
    private int id;

    @SerializedName("workout_name")
    private String workout_name;

    @SerializedName("workout_image")
    private String workout_image;

    @SerializedName("sets")
    private int sets;

    @SerializedName("level")
    private String level;

    @SerializedName("main_muscle")
    private String main_muscle;

    @SerializedName("exercises")
    private ExerciseRep[] exercises;



    public Workout(){}



    protected Workout(Parcel in) {
        id = in.readInt();
        workout_name = in.readString();
        workout_image = in.readString();
        sets = in.readInt();
        level = in.readString();
        main_muscle = in.readString();
        exercises = in.createTypedArray(ExerciseRep.CREATOR);
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public int getWorkoutId() {
        return id;
    }

    public String getWorkout_name() {
        return workout_name;
    }

    public String getWorkout_image() {
        return workout_image;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getLevel() {return level;}

    public String getMainMuscle() {
        return main_muscle;
    }

    public ExerciseRep[] getExercises() {
        return exercises;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(workout_name);
        parcel.writeString(workout_image);
        parcel.writeInt(sets);
        parcel.writeString(level);
        parcel.writeString(main_muscle);
        parcel.writeTypedArray(exercises, i);
    }
}
