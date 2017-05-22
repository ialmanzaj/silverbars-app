package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 04/04/17.
 */
public class ExerciseProgression implements Parcelable{

    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private String date;

    @SerializedName("my_workout_done")
    private WorkoutDone my_workout_done;

    @SerializedName("person")
    private int person;

    @SerializedName("total_time")
    private String total_time;

    @SerializedName("exercise")
    private Exercise exercise;

    @SerializedName("total_repetition")
    private int total_repetition;

    @SerializedName("repetitions_done")
    private int repetitions_done;

    @SerializedName("total_seconds")
    private int total_seconds;

    @SerializedName("seconds_done")
    private int seconds_done;

    @SerializedName("total_weight")
    private double total_weight;

    private int sets_completed;

    public ExerciseProgression(){}


    public ExerciseProgression(int id,String date,WorkoutDone my_workout_done,int person,String total_time,Exercise exercise,
                               int total_repetition,int repetitions_done,int total_seconds,int seconds_done,double total_weight){
        this.id = id;
        this.date = date;
        this.my_workout_done = my_workout_done;
        this.person =person;
        this.total_time= total_time;
        this.exercise = exercise;
        this.total_repetition = total_repetition;
        this.repetitions_done =repetitions_done;
        this.total_seconds = total_seconds;
        this.seconds_done = seconds_done;
        this.total_weight = total_weight;
    }


    protected ExerciseProgression(Parcel in) {
        id = in.readInt();
        date = in.readString();
        my_workout_done = in.readParcelable(WorkoutDone.class.getClassLoader());
        person = in.readInt();
        total_time = in.readString();
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        total_repetition = in.readInt();
        repetitions_done = in.readInt();
        total_seconds = in.readInt();
        seconds_done = in.readInt();
        total_weight = in.readDouble();
        sets_completed = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeParcelable(my_workout_done, flags);
        dest.writeInt(person);
        dest.writeString(total_time);
        dest.writeParcelable(exercise, flags);
        dest.writeInt(total_repetition);
        dest.writeInt(repetitions_done);
        dest.writeInt(total_seconds);
        dest.writeInt(seconds_done);
        dest.writeDouble(total_weight);
        dest.writeInt(sets_completed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseProgression> CREATOR = new Creator<ExerciseProgression>() {
        @Override
        public ExerciseProgression createFromParcel(Parcel in) {
            return new ExerciseProgression(in);
        }

        @Override
        public ExerciseProgression[] newArray(int size) {
            return new ExerciseProgression[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public void setMy_workout_done(WorkoutDone my_workout_done) {
        this.my_workout_done = my_workout_done;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public int getId() {
        return id;
    }

    public int getTotal_repetition() {
        return total_repetition;
    }

    public void setTotal_repetition(int total_repetition) {
        this.total_repetition = total_repetition;
    }

    public void setTotal_seconds(int total_seconds) {
        this.total_seconds = total_seconds;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public int getRepetitions_done() {
        return repetitions_done;
    }

    public String getTotal_time() {
        return total_time;
    }

    public WorkoutDone getMy_workout_done() {
        return my_workout_done;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int getSeconds_done() {
        return seconds_done;
    }

    public int getTotal_seconds() {
        return total_seconds;
    }

    public int getPerson() {
        return person;
    }

    public double getTotal_weight() {
        return total_weight;
    }

    public String getDate() {
        return date;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRepetitions_done(int repetitions_done) {
        this.repetitions_done = repetitions_done;
    }

    public void setSeconds_done(int seconds_done) {
        this.seconds_done = seconds_done;
    }

    public void setTotal_weight(double total_weight) {
        this.total_weight = total_weight;
    }

    public void setSets_completed(int sets_completed) {
        this.sets_completed = sets_completed;
    }

    public int getSets_completed() {
        return sets_completed;
    }
}
