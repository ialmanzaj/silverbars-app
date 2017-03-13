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

    private STATE exercise_state = STATE.REP;

    private int number;

    public ExerciseRep() {}

    public ExerciseRep(int id,Exercise exercise,int repetition,int seconds) {
        this.id = id;
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
    }


    public ExerciseRep(int id,Exercise exercise,int repetition,int seconds,int tempo_positive,int tempo_isometric,int tempo_negative) {
        this.id = id;
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
    }


    protected ExerciseRep(Parcel in) {
        id = in.readInt();
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        repetition = in.readInt();
        seconds = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(exercise, flags);
        dest.writeInt(repetition);
        dest.writeInt(seconds);
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


    public void setExerciseState(int position){
        exercise_state = STATE.values()[position];
    }

    public STATE getExercise_state() {
        return exercise_state;
    }



    @Override
    public int describeContents() {
        return 0;
    }


    public enum STATE {
        REP, SECOND,
    }


}


