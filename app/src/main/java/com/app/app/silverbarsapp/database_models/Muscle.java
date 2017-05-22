package com.app.app.silverbarsapp.database_models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by isaacalmanza on 10/06/16.
 */
@DatabaseTable
public class Muscle implements Serializable{

    @DatabaseField(generatedId = true,canBeNull = false, columnName = "id")
    int id;

    @DatabaseField
    String muscle;

    @DatabaseField
    int muscle_activation;

    @DatabaseField
    String classification;

    @DatabaseField
    int progression_level;


    @DatabaseField(foreign = true)
    Exercise exercise;


    public Muscle(){}



    public Muscle(String muscle,int muscle_activation,String classification,int progression_level,Exercise exercise){
        this.muscle = muscle;
        this.muscle_activation = muscle_activation;
        this.classification = classification;
        this.progression_level = progression_level;
        this.exercise = exercise;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMuscle_activation() {
        return muscle_activation;
    }

    public String getClassification() {
        return classification;
    }


    public void setMuscle_activation(int muscle_activation) {
        this.muscle_activation = muscle_activation;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public void setProgression_level(int progression_level) {
        this.progression_level = progression_level;
    }
    public int getProgression_level() {
        return progression_level;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }


}
