package com.app.proj.silverbarsapp.fragments;

/**
 * Created by isaacalmanza on 01/07/17.
 */


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.proj.silverbarsapp.presenters.BasePresenter;

import butterknife.ButterKnife;

/**
 * Created by Pedro Antonio Hernández on 14/06/2015.
 *
 * <p>
 *     A fragment like an activity only will execute operations that affect the UI.
 *     These operations are defined by a view model and are triggered by its presenter.
 * </p>
 */
public abstract class BaseFragment extends Fragment {

    protected Context CONTEXT;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CONTEXT = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getPresenter() != null)
            getPresenter().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getPresenter() != null)
            getPresenter().onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

        if (getPresenter() != null)
            getPresenter().onPause();
    }


    @Override
    public void onStop() {
        super.onStop();

        if (getPresenter() != null)
            getPresenter().onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindViews();
    }

    @Override
    public void onDestroy() {

        if (getPresenter() != null)
            getPresenter().onDestroy();


        super.onDestroy();
    }

    protected abstract int getFragmentLayout();

    /**
     * @return The presenter attached to the fragment. This must extends from {@link BasePresenter}
     */
    protected abstract BasePresenter getPresenter();

    /**
     * Override this method in case you need to inject dependencies
     */
    public void injectDependencies() {
    }

    /**
     * Replace all the annotated fields with ButterKnife annotations with the proper value
     */
    private void bindViews(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    private void unbindViews() {
        //ButterKnife.unbind(this);
    }




}