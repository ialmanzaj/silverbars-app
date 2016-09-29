package com.app.proj.silverbars;


public class WorkoutInfo {
//    private int imagen;
    private String nombre;
    private String next;
    private String Reps;
    private String exercise_dir;

    public WorkoutInfo(String nombre, String Reps,String exercise_dir) {
        this.nombre = nombre;
        this.Reps = Reps;
        this.exercise_dir = exercise_dir;
    }

    public String getNombre() {
        return nombre;
    }

    public String getExercise_dir() { return exercise_dir;}

    public String getReps() {
        return Reps;
    }
}
