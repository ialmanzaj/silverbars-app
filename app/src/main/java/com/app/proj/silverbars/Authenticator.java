package com.app.proj.silverbars;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by isaacalmanza on 09/23/16.
 */

public class Authenticator {

    private static final String CONSUMER_KEY = "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz";
    private static final String CONSUMER_SECRET = "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e";
    private static final String TAG = "Authenticator";

    Context context;

    public Authenticator(Context context){
        this.context = context;
    }


    public void getInitalAccessToken(final String facebook_token) {

        final AuthPreferences authPreferences = new AuthPreferences(context);

        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<AccessToken> call = loginService.getAccessToken("convert_token",CONSUMER_KEY,CONSUMER_SECRET,"facebook",facebook_token);

       call.enqueue(new retrofit2.Callback<AccessToken>() {
            @Override
            public void onResponse(retrofit2.Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {

                    AccessToken accessToken = response.body();

                    Log.v(TAG, "initial accessToken: " + accessToken.getAccess_token());
                    Log.v(TAG, "Expires_in: " + accessToken.getExpires_in());
                    Log.v(TAG, "initial refresh token: " + accessToken.getRefresh_token());


                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    int hour = cal.get(Calendar.HOUR_OF_DAY)+(accessToken.getExpires_in()/3600);
                    cal.set(Calendar.HOUR_OF_DAY,hour);

                    Log.v(TAG,"current date: "+dateFormat.format(cal.getTime()));

                    authPreferences.setToken(accessToken.getAccess_token());
                    authPreferences.setRefreshToken(accessToken.getRefresh_token());
                    authPreferences.setScope(accessToken.getScope());
                    authPreferences.setCurrentHour(dateFormat.format(cal.getTime()));

                } else {

                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG, errorBody.toString());
                    Log.e(TAG, "statusCode:" + statusCode);

                }
            }
            @Override
            public void onFailure(retrofit2.Call<AccessToken> call, Throwable t) {Log.e(TAG, "get initial token, onFailure", t);}
        });

    }


    public void refreshToken(){
        Log.v(TAG,"refreshToken: called");

        final AuthPreferences preferences = new AuthPreferences(context);

        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<AccessToken> call = loginService.getRefreshAccessToken("refresh_token",CONSUMER_KEY,CONSUMER_SECRET,preferences.getRefreshToken());


        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {


                    AccessToken accessToken = response.body();
                    Log.v(TAG,"new accessToken: "+accessToken.getAccess_token());
                    Log.v(TAG,"Expires_in: "+accessToken.getExpires_in()/3600);
                    Log.v(TAG,"scope: "+accessToken.getScope());


                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    int hour = cal.get(Calendar.HOUR_OF_DAY)+(accessToken.getExpires_in()/3600);
                    cal.set(Calendar.HOUR_OF_DAY,hour);


                    Log.v(TAG,"refresh current date: "+dateFormat.format(cal.getTime()));

                    preferences.setToken(accessToken.getAccess_token());
                    preferences.setRefreshToken(accessToken.getRefresh_token());
                    preferences.setScope(accessToken.getScope());
                    preferences.setCurrentHour(dateFormat.format(cal.getTime()));

                }else {

                    Log.e(TAG, "statusCode:" + response.code());
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG,"refresh accessToken, onFailure",t);
            }
        });

    }



    public Boolean isAuthenticated(){

        AuthPreferences preferences = new AuthPreferences(context);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        try {

            Date refresh_token_hour = dateFormat.parse(preferences.getCurrentHour());
            Date current_date = dateFormat.parse(dateFormat.format(cal.getTime()));


            Log.v(TAG,"past date: "+refresh_token_hour);
            Log.v(TAG,"current: "+current_date);

            if (refresh_token_hour.after(current_date)){
                return true;
            }

        } catch (ParseException e){
            Log.e(TAG,"ParseException: "+e);
        }

        return false;
    }

}
