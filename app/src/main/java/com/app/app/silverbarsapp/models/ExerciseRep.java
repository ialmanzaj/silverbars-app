package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/08/16.
 */
public class ExerciseRep implements Parcelable{

    @SerializedName("id")
    private int id;

    @SerializedName("exercise")
    private Exercise exercise;

    @SerializedName("repetition")
    private int repetition;

    @SerializedName("seconds")
    private int seconds;

    @SerializedName("workout")
    private int workout;


    private double weight;

    private String timeperset;
    private String exercise_state = "REP";
    private int number;

    public ExerciseRep() {}


    public ExerciseRep(int id,Exercise exercise,int repetition,int seconds) {
        this.id = id;
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
    }

    public ExerciseRep(int id,Exercise exercise,int repetition,int seconds,double weight) {
        this.id = id;
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.weight = weight;
    }


    protected ExerciseRep(Parcel in) {
        id = in.readInt();
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        repetition = in.readInt();
        seconds = in.readInt();
        workout = in.readInt();
        weight = in.readDouble();
        timeperset = in.readString();
        exercise_state = in.readString();
        number = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(exercise, flags);
        dest.writeInt(repetition);
        dest.writeInt(seconds);
        dest.writeInt(workout);
        dest.writeDouble(weight);
        dest.writeString(timeperset);
        dest.writeString(exercise_state);
        dest.writeInt(number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseRep> CREATOR = new Creator<ExerciseRep>() {
        @Override
        public ExerciseRep createFromParcel(Parcel in) {
            return new ExerciseRep(in);
        }

        @Override
        public ExerciseRep[] newArray(int size) {
            return new ExerciseRep[size];
        }
    };



    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }


    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public double getWeight() {
        return weight;
    }

    public void setWorkout(int workout) {
        this.workout = workout;
    }

    public int getWorkout() {
        return workout;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setExerciseState(String type){
        exercise_state = type;
    }

    public String getExercise_state() {
        return exercise_state;
    }

    public void setTimes_per_set(String times_per_set) {
        this.timeperset = times_per_set;
    }

    public String getTimes_per_set() {
        return timeperset;
    }



}


