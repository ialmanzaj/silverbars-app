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

import inaka.com.tinytask.DoThis;
import inaka.com.tinytask.Something;
import inaka.com.tinytask.TinyTask;

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
//        AsyncTask task = new AsyncTaskParseJson().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        startProgress();
        Task();
        Intent intent = this.getActivity().getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");
    }

    public void Task(){
        TinyTask.perform(new Something<String>() {
            @Override
            public String whichDoes() {
                JsonParser JsonData = new JsonParser();
                String array = null;
                try {
                    Workouts = JsonData.getWorkouts("http://api.silverbarsapp.com/workouts/?format=json");
                    array = Arrays.toString(Workouts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return array; // you write this method..
            }

        }).whenDone(new DoThis<String>() {
            @Override
            public void ifOK(String result) {
                recyclerView = (TwoWayView) getView().findViewById(R.id.list);
                recyclerView.setAdapter(new WorkoutAdapter(getActivity()));
                Log.i("Result", result);
            }

            @Override
            public void ifNotOK(Exception e) {
                Log.i("Result", e.toString());
            }
        }).go();
    }

    public boolean CheckInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
