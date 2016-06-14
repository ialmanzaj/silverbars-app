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

public class JsonParser {

    public static JsonWorkout[] getWorkouts(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            String json = buffer.toString();
            Gson gson = new Gson();
            JsonWorkout[] workout = gson.fromJson(json, JsonWorkout[].class);
//            Log.v("Workouts:",Arrays.toString(workout));
            return workout;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static JsonExercise getExercise(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            String json = buffer.toString();
            Gson gson = new Gson();
            JsonExercise exercise = gson.fromJson(json, JsonExercise.class);
//            Log.v("Workouts:",Arrays.toString(workout));
            return exercise;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static JsonReps[] getReps(String urlString, int workout_id, int exercises) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            String json = buffer.toString();
            Gson gson = new Gson();
            JsonReps[] reps = gson.fromJson(json, JsonReps[].class);
            JsonReps[] ParsedReps = new JsonReps[exercises];
            int y = 0;
            for (int z = 0; z < reps.length; z++){
                String workout = reps[z].workout_id;
                if (workout.indexOf("workouts/"+workout_id)>0){
                    ParsedReps[y] = reps[z];
                    y++;
                }
            }
            Log.v("Workouts:",Arrays.toString(ParsedReps));
            return ParsedReps;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}