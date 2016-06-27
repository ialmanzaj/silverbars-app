package com.app.project.silverbars;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by roberto on 26/05/16.
 */
public class Utils {

    public static void hideKeyboard(Activity context) {
        View v = context.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
