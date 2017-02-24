package com.app.proj.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 02/12/17.
 */
@DatabaseTable
public class UserWorkout extends Workout{


    @DatabaseField(generatedId = true,canBeNull = false, columnName = "id")
    private int id;


    public UserWorkout(){}

    public UserWorkout(String workout_name, String workout_image, int sets, String level, String main_muscle) {
        super(workout_name, workout_image, sets, level, main_muscle);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
