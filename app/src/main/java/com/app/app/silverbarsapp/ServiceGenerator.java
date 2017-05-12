package com.app.app.silverbarsapp;

import com.andretietz.retroauth.AndroidAuthenticationHandler;
import com.andretietz.retroauth.Retroauth;
import com.app.app.silverbarsapp.utils.TokenProvider;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.app.app.silverbarsapp.Constants.API_URL;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class ServiceGenerator {

    private static final String TAG = ServiceGenerator.class.getSimpleName();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retroauth.Builder builder = new Retroauth.Builder<>(AndroidAuthenticationHandler.create(new TokenProvider()))
            .baseUrl(API_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());


    private static Retrofit.Builder builder2 = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass,null);
    }

    public static <S> S createService(Class<S> serviceClass,String token) {
        //logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        //time outs
        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(20, TimeUnit.SECONDS);

        // return header complete url with header token
        if (token != null){
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return retrofit.create(serviceClass);
        }

        Retrofit retrofit = builder2.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }



}