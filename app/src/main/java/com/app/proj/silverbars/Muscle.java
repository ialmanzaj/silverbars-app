package com.app.proj.silverbars;

/**
 * Created by isaacalmanza on 10/06/16.
 */

public class Muscle {

    private int id;
    private String exercise;
    private String classification;
    private String muscle;
    private int muscle_activation;


    public Muscle(int id,String exercise,int muscle_activation,String classification,String muscle){
        this.id = id;
        this.exercise = exercise;
        this.muscle_activation = muscle_activation;
        this.classification = classification;
        this.muscle = muscle;
    }

    public int getId() {
        return id;
    }

    public int getMuscle_activation() {
        return muscle_activation;
    }

    public String getExercise() {
        return exercise;
    }

    public String getClassification() {
        return classification;
    }

    public String getMuscle() {
        return muscle;
    }
}
