package com.app.app.silverbarsapp;

import com.andretietz.retroauth.AndroidAuthenticationHandler;
import com.andretietz.retroauth.Retroauth;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
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
            .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {

        //logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }




}