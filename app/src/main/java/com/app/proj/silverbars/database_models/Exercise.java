package com.app.proj.silverbars.database_models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by isaacalmanza on 10/04/16.
 */
@DatabaseTable
public class Exercise implements Serializable{

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    protected int id;

    @DatabaseField
    private String exercise_name;

    @DatabaseField
    protected String level;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<TypeExercise> type_exercise;

    @DatabaseField
    private String exercise_audio;

    @DatabaseField
    private String exercise_image;


    @ForeignCollectionField(eager = true)
    private ForeignCollection<Muscle> muscles;




    public Exercise(){}




    public Exercise(int id,String exercise_name,String level,String exercise_audio,String exercise_image){
        this.id = id;
        this.exercise_name = exercise_name;
        this.level = level;
        this.exercise_audio = exercise_audio;
        this.exercise_image = exercise_image;
    }




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

    public String getExercise_audio() {
        return exercise_audio;
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

    public ForeignCollection<Muscle> getMuscles() {
        return muscles;
    }

    public ForeignCollection<TypeExercise> getType_exercise() {
        return type_exercise;
    }

    public void setMuscles(ForeignCollection<Muscle> muscles) {
        this.muscles = muscles;
    }

    public void setType_exercise(ForeignCollection<TypeExercise> type_exercise) {
        this.type_exercise = type_exercise;
    }
}
