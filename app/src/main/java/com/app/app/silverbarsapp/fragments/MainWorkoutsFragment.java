package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.WorkoutsAdapter;
import com.app.app.silverbarsapp.components.DaggerMainWorkoutsComponent;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.MainWorkoutsModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.MainWorkoutsPresenter;
import com.app.app.silverbarsapp.viewsets.MainWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class MainWorkoutsFragment extends BaseFragment implements MainWorkoutsView{

    private static final String TAG = MainWorkoutsFragment.class.getSimpleName();

    @Inject
    MainWorkoutsPresenter mMainWorkoutsPresenter;

    @BindView(R.id.list) TwoWayView list;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @BindView(R.id.loading) LinearLayout loadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    private WorkoutsAdapter adapter;
    private String mMuscleSelected = "ALL";


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

       mMainWorkoutsPresenter.getMyWorkout();
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
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(() -> mMainWorkoutsPresenter.fetchTimelineAsync());
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mMainWorkoutsPresenter.getMyWorkout();
    }

    @Override
    public void displayWorkouts(List<Workout> workouts) {
        onLoadingViewOff();
        setWorkoutsInAdapter(workouts);
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
        onErrorViewOn();
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

    private void onLoadingViewOn(){
        loadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        loadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);
    }
}
