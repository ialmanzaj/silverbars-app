package com.app.app.silverbarsapp;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by isaacalmanza on 03/20/17.
 */

public class WebAppInterface {
    Context mContext;


    MuscleListener listener;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c,MuscleListener listener) {
        mContext = c;
        this.listener = listener;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void setMuscle(String muscle_selected) {
        listener.onMuscleSelected(muscle_selected);
    }


}