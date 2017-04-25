package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.SavedWorkoutsAdapter;
import com.app.app.silverbarsapp.components.DaggerSavedWorkoutsComponent;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.SavedWorkoutsModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.SavedWorkoutsPresenter;
import com.app.app.silverbarsapp.viewsets.SavedWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class SavedWorkoutsFragment extends BaseFragment implements SavedWorkoutsView {

    private static final String TAG = SavedWorkoutsFragment.class.getSimpleName();

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

        mSavedWorkoutsPresenter.getWorkouts();
    }

    @Override
    public void onWorkouts(List<Workout> workouts) {
        onEmptyOff();
        adapter.set(workouts);
    }

    @Override
    public void onEmptyWorkouts() {
        Log.i(TAG,"empty");
        onEmptyOn();
    }

    private void onEmptyOff(){
        mEmpyStateSavedWorkout.setVisibility(View.GONE);
    }

    private void onEmptyOn(){
        mEmpyStateSavedWorkout.setVisibility(View.VISIBLE);
    }

}
