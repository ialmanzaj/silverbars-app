package com.app.proj.silverbars;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/08/16.
 */

public class ExerciseRep implements Parcelable{

    @SerializedName("exercise")
    private Exercise exercise;

    @SerializedName("repetition")
    private int repetition;

    @SerializedName("seconds")
    private int seconds;


    private int tempo_positive;
    private int tempo_negative;
    private int tempo_isometric;


    public ExerciseRep(Exercise exercise) {
        this.exercise = exercise;
        this.repetition = 0;
        this.seconds = 0;
        this.tempo_positive = 0;
        this.tempo_negative = 0;
        this.tempo_isometric = 0;
    }

    public ExerciseRep(Exercise exercise, int repetition, int seconds) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.tempo_positive = 0;
        this.tempo_negative = 0;
        this.tempo_isometric = 0;
    }


    protected ExerciseRep(Parcel in) {
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        repetition = in.readInt();
        seconds = in.readInt();
        tempo_positive = in.readInt();
        tempo_negative = in.readInt();
        tempo_isometric = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(exercise, flags);
        dest.writeInt(repetition);
        dest.writeInt(seconds);
        dest.writeInt(tempo_positive);
        dest.writeInt(tempo_negative);
        dest.writeInt(tempo_isometric);
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

    public void setTempo_isometric(int tempo_isometric) {
        this.tempo_isometric = tempo_isometric;
    }

    public void setTempo_negative(int tempo_negative) {
        this.tempo_negative = tempo_negative;
    }

    public void setTempo_positive(int tempo_positive) {
        this.tempo_positive = tempo_positive;
    }

    public int getTempo_isometric() {
        return tempo_isometric;
    }

    public int getTempo_negative() {
        return tempo_negative;
    }

    public int getTempo_positive() {
        return tempo_positive;
    }

    @Override
    public int describeContents() {
        return 0;
    }



}


