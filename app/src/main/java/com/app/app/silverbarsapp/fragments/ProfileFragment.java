package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.SettingsActivity;
import com.app.app.silverbarsapp.components.DaggerProfileComponent;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.modules.ProfileModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProfilePresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProfileView;

import java.sql.SQLException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;


public class ProfileFragment extends BaseFragment implements ProfileView{

    private static final String TAG = ProfileFragment.class.getSimpleName();

    @BindView(R.id.Profile_name) TextView mProfileName;
    @BindView(R.id.profile_image) ImageView mProfileImg;

    @BindView(R.id.my_workouts_done) LinearLayout mMyWorkoutsDone;

    @Inject
    ProfilePresenter mProfilePresenter;

    private Utilities utilities = new Utilities();

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
        getImg();

        //mMyWorkoutsDone.setOnClickListener(v -> {startActivity(new Intent(CONTEXT, WorkoutsDoneActivity.class));});
    }

    private void getImg(){
        Bitmap profile_img = getProfileImg();

        if (profile_img != null){
            setProfileImg(getProfileImg());
        }else {

            try {
                mProfilePresenter.getProfileImg();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick(R.id.settings)
    public void settings(){
        CONTEXT.startActivity(new Intent(CONTEXT, SettingsActivity.class));
    }


    @Override
    public void displayProfileFacebook(ProfileFacebook profile) {
        mProfileName.setText(profile.getFirst_name() +" "+ profile.getLast_name());
    }

    @Override
    public void displayProfileImg(ResponseBody img) {
        if (utilities.saveWorkoutImgInDevice(CONTEXT,img,"profile")){
           setProfileImg(getProfileImg());
        }
    }

    private void setProfileImg(Bitmap bitmap){
        mProfileImg.setImageBitmap(bitmap);
    }

    private Bitmap getProfileImg(){
        return utilities.loadWorkoutImageFromDevice(CONTEXT,"profile");
    }

}
