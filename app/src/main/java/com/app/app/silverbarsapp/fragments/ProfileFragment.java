package com.app.app.silverbarsapp.fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andretietz.retroauth.AuthAccountManager;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.SettingsActivity;
import com.app.app.silverbarsapp.activities.WorkoutsDoneActivity;
import com.app.app.silverbarsapp.components.DaggerProfileComponent;
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

    @Inject
    ProfilePresenter mProfilePresenter;

    @BindView(R.id.Profile_name) TextView mProfileName;
    @BindView(R.id.profile_image) ImageView mProfileImg;

    @BindView(R.id.my_workouts_done) LinearLayout mMyWorkoutsDone;
    @BindView(R.id.last_exercise_progress) LinearLayout mLastExerciseProgression;

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
        AuthAccountManager authAccountManager = new AuthAccountManager();
        Account activeAccount = authAccountManager.getActiveAccount(CONTEXT.getString(R.string.authentication_ACCOUNT));

        String name = AccountManager.get(CONTEXT).getUserData(activeAccount, CONTEXT.getString(R.string.authentication_NAME));
        String id = AccountManager.get(CONTEXT).getUserData(activeAccount, CONTEXT.getString(R.string.authentication_ID));


        displayProfile(name);
        getImg(id);


        mMyWorkoutsDone.setOnClickListener(v -> startActivity(new Intent(CONTEXT, WorkoutsDoneActivity.class)));
        mLastExerciseProgression.setOnClickListener(v -> startActivity(new Intent(CONTEXT, WorkoutsDoneActivity.class)));
    }


    private void getImg(String id){
        Bitmap profile_img = getProfileImg();

        if (profile_img != null){
            setProfileImg(getProfileImg());
        }else {

            try {
                mProfilePresenter.getProfileImg(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.settings)
    public void settings(){
        CONTEXT.startActivity(new Intent(CONTEXT, SettingsActivity.class));
    }

    private void displayProfile(String first_name) {
        mProfileName.setText(first_name);
    }

    private void setProfileImg(Bitmap bitmap){
        mProfileImg.setImageBitmap(bitmap);
    }

    private Bitmap getProfileImg(){return utilities.loadWorkoutImageFromDevice(CONTEXT,"profile");}

    @Override
    public void displayProfileImg(ResponseBody img) {
        if (utilities.saveWorkoutImgInDevice(CONTEXT,img,"profile")){
            setProfileImg(getProfileImg());
        }
    }

}
