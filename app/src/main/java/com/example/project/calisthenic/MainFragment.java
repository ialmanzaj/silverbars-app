package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.Arrays;

public class MainFragment extends Fragment {

    private String email;
    private String name;
    private TwoWayView recyclerView;
    private Button songs;
    public static JsonWorkout[] Workouts;


    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fmain, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new AsyncTaskParseJson().execute();
        Intent intent = this.getActivity().getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");
    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {
            JsonParser JsonData = new JsonParser();
            try {
                Workouts = JsonData.getWorkouts("http://api.silverbarsapp.com/workouts/?format=json");
                Log.v("Workout:",Arrays.toString(Workouts));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            recyclerView = (TwoWayView) getView().findViewById(R.id.list);
            recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
        }
    }

    public boolean CheckInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
