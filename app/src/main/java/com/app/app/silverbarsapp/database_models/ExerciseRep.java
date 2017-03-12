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
    private int id;

    @DatabaseField(foreign = true)
    private Exercise exercise;

    @DatabaseField
    private int repetition;

    @DatabaseField
    private int seconds;

    @DatabaseField(foreign = true)
    private UserWorkout userWorkout;

    @DatabaseField(foreign = true)
    private MySavedWorkout saved_workout;


    public ExerciseRep() {}


    public ExerciseRep(Exercise exercise,int repetition,UserWorkout userWorkout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = 0;
        this.userWorkout = userWorkout;
    }


    public ExerciseRep(Exercise exercise,int repetition,MySavedWorkout saved_workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = 0;
        this.saved_workout = saved_workout;
    }

    public ExerciseRep(Exercise exercise,int repetition,int seconds,UserWorkout userWorkout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.userWorkout = userWorkout;
    }


    public ExerciseRep(Exercise exercise,int repetition,int seconds,MySavedWorkout saved_workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.saved_workout = saved_workout;
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public void setUserWorkout(UserWorkout userWorkout) {
        this.userWorkout = userWorkout;
    }

    public UserWorkout getUserWorkout() {
        return userWorkout;
    }


    public void setSaved_workout(MySavedWorkout saved_workout) {
        this.saved_workout = saved_workout;
    }

    public MySavedWorkout getSaved_workout() {
        return saved_workout;
    }
}


