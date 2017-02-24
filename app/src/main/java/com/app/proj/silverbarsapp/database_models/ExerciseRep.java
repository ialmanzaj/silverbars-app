package com.app.proj.silverbarsapp.database_models;


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

    @DatabaseField
    private int tempo_positive;

    @DatabaseField
    private int tempo_negative;

    @DatabaseField
    private int tempo_isometric;

    @DatabaseField(foreign = true)
    private UserWorkout userWorkout;

    @DatabaseField(foreign = true)
    private MySavedWorkout saved_workout;


    public ExerciseRep() {}


    public ExerciseRep(Exercise exercise,int repetition,UserWorkout userWorkout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = 0;
        this.tempo_positive  = 1;
        this.tempo_negative = 1;
        this.tempo_isometric = 1;
        this.userWorkout = userWorkout;
    }



    public ExerciseRep(Exercise exercise,int repetition,MySavedWorkout saved_workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = 0;
        this.tempo_positive  = 1;
        this.tempo_negative = 1;
        this.tempo_isometric = 1;
        this.saved_workout = saved_workout;
    }
    
    
    
    public ExerciseRep(Exercise exercise,int repetition,int seconds,UserWorkout userWorkout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.tempo_positive  = 1;
        this.tempo_negative = 1;
        this.tempo_isometric = 1;
        this.userWorkout = userWorkout;
    }
    
    
    
    public ExerciseRep(Exercise exercise,int repetition,int seconds,int tempo_positive,int tempo_isometric,int tempo_negative,UserWorkout userWorkout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.tempo_positive  = tempo_positive;
        this.tempo_isometric = tempo_isometric;
        this.tempo_negative = tempo_negative;
        this.userWorkout = userWorkout;
    }


    public ExerciseRep(Exercise exercise,int repetition,int seconds,int tempo_positive,int tempo_isometric,int tempo_negative,MySavedWorkout saved_workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.tempo_positive  = tempo_positive;
        this.tempo_isometric = tempo_isometric;
        this.tempo_negative = tempo_negative;
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


