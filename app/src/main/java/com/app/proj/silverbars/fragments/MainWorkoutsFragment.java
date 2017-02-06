package com.app.proj.silverbars.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.adapters.WorkoutsAdapter;
import com.app.proj.silverbars.components.DaggerMainWorkoutsComponent;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.modules.MainWorkoutsModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.MainWorkoutsPresenter;
import com.app.proj.silverbars.viewsets.MainWorkoutsView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.lucasr.twowayview.widget.TwoWayView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class MainWorkoutsFragment extends BaseFragment implements MainWorkoutsView{


    private static final String TAG = MainWorkoutsFragment.class.getSimpleName();

    @Inject
    MainWorkoutsPresenter mMainWorkoutsPresenter;


    @BindView(R.id.list) TwoWayView list;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    private WorkoutsAdapter adapter;

    public String mMuscleSelected = "ALL";


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_main_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mMainWorkoutsPresenter;
    }



    @Override
    public void injectDependencies() {
        super.injectDependencies();



        DaggerMainWorkoutsComponent
                .builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .mainWorkoutsModule(new MainWorkoutsModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mMuscleSelected = getArguments().getString("Muscle");



        //get my workouts from api
       //mMainWorkoutsPresenter.getMyWorkout();
    }




    private String getJson(){
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("example.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.isAdded()){

            adapter = new WorkoutsAdapter(getActivity());
            list.setAdapter(adapter);


            Log.i(TAG,"workout"+getJson());


           List<Workout> workouts = new Gson().fromJson(getJson(),new TypeToken<ArrayList<Workout>>(){}.getType());


            setWorkoutsInAdapter(workouts);
        }


        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(() -> mMainWorkoutsPresenter.fetchTimelineAsync());
    }



    @Override
    public void displayWorkouts(List<Workout> workouts) {
        setWorkoutsInAdapter(workouts);
    }

    @Override
    public void displayNetworkError() {

    }

    @Override
    public void displayServerError() {

    }

    private void setWorkoutsInAdapter(List<Workout> workouts){
        adapter.setWorkouts(filterWorkouts(mMuscleSelected,workouts));
    }


    public List<Workout> filterWorkouts(String muscle,List<Workout> workouts){
        adapter.clear();
        
        if(Objects.equals(muscle, "ALL")) {

            return workouts;

        } else {

            List<Workout> mWorkoutFiltered = new ArrayList<>();

            for (Workout workout: workouts){
                String main_muscle = workout.getMainMuscle();
                if(Objects.equals(muscle, main_muscle)){
                    mWorkoutFiltered.add(workout);
                }
            }

            return mWorkoutFiltered;

        }


    }



}
