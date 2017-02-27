package com.app.app.silverbarsapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 11/13/16.
 */

public class MuscleProgression {

    @SerializedName("muscle")
    private String muscle;

    @SerializedName("person")
    private String person;

    @SerializedName("muscle_activation_progress")
    private int muscle_activation_progress;

    @SerializedName("level")
    private int level;

    @SerializedName("date")
    private String date;


    public MuscleProgression(String muscle, String person, int muscle_activation_progress, int level, String date){
        this.muscle = muscle;
        this.person = person;
        this.muscle_activation_progress = muscle_activation_progress;
        this.level = level;
        this.date = date;
    }

    public String getMuscle(){
        return muscle;
    }

    public String getPerson(){
        return person;
    }

    public int getLevel(){
        return level;
    }

    public int getMuscle_activation() {
        return muscle_activation_progress;
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


}