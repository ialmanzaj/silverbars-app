package com.app.proj.silverbars;

import android.content.Context;
import android.util.Log;

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

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class ServiceGenerator {

    public static final String API_BASE_URL = "https://api.silverbarsapp.com";
    private static final String TAG = "Service Generator";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass,null);
    }

    public static <S> S createService(Class<S> serviceClass, final String token) {
        if (token != null) {


            //logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            httpClient.addInterceptor(logging);
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    Response response = chain.proceed(request);
                    Log.v(TAG, "interceptor 1: " + response.code());
                    return response;
                }
            });

        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(final Class<S> serviceClass, final String token, final Context context) {
        if (token != null && context != null) {

            //logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            //httpClient.addInterceptor(logging);


            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    Response response = chain.proceed(request);
                    Log.v(TAG, "interceptor 2: " + response.code());
                    return response;
                }
            });


           httpClient.authenticator(new okhttp3.Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    System.out.println("Authenticating for response: " + response);
                    System.out.println("Challenges: " + response.challenges());

                    if (responseCount(response) >= 3) {
                        return null; // If we've failed 3 times, give up.
                    }

                    TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(context);
                    tokenAuthenticator.setRefreshToken(token);
                    String refresh_token = tokenAuthenticator.getToken();

                    Log.v(TAG,"old token:"+token);
                    Log.v(TAG,"new token:"+refresh_token);

                    return response.request().newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + refresh_token)
                            .build();

                }

                private int responseCount(Response response) {
                    int result = 1;
                    while ((response = response.priorResponse()) != null) {
                        result++;
                        Log.v(TAG, String.valueOf(result));
                    }
                    return result;
                }
            });


        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }





}