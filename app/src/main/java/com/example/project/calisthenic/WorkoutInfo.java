package com.example.project.calisthenic;

/**
 * Created by andre_000 on 4/12/2016.
 */
public class WorkoutInfo {
    private int imagen;
    private String nombre;
    private String next;
    private String Reps;

    public WorkoutInfo(int imagen, String nombre, String next, String Reps) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.next = next;
        this.Reps = Reps;
    }

    public String getNombre() {
        return nombre;
    }

    public String getVisitas() {
        return next;
    }

    public int getImagen() {
        return imagen;
    }

    public String getReps() {
        return Reps;
    }
}
