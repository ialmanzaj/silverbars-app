package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.adapters.ViewPagerAdapter;
import com.app.app.silverbarsapp.presenters.BasePresenter;

import butterknife.BindView;

public class MyWorkoutsFragment extends BaseFragment {

    private static final String TAG = MyWorkoutsFragment.class.getSimpleName();

    @BindView(R.id.viewpager)ViewPager viewPager;
    @BindView(R.id.tabs)TabLayout tabLayout;

    @Override
    protected int getFragmentLayout() {
        return R.layout.activity_my_workouts;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.isAdded()) {
            int position = getArguments().getInt("position", 0);

            setupViewPager(viewPager, position);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager,int position) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MainWorkoutsFragment(), getActivity().getString(R.string.activity_workout_title));
        adapter.addFragment(new UserWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_myworkouts));
        //adapter.addFragment(new WorkoutsFinishedFragment(), "Workouts finished");
        //adapter.addFragment(new SavedWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_saved));

        //set adapter
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

}
