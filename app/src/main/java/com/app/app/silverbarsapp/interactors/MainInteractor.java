package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.handlers.DatabaseHelper;
import com.app.app.silverbarsapp.handlers.DatabaseQueries;

/**
 * Created by isaacalmanza on 05/16/17.
 */

public class MainInteractor {

    DatabaseQueries queries;

    public MainInteractor(DatabaseHelper helper){
        queries  = new DatabaseQueries(helper);
    }



}
