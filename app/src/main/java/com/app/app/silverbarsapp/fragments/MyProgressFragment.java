package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.adapters.ViewPagerAdapter;


public class MyProgressFragment extends Fragment {

    private static final String TAG = MyProgressFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.isAdded()) {

            Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            getActivity().setTitle("My Progression");


            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPager.setOffscreenPageLimit(3);
            setupViewPager(viewPager);

            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new WeeklyProgressFragment(), getActivity().getString(R.string.progress_fragment_today_title));
        adapter.addFragment(new MonthlyProgressFragment(), "Last 30 days");
        adapter.addFragment(new TotalProgressFragment(), getActivity().getString(R.string.progress_fragment_total_title));
        viewPager.setAdapter(adapter);
    }

}
