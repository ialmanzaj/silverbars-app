package com.app.proj.silverbars;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;


public interface SilverbarsService {

    @GET("v1/workouts/")
    Call<Workout[]> getWorkouts();

    @GET("v1/workout/")
    Call<String[]> getExerciseReps();

    @GET("v1/exercises/")
    Call<Exercise[]> getAllExercises();

    @GET("v1/exercises/{id}/")
    Call<Exercise> getExercise(@Path("id") String id);

    @GET("v1/muscles/{id}/")
    Call<Muscle> getMuscle(@Path("id") int id);

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("silverbarsmedias3/html/index.html")
    Call<ResponseBody> getMusclesTemplate();

    @GET("v1/progression/me/")
    Call<User.ProgressionMuscle[]> getProgression();

    @GET
    Call<SpotifyAdapter[]> SpotifyTracks();



}

