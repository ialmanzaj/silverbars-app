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


    private long[] times_per_set;
    private STATE exercise_state = STATE.REP;
    private int number;
    private double weight;


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
        times_per_set = in.createLongArray();
        number = in.readInt();
        weight = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(exercise, flags);
        dest.writeInt(repetition);
        dest.writeInt(seconds);
        dest.writeInt(workout);
        dest.writeLongArray(times_per_set);
        dest.writeInt(number);
        dest.writeDouble(weight);
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

    public void setExerciseState(int position){
        exercise_state = STATE.values()[position];
    }

    public STATE getExercise_state() {
        return exercise_state;
    }

    public void createTimesPerSet(int total_sets){
        times_per_set = new long[total_sets];
    }

    public void addTimesPerSet(int current_set,long time){
        times_per_set[current_set] = time;
    }

    public long[] getTimes_per_set() {
        return times_per_set;
    }

    public enum STATE {
        REP, SECOND,
    }

}


