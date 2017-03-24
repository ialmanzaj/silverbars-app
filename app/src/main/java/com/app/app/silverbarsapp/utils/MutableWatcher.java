package com.app.app.silverbarsapp.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by isaacalmanza on 03/10/17.
 */

public class MutableWatcher implements TextWatcher {

    private int mPosition;

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public int getPosition(){
        return mPosition;
    }
}