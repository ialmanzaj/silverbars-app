package com.app.proj.silverbars;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class Workout implements Parcelable {

    private int workout_id;
    private String workout_name;
    private String workout_image;
    private int sets;
    private String level;
    private String main_muscle;
    private ExerciseRep[] exercises;

    public Workout(int workout_id, String workout_name, String workout_image, int sets, String level, String main_muscle,ExerciseRep[] exercises) {
        this.workout_id = workout_id;
        this.workout_name = workout_name;
        this.workout_image = workout_image;
        this.sets = sets;
        this.level = level;
        this.main_muscle = main_muscle;
        this.exercises = exercises;
    }

    protected Workout(Parcel in) {
        workout_id = in.readInt();
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
        return workout_id;
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
        parcel.writeInt(workout_id);
        parcel.writeString(workout_name);
        parcel.writeString(workout_image);
        parcel.writeInt(sets);
        parcel.writeString(level);
        parcel.writeString(main_muscle);
        parcel.writeTypedArray(exercises, i);
    }
}
