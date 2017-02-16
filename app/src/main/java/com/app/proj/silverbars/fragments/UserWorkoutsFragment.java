package com.app.proj.silverbars.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.adapters.SavedWorkoutsAdapter;
import com.app.proj.silverbars.components.DaggerUserWorkoutsComponent;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.modules.UserWorkoutsModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.UserWorkoutsPresenter;
import com.app.proj.silverbars.viewsets.UserWorkoutsView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class UserWorkoutsFragment extends BaseFragment implements UserWorkoutsView{

    private static final String TAG = UserWorkoutsFragment.class.getSimpleName();

    @BindView(R.id.list_my_workouts) TwoWayView mMyWorkoutViewList;

    @BindView(R.id.empty_state) LinearLayout mEmpyStateMyWorkouts;
    @BindView(R.id.create) Button mCreateWorkoutButton;

    @Inject
    UserWorkoutsPresenter mUserWorkoutsPresenter;

    SavedWorkoutsAdapter adapter;


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


        if (this.isAdded()){
            adapter = new SavedWorkoutsAdapter(getActivity());
            mMyWorkoutViewList.setAdapter(adapter);
        }


        try {

            mUserWorkoutsPresenter.getMyWorkouts();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //mCreateWorkoutButton.setOnClickListener(view -> startActivity(new Intent(getActivity(),CreateWorkoutActivity.class)));
    }


    @Override
    public void onWorkouts(List<Workout> user_workouts) {
        onEmptyViewOff();
        adapter.set(user_workouts);
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
