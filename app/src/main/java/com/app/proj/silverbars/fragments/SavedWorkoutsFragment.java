package com.app.proj.silverbars.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.adapters.SavedWorkoutsAdapter;
import com.app.proj.silverbars.components.DaggerSavedWorkoutsComponent;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.modules.SavedWorkoutsModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.SavedWorkoutsPresenter;
import com.app.proj.silverbars.viewsets.SavedWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class SavedWorkoutsFragment extends BaseFragment implements SavedWorkoutsView {

    private static final String TAG = "SavedWorkoutsFragment";

    @BindView(R.id.list_saved) TwoWayView mLocalWorkoutsList;
    @BindView(R.id.empty_state)  LinearLayout mEmpyStateSavedWorkout;


    @Inject
    SavedWorkoutsPresenter mSavedWorkoutsPresenter;

    SavedWorkoutsAdapter adapter;


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_saved_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mSavedWorkoutsPresenter;
    }


    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerSavedWorkoutsComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .savedWorkoutsModule(new SavedWorkoutsModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.isAdded()){
            adapter = new SavedWorkoutsAdapter(getActivity());
            mLocalWorkoutsList.setAdapter(adapter);
        }



        try {
            mSavedWorkoutsPresenter.getWorkouts();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onWorkouts(List<Workout> workouts) {
        Log.i(TAG,"workouts"+workouts);
        onEmptyOff();
        adapter.set(workouts);
    }

    @Override
    public void onEmptyWorkouts() {
        Log.i(TAG,"empty");
    }




    private void onEmptyOff(){
        mEmpyStateSavedWorkout.setVisibility(View.GONE);
    }


}
