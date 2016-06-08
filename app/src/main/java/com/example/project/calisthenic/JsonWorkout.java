package com.example.project.calisthenic;

/**
 * Created by andre_000 on 6/7/2016.
 */
public class JsonWorkout {

    public int id;
    public String workout_name;
    public int sets;
    public String[] exercises;

    @Override
    public String toString(){
        return (id+","+workout_name+","+sets+","+exercises);
    }
}
