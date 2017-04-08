package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 04/06/17.
 */
@DatabaseTable
public class ExerciseProgression {

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private int id;

    @DatabaseField
    private String date;

    @DatabaseField(foreign = true)
    private WorkoutDone my_workout_done;

    @DatabaseField
    private int person;

    @DatabaseField
    private double total_time;

    @DatabaseField(foreign = true)
    private Exercise exercise;

    @DatabaseField
    private int total_repetition;

    @DatabaseField
    private int repetitions_done;

    @DatabaseField
    private int total_seconds;

    @DatabaseField
    private int seconds_done;

    @DatabaseField
    private double total_weight;


    public ExerciseProgression(){}


    public ExerciseProgression(int id, String date, WorkoutDone my_workout_done, int person, double total_time, Exercise exercise, int total_repetition,int repetitions_done,int total_seconds,int seconds_done, double total_weight){
        this.id = id;
        this.date = date;
        this.my_workout_done = my_workout_done;
        this.person = person;
        this.total_time = total_time;
        this.exercise = exercise;
        this.total_repetition = total_repetition;
        this.repetitions_done = repetitions_done;
        this.total_seconds = total_seconds;
        this.seconds_done = seconds_done;
        this.total_weight = total_weight;
    }


    public int getId() {
        return id;
    }

    public int getTotal_repetition() {
        return total_repetition;
    }

    public int getRepetitions_done() {
        return repetitions_done;
    }

    public double getTotal_time() {
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



}
