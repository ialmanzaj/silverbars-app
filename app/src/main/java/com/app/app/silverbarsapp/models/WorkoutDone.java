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
    private Workout my_workout;

    @SerializedName("workout")
    private Workout workout;

    @SerializedName("person")
    private int person;

    @SerializedName("total_time")
    private String total_time;

    @SerializedName("sets_completed")
    private int sets_completed;


    public WorkoutDone(){}


    public WorkoutDone(int id, Workout my_workout,Workout workout,int person,String total_time,int sets_completed){
        this.id = id;
        this.my_workout = my_workout;
        this.workout = workout;
        this.person = person;
        this.total_time = total_time;
        this.sets_completed = sets_completed;
    }


    protected WorkoutDone(Parcel in) {
        id = in.readInt();
        date = in.readString();
        my_workout = in.readParcelable(Workout.class.getClassLoader());
        workout = in.readParcelable(Workout.class.getClassLoader());
        person = in.readInt();
        total_time = in.readString();
        sets_completed = in.readInt();
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

    public Workout getMy_workout() {
        return my_workout;
    }

    public Workout getWorkout() {
        return workout;
    }

    public String getTotal_time() {
        return total_time;
    }

    public int getSets_completed() {
        return sets_completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeParcelable(my_workout, flags);
        dest.writeParcelable(workout, flags);
        dest.writeInt(person);
        dest.writeString(total_time);
        dest.writeInt(sets_completed);
    }
}
