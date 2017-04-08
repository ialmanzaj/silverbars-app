package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 04/06/17.
 */

public class WorkoutDone implements Parcelable{

    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private String date;

    @SerializedName("my_workout")
    private Workout workout;

    @SerializedName("person")
    private int person;

    @SerializedName("total_time")
    private String total_time;


    @SerializedName("sets_completed")
    private double sets_completed;


    public WorkoutDone(){}


    protected WorkoutDone(Parcel in) {
        id = in.readInt();
        date = in.readString();
        workout = in.readParcelable(Workout.class.getClassLoader());
        person = in.readInt();
        total_time = in.readString();
        sets_completed = in.readDouble();
    }

    public static final Creator<WorkoutDone> CREATOR = new Creator<WorkoutDone>() {
        @Override
        public WorkoutDone createFromParcel(Parcel in) {
            return new WorkoutDone(in);
        }

        @Override
        public WorkoutDone[] newArray(int size) {
            return new WorkoutDone[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getPerson() {
        return person;
    }

    public double getSets_completed() {
        return sets_completed;
    }

    public Workout getWorkout() {
        return workout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeParcelable(workout, flags);
        dest.writeInt(person);
        dest.writeString(total_time);
        dest.writeDouble(sets_completed);
    }
}
