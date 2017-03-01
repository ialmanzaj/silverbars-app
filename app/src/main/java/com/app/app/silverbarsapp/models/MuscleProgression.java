package com.app.app.silverbarsapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 11/13/16.
 */

public class MuscleProgression {

    @SerializedName("muscle")
    private int muscle;


    private Muscle mainmuscle;

    @SerializedName("person")
    private int person;

    @SerializedName("muscle_activation_progress")
    private int muscle_activation_progress;

    @SerializedName("level")
    private int level;

    @SerializedName("date")
    private String date;


    public MuscleProgression(){}



    public int getMuscle_id(){
        return muscle;
    }

    public int getPerson_id() {
        return person;
    }

    public void setMuscle(Muscle mainmuscle) {
        this.mainmuscle = mainmuscle;
    }

    public Muscle getMuscle() {
        return mainmuscle;
    }

    public int getMuscle_activation_progress() {
        return muscle_activation_progress;
    }

    public int getLevel(){
        return level;
    }


    public void setLevel(int level) {
        this.level = level;
    }

    public void setMuscle_activation_progress(int muscle_activation_progress) {
        this.muscle_activation_progress = muscle_activation_progress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}