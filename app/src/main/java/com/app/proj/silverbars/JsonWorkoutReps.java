package com.app.proj.silverbars;

import java.io.Serializable;

public class JsonWorkoutReps  implements Serializable {

    public int id;
    public String workout_id;
    public String exercise;
    public int repetition;
    public int positive;
    public int isometric;
    public int negative;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public JsonWorkoutReps() {
    }

    public JsonWorkoutReps(int id, String workout_id, String exercise, int repetition) {
        this.id = id;
        this.workout_id = workout_id;
        this.exercise = exercise;
        this.repetition = repetition;
    }
}
