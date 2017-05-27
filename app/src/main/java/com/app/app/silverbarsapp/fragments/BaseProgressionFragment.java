package com.app.app.silverbarsapp.fragments;


import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.app.app.silverbarsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseProgressionFragment extends BaseFragmentExtended {


    enum PENDING_ACTIONS {
        NONE,
        CHANGE_TO_EMPTY,
        CHANGED_BODY
    }


    protected void changeFragment(Fragment currentFragment){
        new Handler().post(() -> {
            if (isAdded()) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, currentFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        });
    }




}
