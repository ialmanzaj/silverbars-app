package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.activities.CreateWorkoutActivity;
import com.app.app.silverbarsapp.adapters.ViewPagerAdapter;
import com.app.app.silverbarsapp.presenters.BasePresenter;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class WorkoutsFragment extends BaseFragment {

    private static final String TAG = WorkoutsFragment.class.getSimpleName();

    @BindView(R.id.viewpager)ViewPager viewPager;
    @BindView(R.id.tabs)TabLayout tabLayout;

    @Override
    protected int getFragmentLayout() {
        return R.layout.activity_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {

            setupViewPager(viewPager, 0);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager,int position) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MainWorkoutsFragment(), getActivity().getString(R.string.activity_workout_title));
        adapter.addFragment(new UserWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_myworkouts));
        adapter.addFragment(new SavedWorkoutsFragment(), "Saved workouts");

        //adapter.addFragment(new WorkoutsFinishedFragment(), "Workouts finished");
        //adapter.addFragment(new SavedWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_saved));

        //set adapter
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    @OnClick(R.id.fab)
    public void createButton(){
        startActivityForResult(new Intent(CONTEXT,CreateWorkoutActivity.class),1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){

                //created succesfully
                //extras.putInt("position",1);
                //selectItem(0);
            }
        }
    }



}
