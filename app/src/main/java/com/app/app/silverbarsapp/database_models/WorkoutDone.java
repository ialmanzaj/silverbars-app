package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by isaacalmanza on 04/06/17.
 */

public class WorkoutDone {

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private int id;

    @DatabaseField
    private String date;

    @DatabaseField(foreign = true)
    private UserWorkout workout;

    @DatabaseField
    private int person;

    @DatabaseField
    private String total_time;

    @DatabaseField
    private double sets_completed;


    public WorkoutDone(){}


    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getSets_completed() {
        return sets_completed;
    }

    public int getPerson() {
        return person;
    }

    public String getTotal_time() {
        return total_time;
    }

    public UserWorkout getWorkout() {
        return workout;
    }
}
