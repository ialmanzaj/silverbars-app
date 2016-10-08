package com.app.proj.silverbars;

import java.io.Serializable;
import java.util.List;


public class Workout implements Serializable {

    private int workout_id;
    private String workout_name;
    private String workout_image;
    private int sets;
    private String level;
    private String main_muscle;
    private ExercisesRep[] exercises;


    public Workout(int workout_id, String workout_name, String workout_image, int sets, String level, String main_muscle,ExercisesRep[] exercises) {
        this.workout_id = workout_id;
        this.workout_name = workout_name;
        this.workout_image = workout_image;
        this.sets = sets;
        this.level = level;
        this.main_muscle = main_muscle;
        this.exercises = exercises;
    }

    public int getId() {
        return workout_id;
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

    public String getLevel() {return level;}

    public String getMain_muscle() {
        return main_muscle;
    }

    public ExercisesRep[] getExercises() {
        return exercises;
    }



}
