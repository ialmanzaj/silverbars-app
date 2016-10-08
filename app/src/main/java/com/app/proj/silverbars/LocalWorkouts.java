package com.app.proj.silverbars;

import java.io.Serializable;

public class LocalWorkouts implements Serializable {

    private int id;
    private String workout_id;
    private String exercise_id;
    private int exercise_repetition;
    private int positive;
    private int isometric;
    private int negative;

    public String getWorkout_id() {
        return workout_id;
    }

    public String getExerciseId() {return exercise_id;}

    public int getRepetition() {
        return exercise_repetition;
    }

    public LocalWorkouts(int id, String workout_id, String exercise_id, int exercise_repetition) {
        this.id = id;
        this.workout_id = workout_id;
        this.exercise_id = exercise_id;
        this.exercise_repetition = exercise_repetition;
    }
}
