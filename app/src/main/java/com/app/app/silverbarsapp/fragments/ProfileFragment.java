package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.WorkoutsDoneActivity;
import com.app.app.silverbarsapp.components.DaggerProfileComponent;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.modules.ProfileModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProfilePresenter;
import com.app.app.silverbarsapp.viewsets.ProfileView;

import javax.inject.Inject;

import butterknife.BindView;


public class ProfileFragment extends BaseFragment implements ProfileView{

    private static final String TAG = ProfileFragment.class.getSimpleName();

    @BindView(R.id.Profile_name) TextView mProfileName;
    @BindView(R.id.profile_image) ImageView mProfileImg;

    @BindView(R.id.my_workouts_done) LinearLayout mMyWorkoutsDone;

    @Inject
    ProfilePresenter mProfilePresenter;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_profile;
    }


    @Override
    protected BasePresenter getPresenter() {
        return mProfilePresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerProfileComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .profileModule(new ProfileModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProfilePresenter.getProfile();

        mMyWorkoutsDone.setOnClickListener(v -> {startActivity(new Intent(CONTEXT, WorkoutsDoneActivity.class));});
    }


    @Override
    public void displayProfileFacebook(ProfileFacebook profile) {
        mProfileName.setText(profile.getFirst_name() +" "+ profile.getLast_name());
    }


}
