package com.app.app.silverbarsapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.CreateWorkoutActivity;
import com.app.app.silverbarsapp.adapters.UserWorkoutsAdapter;
import com.app.app.silverbarsapp.components.DaggerUserWorkoutsComponent;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.UserWorkoutsModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.UserWorkoutsPresenter;
import com.app.app.silverbarsapp.viewsets.UserWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends BaseFragment implements UserWorkoutsView,UserWorkoutsAdapter.OnWorkoutListener{

    private static final String TAG = UserWorkoutsFragment.class.getSimpleName();

    @BindView(R.id.list_my_workouts) TwoWayView mMyWorkoutViewList;

    @BindView(R.id.empty_state) LinearLayout mEmpyStateMyWorkouts;
    @BindView(R.id.create) Button mCreateWorkoutButton;

    @Inject
    UserWorkoutsPresenter mUserWorkoutsPresenter;

    UserWorkoutsAdapter adapter;


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_user_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mUserWorkoutsPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerUserWorkoutsComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .userWorkoutsModule(new UserWorkoutsModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"UserWorkoutsFragment");


        if (this.isAdded()){
            adapter = new UserWorkoutsAdapter(getActivity());
            adapter.setWorkoutListener(this);

            mMyWorkoutViewList.setAdapter(adapter);
        }


        mUserWorkoutsPresenter.getMyWorkouts();
    }

    @OnClick(R.id.create)
    public void createButton(){
        startActivity(new Intent(CONTEXT,CreateWorkoutActivity.class));
    }


    @Override
    public void onWorkouts(List<Workout> user_workouts) {
        Log.d(TAG,"workouts: "+user_workouts.get(0).getWorkout_name());
        onEmptyViewOff();
        adapter.set(user_workouts);
    }

    @Override
    public void deleteWorkout(int workout_id) {
        mUserWorkoutsPresenter.deleteWorkout(workout_id);
    }

    @Override
    public void onEmptyWorkouts() {
        Log.i(TAG,"onEmptyWorkouts");
    }

    private void onEmptyViewOn(){
        mEmpyStateMyWorkouts.setVisibility(View.VISIBLE);
    }

    private void onEmptyViewOff(){
        mEmpyStateMyWorkouts.setVisibility(View.GONE);
    }

}
