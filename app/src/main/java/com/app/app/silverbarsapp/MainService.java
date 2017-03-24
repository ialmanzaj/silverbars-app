package com.app.app.silverbarsapp;

import com.andretietz.retroauth.Authenticated;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.models.Workout;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;


public interface MainService {


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/workouts/")
    Call<List<Workout>> getWorkouts();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/muscles/")
    Call<List<Muscle>> getMuscles();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/exercises/")
    Call<List<Exercise>> getExercises();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/exercises/{id}/")
    Call<Exercise> getExercise(@Path("id") String id);

   /* @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/muscles/{id}/")
    Call<Muscle> getMuscle(@Path("id") int id);*/

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/myprogression/")
    Call<List<MuscleProgression>> getProgression();


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/myprogression/")
    Call<MuscleProgression> saveMyProgression(@Field("muscle") Muscle muscle,
                                              @Field("muscle_activation_progress") String muscle_activation_progress,
                                              @Field("person") int person,
                                              @Field("level") String level,
                                              @Field("date") String date);


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/muscles/{id}/")
    Observable<Muscle> getMuscle(@Path("id") int id);


    @GET("v1/me/")
    Call<Person> getMyProfile();

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

}

