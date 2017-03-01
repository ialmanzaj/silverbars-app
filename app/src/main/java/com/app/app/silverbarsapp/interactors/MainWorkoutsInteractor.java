package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.MainWorkoutsCallback;
import com.app.app.silverbarsapp.models.Workout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class MainWorkoutsInteractor {

    private static final String TAG = MainWorkoutsInteractor.class.getSimpleName();

    private MainService mainService;

    public MainWorkoutsInteractor(MainService mainService){
        this.mainService = mainService;
    }

    public void getWorkouts(MainWorkoutsCallback callback){
        mainService.getWorkouts().enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if(response.isSuccessful()){

                    callback.onWorkoutsFound(response.body());

                }else {
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {
                Log.e(TAG,"displayNetworkError",t);
                callback.onNetworkError();
            }
        });
    }


}
