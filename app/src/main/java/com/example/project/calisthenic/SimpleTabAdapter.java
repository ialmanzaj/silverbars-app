package com.example.project.calisthenic;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by andre_000 on 4/3/2016.
 */
public class SimpleTabAdapter extends FragmentPagerAdapter {

    private int page = 4;
    private String[] tabtitle = new String[]{"Home", "Search", "Workout", "Profile"};

    public SimpleTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Fmain();
            case 1:
                return new Search();
            case 2:
//                return new Workout();
            case 3:
                return new Profile();
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