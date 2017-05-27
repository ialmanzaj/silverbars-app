package com.app.app.silverbarsapp;

import com.andretietz.retroauth.Authenticated;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.models.WorkoutDone;

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

    @Authenticated({R.string.authentication_ACCOUNT, R.string.authentication_TOKEN})
    @GET("v1/userworkouts/")
    Call<List<Workout>> getMyWorkouts();

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

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/muscleprogressions/")
    Call<List<MuscleProgression>> getProgression();


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/muscleprogressions/")
    Call<MuscleProgression> saveMuscleProgression(
            @Field("muscle") Muscle muscle,
            @Field("muscle_activation_progress") String muscle_activation_progress,
            @Field("person") int person,
            @Field("level") String level,
            @Field("date") String date
    );

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/userworkoutsdone/")
    Call<WorkoutDone> createWorkoutDoneMyWorkout(
            @Field("date") String date,
            @Field("my_workout_id") int my_workout_id,
            @Field("person") int person,
            @Field("sets_completed") int sets_completed,
            @Field("total_time") String total_time
    );


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/userworkoutsdone/")
    Call<WorkoutDone> createWorkoutDoneWorkout(
            @Field("date") String date,
            @Field("workout_id") int workout_id,
            @Field("person") int person,
            @Field("sets_completed") int sets_completed,
            @Field("total_time") String total_time
    );


    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/userworkoutsdone/{id}/")
    Call<WorkoutDone> getWorkoutDone(@Path("id") int id);

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/userworkoutsdone/")
    Call<List<WorkoutDone>> getWorkoutsDone();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/exerciseprogressions/")
    Observable<ExerciseProgression> saveExerciseProgression(
            @Field("my_workout_done_id") int my_workout_done_id,
            @Field("person") int person,
            @Field("exercise_id") int exercise_id,
            @Field("total_time") String total_time,
            @Field("total_repetition") int total_repetition,
            @Field("repetitions_done") int repetitions_done,
            @Field("total_seconds") int total_seconds,
            @Field("seconds_done") int seconds_done,
            @Field("total_weight") double total_weight,
            @Field("date") String date
    );

    @Authenticated({R.string.authentication_ACCOUNT, R.string.authentication_TOKEN})
    @GET("v1/exerciseprogressions/")
    Call<List<ExerciseProgression>> getExercisesProgression();

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/userworkouts/")
    Call<Workout> createMyWorkout(
            @Field("person") int person,
            @Field("workout_name") String workout_name,
            @Field("workout_image") String workout_image,
            @Field("sets") int sets,
            @Field("level") String level,
            @Field("main_muscle") String main_muscle
    );

    @Authenticated({R.string.authentication_ACCOUNT, R.string.authentication_TOKEN})
    @FormUrlEncoded
    @POST("v1/userworkoutsreps/")
    Observable<ExerciseRep> insertExercisesReps(
            @Field("exercise_id") int exercise_id,
            @Field("workout") int workout,
            @Field("repetition") int repetition,
            @Field("seconds") int seconds,
            @Field("weight") double weight
    );

    @Authenticated({R.string.authentication_ACCOUNT,
            R.string.authentication_TOKEN})
    @GET("v1/muscles/{id}/")
    Observable<Muscle> getMuscle(@Path("id") int id);

    @Authenticated({R.string.authentication_ACCOUNT, R.string.authentication_TOKEN})
    @GET("v1/me/")
    Call<List<Person>> getMyProfile();


    @Authenticated({R.string.authentication_ACCOUNT, R.string.authentication_TOKEN})
    @POST("v1/me/")
    Call<Person> saveProfile(
            @Field("age") int age,
            @Field("email") String email,
            @Field("gender") String gender
    );


    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

}

