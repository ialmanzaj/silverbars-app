package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.ProgressionCallback;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
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

                    if (response.body().isEmpty()){
                        callback.emptyProgress();
                        return;
                    }

                    callback.onProgression(response.body());

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


    public void getMuscle(ProgressionCallback callback,List<Integer> muscles_ids){
        Log.d(TAG,"muscle_id: "+muscles_ids);
        Observable.from(muscles_ids)
                .flatMap(id -> mainService.getMuscle(id))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onMuscle,
                        error -> Log.e(TAG,"error ",error));
    }

    public void unsuscribe(){

    }


}
