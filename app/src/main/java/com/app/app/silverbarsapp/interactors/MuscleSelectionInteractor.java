package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.MuscleSelectionCallback;
import com.app.app.silverbarsapp.models.Muscle;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 03/20/17.
 */

public class MuscleSelectionInteractor {

    private MainService mainService;

    public MuscleSelectionInteractor(MainService mainService){
        this.mainService = mainService;
    }

    public void getMuscles(MuscleSelectionCallback callback){
        mainService.getMuscles().enqueue(new Callback<List<Muscle>>() {
            @Override
            public void onResponse(Call<List<Muscle>> call, Response<List<Muscle>> response) {
                if (response.isSuccessful()){
                    callback.onMuscles(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Muscle>> call, Throwable t) {

            }
        });
    }


}
