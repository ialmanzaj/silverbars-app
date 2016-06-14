package com.example.project.calisthenic;

/**
 * Created by andre_000 on 6/13/2016.
 */
public class JsonReps {

    public int id;
    public String workout_id;
    public String exercise;
    public int repetition;

    @Override
    public String toString(){return id+","+workout_id+","+exercise+","+repetition;}
}
