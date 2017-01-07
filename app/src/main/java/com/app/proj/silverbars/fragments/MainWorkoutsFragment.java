package com.app.proj.silverbars.fragments;


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

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.ServiceGenerator;
import com.app.proj.silverbars.adapters.WorkoutsAdapter;
import com.app.proj.silverbars.models.Workout;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class MainWorkoutsFragment extends Fragment {

    private static final String TAG = MainWorkoutsFragment.class.getSimpleName();

    private MainService service = ServiceGenerator.createService(MainService.class);

    public TwoWayView recyclerView;

    private SwipeRefreshLayout swipeContainer;

    public String muscleData = "ALL";
    private boolean opened;
    Button button_reload_no_internet;
    Button button_failed_server;

    LinearLayout noInternetConnectionLayout,failedServerLayout;

    ProgressBar progressBar;

    List<Workout> mWorkouts  = new ArrayList<>();
    private WorkoutsAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fmain, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        muscleData = getArguments().getString("Muscle");
        getWorkoutsData(muscleData);


        progressBar = (ProgressBar)  view.findViewById(R.id.progress_bar);
        recyclerView = (TwoWayView) view.findViewById(R.id.list);

        noInternetConnectionLayout = (LinearLayout) view.findViewById(R.id.noInternetConnection_layout);
        failedServerLayout = (LinearLayout) view.findViewById(R.id.failed_conection_layout);
        swipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);

        button_failed_server = (Button) view.findViewById(R.id.button_reload_failed_server);
        button_reload_no_internet = (Button) view.findViewById(R.id.button_reload_no_internet);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }



    private void getWorkoutsData(String muscle){
        muscleData = muscle;

        if (!swipeContainer.isRefreshing()){
            progressBar.setVisibility(View.VISIBLE);
        }



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
        adapter = new WorkoutsAdapter(getActivity(),workouts);
        recyclerView.setAdapter(adapter);
    }




    public void filterWorkouts(String muscle){
        recyclerView.swapAdapter(adapter,false);


        if(Objects.equals(muscle, "ALL")) {

            adapter.setWorkouts(mWorkouts);

        } else {

            List<Workout> mWorkoutFiltered = new ArrayList<>();

            for (Workout workout: mWorkouts){
                String main_muscle = workout.getMainMuscle();
                if(Objects.equals(muscle, main_muscle)){
                    mWorkoutFiltered.add(workout);
                }
            }

            adapter.setWorkouts(mWorkoutFiltered);
        }


        adapter.notifyDataSetChanged();

    }


    private void onErrorViewOn(){
        failedServerLayout.setVisibility(View.VISIBLE);
    }

    private void onErrorViewoff(){
        failedServerLayout.setVisibility(View.GONE);
    }



}
