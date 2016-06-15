package com.example.project.calisthenic;

/**
 * Created by andre_000 on 6/7/2016.
 */
public class JsonWorkout {

    public int id;
    public String workout_name;
    public String workout_image;
    public int sets;
    public String level;
    public String main_muscle;
    public String[] exercises;

    @Override
    public String toString(){
        return (id+","+workout_name+","+workout_image+","+sets+","+level+","+main_muscle+","+exercises);
    }
}
