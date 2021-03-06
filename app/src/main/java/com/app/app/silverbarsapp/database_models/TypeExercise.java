package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 02/08/17.
 */
@DatabaseTable
public class TypeExercise {

    @DatabaseField(generatedId = true,canBeNull = false, columnName = "id")
    int id;

    @DatabaseField
    String type;

    @DatabaseField(foreign = true)
    Exercise exercise;


    public TypeExercise(){}


    public TypeExercise(String type, Exercise exercise){
        this.type = type;
        this.exercise = exercise;
    }


    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
