package com.app.proj.silverbars;

import com.andretietz.retroauth.Authenticated;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;


public interface SilverbarsService {


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/workouts/")
    Call<Workout[]> getWorkouts();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/exercises/")
    Call<Exercise[]> getAllExercises();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/exercises/{id}/")
    Call<Exercise> getExercise(@Path("id") String id);

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/muscles/{id}/")
    Call<Muscle> getMuscle(@Path("id") int id);

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/progression/me/")
    Call<User.ProgressionMuscle[]> getProgression();

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("silverbarsmedias3/html/index.html")
    Call<ResponseBody> getMusclesTemplate();

    @GET
    Call<SpotifyAdapter[]> SpotifyTracks();


}

