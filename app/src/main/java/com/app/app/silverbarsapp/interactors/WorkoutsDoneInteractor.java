package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.WorkoutsDoneCallback;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 04/06/17.
 */

public class WorkoutsDoneInteractor {

    private static final String TAG = WorkoutsDoneInteractor.class.getSimpleName();

    private DatabaseHelper databaseHelper;
    private MainService mainService;

    public WorkoutsDoneInteractor(DatabaseHelper helper, MainService mainService){
        this.databaseHelper = helper;
        this.mainService = mainService;
    }

    public void getMyWorkoutsDone(WorkoutsDoneCallback callback){
        mainService.getWorkoutsDone().enqueue(new Callback<List<WorkoutDone>>() {
            @Override
            public void onResponse(Call<List<WorkoutDone>> call, Response<List<WorkoutDone>> response) {
                if (response.isSuccessful()){
                    callback.onWorkoutsDone(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<WorkoutDone>> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
            }
        });
    }



}
