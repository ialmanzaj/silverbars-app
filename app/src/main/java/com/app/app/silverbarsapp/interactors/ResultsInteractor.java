package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public class ResultsInteractor {

    private static final String TAG = ResultsInteractor.class.getSimpleName();

    private DatabaseHelper databaseHelper;
    private MainService mainService;



    public ResultsInteractor(DatabaseHelper helper,MainService mainService){
        this.databaseHelper = helper;
        this.mainService = mainService;
    }


}
