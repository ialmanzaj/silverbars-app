package com.app.proj.silverbars;

import android.content.Context;
import android.util.Log;

import com.andretietz.retroauth.Retroauth;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.andretietz.retroauth.AndroidAuthenticationHandler;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class ServiceGenerator {

    public static final String API_BASE_URL = "https://api.silverbarsapp.com";
    private static final String TAG = "Service Generator";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {

        TokenProvider tokenProvider = new TokenProvider();

        Retroauth.Builder builder = new Retroauth.Builder<>(AndroidAuthenticationHandler.create(tokenProvider))
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        //logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(logging);


        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }





}