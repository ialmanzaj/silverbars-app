package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 02/15/17.
 */
@DatabaseTable
public class MySavedWorkout extends Workout {

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private int id;

    @DatabaseField
    private boolean saved;

    public MySavedWorkout(){}


    public MySavedWorkout(int id, String workout_name, String workout_image, int sets, String level, String main_muscle,boolean saved) {
        super(workout_name, workout_image, sets, level, main_muscle);
        this.id = id;
        this.saved = saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean getSaved() {
        return saved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}



