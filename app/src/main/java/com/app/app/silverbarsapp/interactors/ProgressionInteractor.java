package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ProgressionCallback;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public class ProgressionInteractor {

    private static final String TAG = ProgressionPresenter.class.getSimpleName();

    private MainService mainService;
    private DatabaseHelper helper;

    public ProgressionInteractor(MainService mainService, DatabaseHelper helper){
        this.mainService = mainService;
        this.helper = helper;
    }

    public void getProgression(ProgressionCallback callback){
        mainService.getProgression().enqueue(new Callback<List<MuscleProgression>>() {
            @Override
            public void onResponse(Call<List<MuscleProgression>> call, Response<List<MuscleProgression>> response) {
                if (response.isSuccessful()){
                    callback.onProgression(response.body());
                }else
                    callback.onServerError();
            }
            @Override
            public void onFailure(Call<List<MuscleProgression>> call, Throwable t) {
                callback.onNetworkError();
            }
        });
    }


    public void getMuscle(ProgressionCallback callback,int muscle_id){
        mainService.getMuscle(muscle_id)
                 .subscribeOn(Schedulers.newThread())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Subscriber<Muscle>() {
                     @Override
                     public void onCompleted() {
                         // do nothing
                     }
                     @Override
                     public void onError(Throwable e) {
                         Log.e("error", e.getMessage());
                         callback.onServerError();
                     }
                     @Override
                     public void onNext(Muscle response) {
                         Log.v(TAG, "muscle "+response.getMuscle_name());
                         callback.onMuscle(response);
                     }
                 });
    }
}
