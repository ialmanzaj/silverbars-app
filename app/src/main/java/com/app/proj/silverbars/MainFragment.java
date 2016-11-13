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

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    public TwoWayView recyclerView;

    private SwipeRefreshLayout swipeContainer;

    public String muscleData = "ALL";
    private boolean opened;

    LinearLayout noInternetConnectionLayout,failedServerLayout;

    ProgressBar progressBar;

    List<Workout> mWorkouts  = new ArrayList<>();
    private WorkoutAdapter adapter;


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
                Log.v(TAG,"button_failed_server");

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
                Log.v(TAG,"button_reload_no_internet");

                if (CheckInternet(getActivity().getApplicationContext())){
                    swipeContainer.setVisibility(View.VISIBLE);
                    getWorkoutsData(muscleData);
                    noInternetConnectionLayout.setVisibility(View.GONE);
                }else {
                    noInternetConnectionLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CheckInternet(getActivity().getApplicationContext())){
                    getWorkoutsData(muscleData);
                } else{
                    swipeContainer.setRefreshing(false);
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return  rootview;
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        muscleData = getArguments().getString("Muscle");

        getWorkoutsData(muscleData);

    }



    private void getWorkoutsData(String muscle){
        muscleData = muscle;

        if (!swipeContainer.isRefreshing()){
            progressBar.setVisibility(View.VISIBLE);
        }


        MainService service = ServiceGenerator.createService(MainService.class);
        service.getWorkouts().enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if (response.isSuccessful()) {
                        onErrorViewoff();
                        progressBar.setVisibility(View.GONE);

                        mWorkouts = response.body();

                        if (adapter != null){

                            if (adapter.getItemCount() != mWorkouts.size()){

                            }
                        }else {

                            putItemsInAdapter(mWorkouts);
                        }

                        swipeContainer.setRefreshing(false);

                    } else {
                        Log.e(TAG,"statusCode: "+response.code());
                        onErrorViewOn();
                    }
                }
                @Override
                public void onFailure(Call<List<Workout>> call, Throwable t) {
                    Log.e(TAG,"Workouts, onFailure: ",t);
                    onErrorViewOn();
                }
        });


    }




    private void putItemsInAdapter(List<Workout> workouts){
        adapter = new WorkoutAdapter(getActivity(),workouts);
        recyclerView.setAdapter(adapter);
    }

    public void filterWorkouts(String muscle){
        recyclerView.swapAdapter(adapter,false);


        if(Objects.equals(muscle, "ALL")) {

            adapter.setItems(mWorkouts);

        } else {

            List<Workout> mWorkoutFiltered = new ArrayList<>();

            for (Workout workout: mWorkouts){
                String main_muscle = workout.getMainMuscle();
                if(Objects.equals(muscle, main_muscle)){
                    mWorkoutFiltered.add(workout);
                }
            }

            adapter.setItems(mWorkoutFiltered);
        }


        adapter.notifyDataSetChanged();

    }

    private void onErrorViewOn(){
        failedServerLayout.setVisibility(View.VISIBLE);
    }

    private void onErrorViewoff(){
        failedServerLayout.setVisibility(View.GONE);
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
