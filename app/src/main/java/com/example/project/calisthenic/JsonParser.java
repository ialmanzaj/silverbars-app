package com.example.project.calisthenic;

/**
 * Created by andre_000 on 6/7/2016.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;

import android.util.Log;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonParser {
   public OkHttpClient client = new OkHttpClient();

        JsonWorkout[] getWorkouts(String urlString) throws IOException, JSONException {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();
            Response responses = null;
            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();
            Gson gson = new Gson();
            JsonWorkout[] workout = gson.fromJson(jsonData, JsonWorkout[].class);
            Log.v("Json",Arrays.toString(workout));

            return workout;
        }

        JsonExercise getExercise(String urlString) throws Exception {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();
            Response responses = null;
            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();
            Gson gson = new Gson();
            JsonExercise exercise = gson.fromJson(jsonData, JsonExercise.class);
//            Log.v("Workouts:",Arrays.toString(workout));
            return exercise;
        }

        JsonReps[] getReps(String urlString, int workout_id, int exercises) throws Exception {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();
            Response responses = null;
            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();
            Gson gson = new Gson();
            JsonReps[] reps = gson.fromJson(jsonData, JsonReps[].class);
            JsonReps[] ParsedReps = new JsonReps[exercises];
            int y = 0;
            for (int z = 0; z < reps.length; z++){
                String workout = reps[z].workout_id;
                if (workout.indexOf("workouts/"+workout_id)>0){
                    ParsedReps[y] = reps[z];
                    y++;
                }
            }
//            Log.v("Workouts:",Arrays.toString(ParsedReps));
            return ParsedReps;
        }
}