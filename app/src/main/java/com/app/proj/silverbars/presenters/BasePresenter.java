package com.app.proj.silverbars.presenters;

/**
 * Created by Pedro Antonio Hernández on 04/06/2015.
 *
 * <p>
 * Base model for every presenter tha will be connected to a fragment or activity
 * </p>
 *
 * <p>
 *  The presenter will be the bridge between Activity or Fragment and an interactor.
 *  Basically a presenter will communicate the results of background tasks (like a server request
 *  or query to database) that are needed to affect the UI
 * </p>
 */
public abstract class BasePresenter {


    public abstract void onStart();
    public abstract void onStop();



}



