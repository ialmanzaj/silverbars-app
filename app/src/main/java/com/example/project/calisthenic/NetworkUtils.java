package com.example.project.calisthenic;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by saleventa7 on 5/26/2016.
 */
public class NetworkUtils {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    private static String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static Track[] searchTracks(String query) {
        try {
            String json = run("http://api.soundcloud.com/tracks?client_id=fefa8a825044cf4e0b5e308fe049e924&q=" + query);
            return gson.fromJson(json, Track[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
