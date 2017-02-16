package com.app.proj.silverbars.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 02/15/17.
 */
@DatabaseTable
public class MySavedWorkout extends Workout {

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private int id;


    public MySavedWorkout(){}


    public MySavedWorkout(int id, String workout_name, String workout_image, int sets, String level, String main_muscle) {
        super(workout_name, workout_image, sets, level, main_muscle);
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}



