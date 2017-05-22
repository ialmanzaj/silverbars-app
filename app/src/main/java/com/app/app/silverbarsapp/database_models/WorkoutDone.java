package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 04/06/17.
 */
@DatabaseTable
public class WorkoutDone {


    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    int id;

    @DatabaseField
    String date;

    @DatabaseField(foreign = true)
    UserWorkout my_workout;

    @DatabaseField
    int person;

    @DatabaseField
    String total_time;

    @DatabaseField
    int sets_completed;


    public WorkoutDone(){}



    public WorkoutDone(int id, String date, UserWorkout my_workout, int person, String total_time, int sets_completed){
        this.id = id;
        this.date = date;
        this.my_workout = my_workout;
        this.person = person;
        this.total_time = total_time;
        this.sets_completed = sets_completed;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMy_workout(UserWorkout my_workout) {
        this.my_workout = my_workout;
    }


    public void setPerson(int person) {
        this.person = person;
    }

    public void setSets_completed(int sets_completed) {
        this.sets_completed = sets_completed;
    }


    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getSets_completed() {
        return sets_completed;
    }

    public int getPerson() {
        return person;
    }

    public String getTotal_time() {
        return total_time;
    }

    public UserWorkout getMy_workout() {
        return my_workout;
    }
}
