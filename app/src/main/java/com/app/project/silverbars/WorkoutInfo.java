package com.app.project.silverbars;

/**
 * Created by andre_000 on 4/12/2016.
 */
public class WorkoutInfo {
//    private int imagen;
    private String nombre;
    private String next;
    private String Reps;
    private String exercise_dir;

    public WorkoutInfo(String nombre, String Reps,String exercise_dir) {
//        this.imagen = imagen;
        this.nombre = nombre;
//        this.next = next;
        this.Reps = Reps;
        this.exercise_dir = exercise_dir;
    }

    public String getNombre() {
        return nombre;
    }

    public String getExercise_dir() { return exercise_dir;}

//    public String getVisitas() {
//        return next;
//    }

//    public int getImagen() {
//        return imagen;
//    }

    public String getReps() {
        return Reps;
    }
}
