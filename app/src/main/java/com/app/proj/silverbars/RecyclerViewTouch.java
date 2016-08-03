package com.app.proj.silverbars;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by andre_000 on 5/30/2016.
 */
public class RecyclerViewTouch implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
