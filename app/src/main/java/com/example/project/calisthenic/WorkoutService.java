package com.example.project.calisthenic;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by andre_000 on 6/17/2016.
 */
public interface WorkoutService {
    @GET("/workouts/?format=json")
    void getWorkouts(Callback<List<JsonWorkout>> callback);

    @GET("/exercises/?format=json")
    void getExercises(Callback<List<JsonExercise>> callback);
}
