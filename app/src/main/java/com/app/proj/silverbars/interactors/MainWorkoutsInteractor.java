package com.app.proj.silverbars.interactors;

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.callbacks.MainWorkoutsCallback;
import com.app.proj.silverbars.models.Workout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public class MainWorkoutsInteractor {

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




                }

            }
            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {

                callback.onServerError();


            }
        });
    }


}
