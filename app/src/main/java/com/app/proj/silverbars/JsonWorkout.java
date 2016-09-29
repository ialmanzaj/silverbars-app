package com.app.proj.silverbars;

import java.io.Serializable;


public class JsonWorkout  implements Serializable {

    private int id;
    private String workout_name;
    private String workout_image;
    private int sets;
    private String level;
    private String main_muscle;
    private String[] exercises;

    public JsonWorkout(int id, String workout_name, String workout_image, int sets, String level, String main_muscle, String[] exercises) {
        this.id = id;
        this.workout_name = workout_name;
        this.workout_image = workout_image;
        this.sets = sets;
        this.level = level;
        this.main_muscle = main_muscle;
        this.exercises = exercises;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkout_name() {
        return workout_name;
    }

    public String getWorkout_image() {
        return workout_image;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getLevel() {
        return level;
    }

    public String getMain_muscle() {
        return main_muscle;
    }

    public String[] getExercises() {
        return exercises;
    }

    public void setExercises(String[] exercises) {
        this.exercises = exercises;
    }


}
