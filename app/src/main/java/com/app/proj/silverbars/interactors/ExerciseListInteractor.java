package com.app.proj.silverbars.interactors;

import android.util.Log;

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.callbacks.ExerciseListCallback;
import com.app.proj.silverbars.models.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 01/30/17.
 */

public class ExerciseListInteractor {

    private static final String TAG = ExerciseListInteractor.class.getSimpleName();

    private MainService service;

    public ExerciseListInteractor(MainService service){
        this.service = service;
    }

    public void getExercises(ExerciseListCallback callback){
        service.getExercises().enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful()){

                    callback.onExercises(response.body());

                }else {
                    Log.e(TAG,"response "+response.code());
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.e(TAG,"onFailure ",t);

                callback.onNetworkError();
            }
        });
    }



}
