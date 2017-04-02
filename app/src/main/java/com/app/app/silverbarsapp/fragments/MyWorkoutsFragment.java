package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.adapters.ViewPagerAdapter;

public class MyWorkoutsFragment extends Fragment {

    private static final String TAG = MyWorkoutsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.activity_my_workouts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.isAdded()) {
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new UserWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_myworkouts));
        //adapter.addFragment(new SavedWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_saved));
        viewPager.setAdapter(adapter);
    }






}
