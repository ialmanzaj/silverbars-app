package com.app.proj.silverbars.utils;

/**
 * Created by isaacalmanza on 09/19/16.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private SharedPreferences preferences;


    public AuthPreferences(Context context) {
        preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public AuthPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setUser(String user) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        editor.apply();
    }

    public void setAccessToken(String token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }


    public String getUser() {
        return preferences.getString(KEY_USER, null);
    }

    public String getAccessToken() {
        return preferences.getString(KEY_TOKEN, null);
    }


}