package com.app.proj.silverbars;

/**
 * Created by isaacalmanza on 09/19/16.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";

    private static final String KEY_REFRESH_TOKEN = "refresh";
    private static final String KEY_SCOPE= "scope";
    private static final String KEY_DATE= "date";
    private static final int KEY_EXPIRES = 0;


    private SharedPreferences preferences;

    public AuthPreferences(Context context) {
        preferences = context
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public void setUser(String user) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        editor.apply();
    }

    public void setToken(String token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void setRefreshToken(String refresh_token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_REFRESH_TOKEN, refresh_token);
        editor.apply();
    }

    public void setCurrentHour(String date_to_refresh) {
        Editor editor = preferences.edit();
        editor.putString(KEY_DATE, date_to_refresh);
        editor.apply();
    }

    public void setScope(String scope) {
        Editor editor = preferences.edit();
        editor.putString(KEY_SCOPE, scope);
        editor.apply();
    }




    public String getUser() {
        return preferences.getString(KEY_USER, null);
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public String getScope() {
        return preferences.getString(KEY_SCOPE,null);
    }

    public  String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN,null);
    }

    public  String getCurrentHour() {
        return  preferences.getString(KEY_DATE,null);
    }
}