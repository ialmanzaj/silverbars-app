package com.app.proj.silverbars;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface SilverbarsService {
    @GET("v1/workouts/?format=json")
    Call<JsonWorkout[]> getWorkouts();

    @GET
    Call<JsonExercise> getExercises(@Url String url);

    @GET("v1/workout/?format=json")
    Call<JsonWorkoutReps[]> getReps();

   @GET("v1/exercises/?format=json")
    Call<JsonExercise[]> getAllExercises();

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET
    Call<SpotifyAdapter[]> SpotifyTracks();

    @GET("silverbarsmedias3/html/index.html")
    Call<ResponseBody> getMusclesTemplate();

    @GET("auth/convert-token/")
    Call<AccessToken> getAccessToken();

}
