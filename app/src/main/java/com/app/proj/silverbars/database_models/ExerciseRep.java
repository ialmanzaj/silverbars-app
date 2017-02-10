package com.app.proj.silverbars.database_models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by isaacalmanza on 10/08/16.
 */
@DatabaseTable
public class ExerciseRep  implements Serializable {

    @DatabaseField(generatedId = true,canBeNull = false, columnName = "id")
    protected int id;

    @DatabaseField(foreign = true)
    protected Exercise exercise;

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
    protected Workout workout;


    public ExerciseRep() {}


    public ExerciseRep(Exercise exercise,int repetition,Workout workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = 0;
        this.tempo_positive  = 1;
        this.tempo_negative = 1;
        this.tempo_isometric = 1;
        this.workout = workout;
    }



    public ExerciseRep(Exercise exercise,int repetition,int seconds,Workout workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.tempo_positive  = 1;
        this.tempo_negative = 1;
        this.tempo_isometric = 1;
        this.workout = workout;
    }

    public ExerciseRep(Exercise exercise,int repetition,int seconds,int tempo_positive,int tempo_isometric,int tempo_negative,Workout workout) {
        this.exercise = exercise;
        this.repetition = repetition;
        this.seconds = seconds;
        this.tempo_positive  = tempo_positive;
        this.tempo_isometric = tempo_isometric;
        this.tempo_negative = tempo_negative;
        this.workout = workout;
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

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }
}


