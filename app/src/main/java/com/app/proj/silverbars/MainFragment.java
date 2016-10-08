package com.app.proj.silverbars;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.internal.CollectionMapper;

import org.lucasr.twowayview.widget.TwoWayView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainFragment extends Fragment {

    private static final String TAG ="MAIN SCREEN FRAGMENT";
    public TwoWayView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    public String muscleData = "ALL";
    private boolean opened;
    LinearLayout noInternetConnectionLayout,failedServerLayout;
    ProgressBar progressBar;

    List<Workout> workoutList  = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_fmain, container, false);

        progressBar = (ProgressBar)  rootview.findViewById(R.id.progress_bar);
        recyclerView = (TwoWayView) rootview.findViewById(R.id.list);


        noInternetConnectionLayout = (LinearLayout) rootview.findViewById(R.id.noInternetConnection_layout);
        failedServerLayout = (LinearLayout) rootview.findViewById(R.id.failed_conection_layout);
        swipeContainer = (SwipeRefreshLayout)rootview.findViewById(R.id.swipeContainer);

        Button button_failed_server = (Button) rootview.findViewById(R.id.button_reload_failed_server);
        button_failed_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckInternet(getActivity().getApplicationContext())){
                    swipeContainer.setVisibility(View.VISIBLE);
                    getWorkoutsData(muscleData);
                    failedServerLayout.setVisibility(View.GONE);
                }else {
                    failedServerLayout.setVisibility(View.VISIBLE);
                }

            }
        });


        Button button_reload_no_internet = (Button) rootview.findViewById(R.id.button_reload_no_internet);
        button_reload_no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckInternet(getActivity().getApplicationContext())){
                    swipeContainer.setVisibility(View.VISIBLE);
                    getWorkoutsData(muscleData);
                    noInternetConnectionLayout.setVisibility(View.GONE);
                }else {
                    noInternetConnectionLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        return  rootview;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        muscleData = getArguments().getString("Muscle");
        opened = getArguments().getBoolean("Opened");


        if (!opened){

            opened = true;
            if (CheckInternet(getActivity().getApplicationContext())){
                getWorkoutsData(muscleData);
                swipeContainer.setVisibility(View.VISIBLE);
                noInternetConnectionLayout.setVisibility(View.GONE);
            }
            else{
                swipeContainer.setVisibility(View.GONE);
                noInternetConnectionLayout.setVisibility(View.VISIBLE);

            }
        } else {

            if (CheckInternet(getActivity().getApplicationContext())){
                getWorkoutsData(muscleData);
                swipeContainer.setVisibility(View.VISIBLE);
                noInternetConnectionLayout.setVisibility(View.GONE);
            }
            else{
                swipeContainer.setVisibility(View.GONE);
                noInternetConnectionLayout.setVisibility(View.VISIBLE);

            }
        }

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CheckInternet(getActivity().getApplicationContext())){
                    getWorkoutsData(muscleData);
                }
                else{
                    swipeContainer.setRefreshing(false);
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }





    public void getWorkoutsData(final String muscle){
        progressBar.setVisibility(View.VISIBLE);
        muscleData = muscle;

        AuthPreferences authPreferences = new AuthPreferences(getActivity());
        SilverbarsService service = ServiceGenerator.createService(SilverbarsService.class,authPreferences.getToken());

        Log.v(TAG,authPreferences.getToken());


        Call<Workout[]> call = service.getWorkouts();
        call.enqueue(new Callback<Workout[]>() {
                    @Override
                    public void onResponse(Call<Workout[]> call, Response<Workout[]> response) {
                        if (response.isSuccessful()) {
                            onErrorViewoff();

                            Workout[] workouts = response.body();
                            Collections.addAll(workoutList,workouts);


                            recyclerView.setAdapter(new WorkoutAdapter(getActivity(),workoutList));

                        } else {

                            Log.e(TAG,"statusCode: "+response.code());

                            onErrorViewOn();
                        }
                    }
                    @Override
                    protected void finalize() throws Throwable {
                        super.finalize();
                        Log.v(TAG,"ha finalizado");

                    }
                    @Override
                    public void onFailure(Call<Workout[]> call, Throwable t) {
                        Log.e(TAG,"getWorkoutsData, onFailure: ",t);

                        onErrorViewOn();
                    }
                });

        /*
        if (Objects.equals(muscle,"ALL")){



        } else {

            Workout[] auxWorkout = response.body();
            Workout[] workoutFlag = null;
            int x = 0;
            for (Workout anAuxWorkout : auxWorkout) {
                String muscleData = anAuxWorkout.getMain_muscle();
                if (Objects.equals(muscle, muscleData)) {
                    x++;
                }
            }
            Workouts = new Workout[x];
            int y = 0;
            for (Workout anAuxWorkout : auxWorkout) {
                String muscleData = anAuxWorkout.getMain_muscle();
                if (Objects.equals(muscle, muscleData)) {
                    Workouts[y] = anAuxWorkout;
                    //Log.v("Workout", Workouts[y].getWorkout_name());
                    y++;
                }
            }
        }

        */


    }

    public boolean isOpened() {
        return opened;
    }

    public String getMuscleData() {
        return muscleData;
    }

    private void onErrorViewOn(){
        progressBar.setVisibility(View.GONE);
        swipeContainer.setVisibility(View.GONE);
        failedServerLayout.setVisibility(View.VISIBLE);

    }
    private void onErrorViewoff(){
        progressBar.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);

    }


    private boolean CheckInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = cm.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }



}
