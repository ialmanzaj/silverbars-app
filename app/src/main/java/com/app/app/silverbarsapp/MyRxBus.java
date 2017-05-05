package com.app.app.silverbarsapp;

import android.content.Context;

import com.app.app.silverbarsapp.models.Metadata;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by isaacalmanza on 05/02/17.
 */

public class MyRxBus {

    private static MyRxBus instance;

    private PublishSubject<Object> subject = PublishSubject.create();

    public static MyRxBus instanceOf() {
        if (instance == null) {
            instance = new MyRxBus();
        }
        return instance;
    }

    /**
     * Pass any event down to event listeners.
     */
    public void setContext(Context context) {
        subject.onNext(context);
    }


    public void setString(Metadata metadata) {
        subject.onNext(metadata);
    }




    /**
     * Subscribe to this Observable. On event, do something
     * e.g. replace a fragment
     */
    public Observable<Object> getEvents() {
        return subject;
    }



}
