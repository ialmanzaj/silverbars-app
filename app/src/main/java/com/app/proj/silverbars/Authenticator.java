package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by isaacalmanza on 09/23/16.
 */

public class Authenticator {


    private static final String TAG = "Authenticator";
    Context context;

    public Authenticator(Context context){
        this.context = context;

    }


    public String getInitalAccessToken(final String facebook_token) {

        final AuthPreferences authPreferences = new AuthPreferences(context);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                RequestBody formBody = new FormBody.Builder()
                        .add("grant_type", "convert_token")
                        .add("client_id", "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz")
                        .add("client_secret", "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e")
                        .add("backend", "facebook")
                        .add("token", facebook_token)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.silverbarsapp.com/auth/convert-token/")
                        .post(formBody)
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v(TAG, response.toString());
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.silverbarsapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        SilverbarsService service = retrofit.create(SilverbarsService.class);
        retrofit2.Call<AccessToken> call = service.getAccessToken();

        call.enqueue(new retrofit2.Callback<AccessToken>() {
            @Override
            public void onResponse(retrofit2.Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {

                    Calendar c = Calendar.getInstance();
                    int current_hour = c.get(Calendar.HOUR_OF_DAY);
                    int current_min = c.get(Calendar.MINUTE);
                    int current_second = c.get(Calendar.SECOND);

                    Log.v(TAG,"current hour: "+current_hour + ":" + current_min + ":" + current_second);

                    AccessToken accessToken = response.body();

                    Log.v(TAG, "initial accessToken: " + accessToken.getAccess_token());
                    Log.v(TAG, "token type: " + accessToken.getToken_type());
                    Log.v(TAG, "Expires_in: " + accessToken.getExpires_in());
                    Log.v(TAG, "initial refresh token: " + accessToken.getRefresh_token());
                    Log.v(TAG, "scope: " + accessToken.getScope());


                    authPreferences.setToken(accessToken.getAccess_token());
                    authPreferences.setRefreshToken(accessToken.getRefresh_token());
                    authPreferences.setScope(accessToken.getScope());
                    authPreferences.setCurrentHour(current_hour+accessToken.getExpires_in()/3600 + ":" + current_min + ":" + current_second);

                } else {

                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG, errorBody.toString());
                    Log.e(TAG, "statusCode:" + statusCode);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "getAccesstoken from server, onFailure", t);
            }
        });

        return authPreferences.getToken();
    }


    public String refreshToken(){
        Log.v(TAG,"refreshToken: called");

        final AuthPreferences preferences = new AuthPreferences(context);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                RequestBody formBody = new FormBody.Builder()
                        .add("grant_type", "refresh_token")
                        .add("client_id", "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz")
                        .add("client_secret", "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e")
                        .add("refresh_token",preferences.getRefreshToken())
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.silverbarsapp.com/auth/token/")
                        .post(formBody)
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v(TAG,response.toString());
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.silverbarsapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        SilverbarsService service = retrofit.create(SilverbarsService.class);
        retrofit2.Call<AccessToken> call = service.getRefreshAccessToken();

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();

                    Calendar c = Calendar.getInstance();
                    int current_hour = c.get(Calendar.HOUR_OF_DAY);
                    int current_min = c.get(Calendar.MINUTE);
                    int current_second = c.get(Calendar.SECOND);

                    Log.v(TAG,"current_hour: "+current_hour);
                    Log.v(TAG,"current_min: "+current_min);
                    Log.v(TAG,"current_second: "+current_second);

                    Log.v(TAG,"new accessToken: "+accessToken.getAccess_token());
                    Log.v(TAG,"token type: "+accessToken.getToken_type());
                    Log.v(TAG,"Expires_in: "+accessToken.getExpires_in());
                    Log.v(TAG,"new refresh token: "+accessToken.getRefresh_token());
                    Log.v(TAG,"scope: "+accessToken.getScope());


                    preferences.setToken(accessToken.getAccess_token());
                    preferences.setRefreshToken(accessToken.getRefresh_token());
                    preferences.setScope(accessToken.getScope());
                    preferences.setCurrentHour(current_hour+":"+current_min+":"+current_second);
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG,"RefreshAccessToken, onFailure",t);
            }
        });


        return preferences.getToken();
    }

    public Boolean isAuthenticated(){

        AuthPreferences preferences = new AuthPreferences(context);

        Calendar c = Calendar.getInstance();
        int current_hour = c.get(Calendar.HOUR_OF_DAY);
        int current_min = c.get(Calendar.MINUTE);
        int current_second = c.get(Calendar.SECOND);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        try {



            Date refresh_token_hour = sdf.parse(preferences.getCurrentHour());
            Date current_date = sdf.parse(current_hour+":"+current_min+":"+current_second);



            Log.v(TAG,"last token hour: "+refresh_token_hour);
            Log.v(TAG,"current hour: "+current_date);

            if (refresh_token_hour.after(current_date)){
                return true;

            }

        } catch (ParseException e){
            Log.e(TAG,"ParseException: "+e);
        }

        return false;
    }

}
