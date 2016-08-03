package com.app.proj.silverbars;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by andre_000 on 6/15/2016.
 */
public class WorkoutsParser {
    OkHttpClient client = new OkHttpClient();

    String getWorkouts(String urlString) throws IOException {
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.v("OkHTTP",response.toString());
            Log.v("OkHTTP return",response.body().toString());
            return response.body().toString();
        }
    }

}
