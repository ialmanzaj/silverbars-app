package com.app.proj.silverbars;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;



public class Exercise implements Serializable {

    private int id;
    private String exercise_name;
    private String level;
    private String[] type_exercise;
    private String exercise_audio;
    private String exercise_image;
    private Muscle[] muscles;

    public Exercise(){}

    public Exercise(int id, String exercise_name, String level, String[] type_exercise, Muscle[] muscles, String exercise_audio, String exercise_image) {
        this.id = id;
        this.exercise_name = exercise_name;
        this.level = level;
        this.type_exercise = type_exercise;
        this.muscles = muscles;
        this.exercise_audio = exercise_audio;
        this.exercise_image = exercise_image;

    }

    public int getExerciseId() {
        return id;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public String getLevel() {
        return level;
    }

    public String[] getTypes_exercise() {
        return type_exercise;
    }

    public Muscle[] getMuscles() {return muscles;}

    public String getExercise_audio() {
        return exercise_audio;
    }

    public String getExercise_image() {
        return exercise_image;
    }


}
