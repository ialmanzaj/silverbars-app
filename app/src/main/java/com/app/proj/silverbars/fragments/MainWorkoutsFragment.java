package com.app.proj.silverbars.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.ServiceGenerator;
import com.app.proj.silverbars.adapters.WorkoutsAdapter;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.MainWorkoutsPresenter;
import com.app.proj.silverbars.viewsets.MainWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public class MainWorkoutsFragment extends BaseFragment implements MainWorkoutsView{

    private static final String TAG = MainWorkoutsFragment.class.getSimpleName();

    private MainService service = ServiceGenerator.createService(MainService.class);


    MainWorkoutsPresenter mMainWorkoutsPresenter;




    @BindView(R.id.list) TwoWayView list;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.progress_bar) ProgressBar progressBar;




    List<Workout> mWorkouts  = new ArrayList<>();
    private WorkoutsAdapter adapter;

    public String muscleData = "ALL";
    private boolean opened;



    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_fmain;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mMainWorkoutsPresenter;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        muscleData = getArguments().getString("Muscle");
        getWorkoutsData(muscleData);

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


    }


    private void putItemsInAdapter(List<Workout> workouts){
        adapter = new WorkoutsAdapter(getActivity(),workouts);
        list.setAdapter(adapter);
    }


    public void filterWorkouts(String muscle){
        list.swapAdapter(adapter,false);


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


    @Override
    public void getWorkouts(List<Workout> workouts) {
        mWorkouts = workouts;

        if (adapter != null){

            if (adapter.getItemCount() != mWorkouts.size()){


            }

        }else {

            putItemsInAdapter(mWorkouts);
        }

        swipeContainer.setRefreshing(false);
    }




    @Override
    public void displayNetworkError() {

    }

    @Override
    public void displayServerError() {

    }
}
