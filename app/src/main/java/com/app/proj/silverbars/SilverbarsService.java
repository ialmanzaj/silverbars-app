package com.app.proj.silverbars;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by andre_000 on 6/17/2016.
 */
public interface SilverbarsService {
    @GET("/workouts/?format=json")
    Call<JsonWorkout[]> getWorkouts();

    @GET
    Call<JsonExercise> getExercises(@Url String url);

    @GET("/workout/?format=json")
    Call<JsonWorkoutReps[]> getReps();

    @GET("/exercises/?format=json")
    Call<JsonExercise[]> getAllExercises();

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET
    Call<SpotifyAdapter[]> SpotifyTracks();

    @GET("silverbarsmedias3/html/index.html")
    Call<ResponseBody> getMusclesTemplate();


}
