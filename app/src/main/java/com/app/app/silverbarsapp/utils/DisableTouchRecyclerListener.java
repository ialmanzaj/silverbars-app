package com.app.app.silverbarsapp.utils;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by andre_000 on 5/30/2016.
 */
public class DisableTouchRecyclerListener extends RecyclerView.OnScrollListener implements RecyclerView.OnItemTouchListener {

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
