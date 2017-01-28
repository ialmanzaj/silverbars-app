package com.app.proj.silverbars.interactors;

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.callbacks.CreateWorkoutCallback;
import com.app.proj.silverbars.models.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class CreateWorkoutInteractor {

    private static final String TAG = CreateWorkoutInteractor.class.getSimpleName();

    private MainService mainService;

    public CreateWorkoutInteractor(MainService mainService){
        this.mainService = mainService;
    }

    public void getExercises(CreateWorkoutCallback callback){
        mainService.getExercises().enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful())

                    callback.onExercises(response.body());

                else
                    callback.onServerError();
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {

                callback.onNetworkError();

            }
        });
    }



}
