package com.app.app.silverbarsapp.database_models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by isaacalmanza on 10/08/16.
 */
@DatabaseTable
public class ExerciseRep  implements Serializable {

    @DatabaseField(generatedId = true,canBeNull = false, columnName = "id")
    int id;

    @DatabaseField(foreign = true)
    Exercise exercise;

    @DatabaseField
    int repetition;

    @DatabaseField
    int seconds;

    @DatabaseField(foreign = true)
    UserWorkout userWorkout;

    @DatabaseField(foreign = true)
     MySavedWorkout saved_workout;

    @DatabaseField
    double weight;


    public ExerciseRep() {}

    public ExerciseRep(Exercise exercise,int repetition,int seconds,UserWorkout userWorkout,double weight) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.userWorkout = userWorkout;
        this.weight = weight;
    }

    public ExerciseRep(Exercise exercise,int repetition,int seconds,MySavedWorkout saved_workout,double weight) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.saved_workout = saved_workout;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int getRepetition() {
        return repetition;
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

    public void setUserWorkout(UserWorkout userWorkout) {
        this.userWorkout = userWorkout;
    }

    public UserWorkout getUserWorkout() {
        return userWorkout;
    }

    public void setSaved_workout(MySavedWorkout saved_workout) {
        this.saved_workout = saved_workout;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }


    public MySavedWorkout getSaved_workout() {
        return saved_workout;
    }
}




