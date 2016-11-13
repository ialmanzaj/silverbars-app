package com.app.proj.silverbars;

import com.andretietz.retroauth.Authenticated;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;


public interface MainService {


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/workouts/")
    Call<List<Workout>> getWorkouts();

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
    Call<List<User.ProgressionMuscle>> getProgression();

    @Authenticated({R.string.authentication_ACCOUNT, R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/progression/me/")
    Call<User.ProgressionMuscle> saveMyProgression(@Field("muscle") String muscle,@Field("muscle_activation_progress") String muscle_activation_progress,
                                                   @Field("level") String level, @Field("date") String date);

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("silverbarsmedias3/html/index.html")
    Call<ResponseBody> getMusclesTemplate();

    @GET
    Call<SpotifyAdapter[]> SpotifyTracks();


}

