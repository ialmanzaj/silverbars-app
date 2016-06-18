package com.example.project.calisthenic;

import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by andre_000 on 6/18/2016.
 */
public interface ExerciseService {
    @GET("/exercises/?format=json")
    void getExercises(Callback<List<JsonExercise>> callback);
}
