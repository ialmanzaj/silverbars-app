package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 10/04/16.
 */
@DatabaseTable
public class Workout {

    @DatabaseField
    private String workout_name;

    @DatabaseField
    private String workout_image;

    @DatabaseField
    private int sets;

    @DatabaseField
    private String level;

    @DatabaseField
    private String main_muscle;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<ExerciseRep> exercises;


    public Workout(String workout_name,String workout_image,int sets,String level,String main_muscle){
        this.workout_name = workout_name;
        this.workout_image = workout_image;
        this.sets = sets;
        this.level = level;
        this.main_muscle = main_muscle;
    }


    protected Workout() {}


    public String getWorkout_name() {
        return workout_name;
    }

    public String getWorkout_image() {
        return workout_image;
    }

    public void setWorkout_name(String workout_name) {
        this.workout_name = workout_name;
    }

    public int getSets() {
        return sets;
    }

    public void setMain_muscle(String main_muscle) {
        this.main_muscle = main_muscle;
    }

    public String getMain_muscle() {
        return main_muscle;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {return level;}

    public void setWorkout_image(String workout_image) {
        this.workout_image = workout_image;
    }

    public ForeignCollection<ExerciseRep> getExercises() {
        return exercises;
    }

    public void setExercises(ForeignCollection<ExerciseRep> exercises) {
        this.exercises = exercises;
    }

}
