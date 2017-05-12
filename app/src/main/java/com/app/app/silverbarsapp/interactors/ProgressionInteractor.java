package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ProgressionCallback;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public class ProgressionInteractor {

    private static final String TAG = ProgressionInteractor.class.getSimpleName();

    private MainService mainService;
    private DatabaseQueries queries;

    public ProgressionInteractor(MainService mainService, DatabaseHelper helper){
        this.mainService = mainService;
        queries = new DatabaseQueries(helper);
    }

    public void getMuscleProgressions(ProgressionCallback callback){
        mainService.getProgression().enqueue(new Callback<List<MuscleProgression>>() {
            @Override
            public void onResponse(Call<List<MuscleProgression>> call, Response<List<MuscleProgression>> response) {
                if (response.isSuccessful()){

                  /*  if (response.body().isEmpty()){
                        callback.emptyProgress();
                        return;
                    }*/

                }else {
                    callback.onServerError();
                }
            }
            @Override
            public void onFailure(Call<List<MuscleProgression>> call, Throwable t) {
                callback.onNetworkError();
            }
        });
    }


    public void getExerciseProgression(ProgressionCallback callback){
        mainService.getExercisesProgression().enqueue(new Callback<List<ExerciseProgression>>() {
            @Override
            public void onResponse(Call<List<ExerciseProgression>> call, Response<List<ExerciseProgression>> response) {
                if (response.isSuccessful()){
                    if (response.body().isEmpty()){
                        callback.emptyProgress();
                        return;
                    }

                    callback.onProgression(response.body());


                    for (ExerciseProgression exerciseProgression: response.body()){
                        try {
                            saveExercisesProgressionDatabase(exerciseProgression);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }


                }else {
                    Log.e(TAG,"error" + response.code() + response.errorBody());
                    callback.onServerError();
                }


            }

            @Override
            public void onFailure(Call<List<ExerciseProgression>> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
                callback.onNetworkError();
            }
        });
    }

    private void saveExercisesProgressionDatabase(ExerciseProgression exerciseProgression) throws SQLException {
        queries.saveExerciseProgression(exerciseProgression);
    }

}
