package com.example.project.calisthenic;

/**
 * Created by andre_000 on 6/7/2016.
 */
public class JsonExercise {

    public int id;
    public String exercise_name;
    public String level;
    public String audio_url;
    public String[] type_exercise;
    public String[] muscle;

    @Override
    public String toString(){
        return (id+","+exercise_name+","+level+","+audio_url+","+type_exercise+","+muscle);
    }

}
