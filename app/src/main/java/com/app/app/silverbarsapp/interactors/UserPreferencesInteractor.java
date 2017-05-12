package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.UserPreferencesCallback;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 04/04/17.
 */

public class UserPreferencesInteractor {

    private static final String TAG = UserPreferencesInteractor.class.getSimpleName();

    private MainService mainService;
    private DatabaseQueries queries;

    public UserPreferencesInteractor(DatabaseHelper databaseHelper, MainService mainService){
        this.mainService = mainService;
        queries = new DatabaseQueries(databaseHelper);
    }

    public void getMyProfile(UserPreferencesCallback callback){
        mainService.getMyProfile().enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                if (response.isSuccessful()){
                    if (response.body().isEmpty()){
                        return;
                    }

                    try {

                        saveProfile(response.body().get(0),callback);

                    } catch (SQLException e) {e.printStackTrace();}

                }else {
                    Log.e(TAG,response.errorBody()+" "+response.code());
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                callback.onNetworkError();
            }
        });
    }

    public void getMyWorkouts(UserPreferencesCallback callback){
        mainService.getMyWorkouts().enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if(response.isSuccessful()){

                    try {

                        for (Workout workout: response.body()){queries.saveMyWorkout(workout);}
                        getExerciseProgression(callback);

                    } catch (SQLException e) {e.printStackTrace();}

                }else {
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {
                callback.onNetworkError();
            }
        });
    }

    private void getExerciseProgression(UserPreferencesCallback callback){
        mainService.getExercisesProgression().enqueue(new Callback<List<ExerciseProgression>>() {
            @Override
            public void onResponse(Call<List<ExerciseProgression>> call, Response<List<ExerciseProgression>> response) {
                if (response.isSuccessful()){

                    if (response.body().isEmpty()){
                        callback.onWorkoutsSaved();
                        return;
                    }

                    for (ExerciseProgression exerciseProgression: response.body()){
                        try {
                            saveExercisesProgressionDatabase(exerciseProgression);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    //tell the callback to return
                    callback.onWorkoutsSaved();

                }else {
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<ExerciseProgression>> call, Throwable t) {
                callback.onNetworkError();
            }
        });
    }

    private void saveExercisesProgressionDatabase(ExerciseProgression exerciseProgression) throws SQLException {
        queries.saveExerciseProgression(exerciseProgression);
    }

    private void saveProfile(Person person, UserPreferencesCallback callback) throws SQLException {
        queries.saveProfile(person);
        callback.onProfileSaved(person);
    }
}
