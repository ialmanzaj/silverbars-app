package com.example.project.calisthenic;

import retrofit.RestAdapter;

/**
 * Created by andre_000 on 6/17/2016.
 */
public class ServiceFactory {

    static <T> T createRetrofitService(final Class<T> clazz, final String endPoint) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .build();
        T service = restAdapter.create(clazz);

        return service;
    }

}
