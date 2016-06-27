package com.app.project.silverbars;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by andre_000 on 4/3/2016.
 */
public class SimpleTabAdapter extends FragmentPagerAdapter {

    private int page = 3;
    private String[] tabtitle = new String[]{"Workouts", "My Workouts",  "Profile"};

    public SimpleTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MainFragment();
            case 1:
                return new WorkoutsFragment();
            case 2:
                return new ProfileFragment();
            default:
                return null;
        }

//        return view;
    }

    @Override
    public int getCount() {
        return page;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitle[position];
    }
}