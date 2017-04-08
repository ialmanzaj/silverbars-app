package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.UserPreferencesCallback;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

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
    private DatabaseQueries databaseQueries;

    public UserPreferencesInteractor(DatabaseHelper databaseHelper, MainService mainService){
        this.mainService = mainService;
        databaseQueries = new DatabaseQueries(databaseHelper);
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
                    Log.e(TAG,response.errorBody()+"");
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
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

                        Log.d(TAG,"insert workout");
                        for (Workout workout: response.body()){databaseQueries.insertUserWorkout(workout);}

                        callback.onWorkoutsSaved();


                    } catch (SQLException e) {e.printStackTrace();}
                }else {
                    Log.e(TAG,response.errorBody()+"");
                }
            }
            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
                callback.onServerError();
            }
        });
    }

    private void saveProfile(Person person, UserPreferencesCallback callback) throws SQLException {
        databaseQueries.saveProfile(person);
        callback.onProfileSaved();
    }


}
