package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.CreateWorkoutCallback;

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

    }


}
