package com.example.project.calisthenic;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by andre_000 on 6/17/2016.
 */
public interface WorkoutService {
    @GET("/workouts/?format=json")
    Call<JsonWorkout[]> getWorkouts();

    @GET
    public Call<JsonExercise> getExercises(@Url String url);

    @GET
    Call<ResponseBody> downloadImage(@Url String fileUrl);
}
