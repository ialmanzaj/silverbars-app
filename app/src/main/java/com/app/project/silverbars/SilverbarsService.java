package com.app.project.silverbars;

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
    Call<JsonReps[]> getReps();

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET
    Call<SpotifyAdapter[]> SpotifyTracks();
}
