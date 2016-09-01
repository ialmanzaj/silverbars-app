package com.app.proj.silverbars;

import java.io.Serializable;

/**
 * Created by andre_000 on 6/7/2016.
 */
public class JsonExercise implements Serializable {

    public int id;
    public String exercise_name;
    public String level;
    public String[] type_exercise;
    public String[] muscle;
    public String exercise_audio;
    public String exercise_image;
    public int rep;

//    @Override
//    public String toString(){
//        return (id+","+exercise_name+","+level+","+audio_url+","+type_exercise+","+muscle+","+exercise_audio+","+exercise_image);
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String[] getType_exercise() {
        return type_exercise;
    }

    public void setType_exercise(String[] type_exercise) {
        this.type_exercise = type_exercise;
    }

    public String[] getMuscle() {
        return muscle;
    }

    public void setMuscle(String[] muscle) {
        this.muscle = muscle;
    }

    public String getExercise_audio() {
        return exercise_audio;
    }

    public int getRep(){
        return rep;
    }



    public void setRep(int rep){
        this.rep = rep;
    }


    public void setExercise_audio(String exercise_audio) {
        this.exercise_audio = exercise_audio;
    }

    public String getExercise_image() {
        return exercise_image;
    }

    public void setExercise_image(String exercise_image) {
        this.exercise_image = exercise_image;
    }

    public JsonExercise() {
    }

    public JsonExercise(int id, String exercise_name, String level, String[] type_exercise, String[] muscle, String exercise_audio, String exercise_image) {
        this.id = id;
        this.exercise_name = exercise_name;
        this.level = level;
        this.type_exercise = type_exercise;
        this.muscle = muscle;
        this.exercise_audio = exercise_audio;
        this.exercise_image = exercise_image;
    }
}
