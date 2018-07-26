package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ViewPagerAdapter;
import com.app.app.silverbarsapp.components.DaggerLoginComponent;
import com.app.app.silverbarsapp.fragments.MainWorkoutsFragment;
import com.app.app.silverbarsapp.fragments.SavedWorkoutsFragment;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.modules.LoginModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.LoginPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;

import static com.app.app.silverbarsapp.Constants.PACKAGE;


public class LoginActivity extends BaseAuthenticationActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    LoginPresenter mLoginPresenter;

    @BindView(R.id.loading)RelativeLayout mLoadingView;
    @BindView(R.id.login_button) LoginButton mFacebookButton;

    private CallbackManager callbackManager;

    private String mFbToken;
    private Account account;

    private Utilities utilities = new Utilities();
    private PendingAction pendingAction = PendingAction.NONE;

    private enum PendingAction {
        NONE,
        SENDED,
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mLoginPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerLoginComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    protected void setFacebookSettings() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //Log.d("KeyHash ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            //Log.e(TAG,"NameNotFoundException or NoSuchAlgorithmException",e);
            }
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

      /*  ViewPager mImageViewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);

        setupViewPager(mImageViewPager,0);
        tabLayout.setupWithViewPager(mImageViewPager, true);*/


        mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        mFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onLoadingOn();

                //saving the token
                mFbToken = loginResult.getAccessToken().getToken();

                //GET user data
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        (object, response) -> {

                            String id = "";
                            String email = "";
                            String birthday = "";
                            String gender = "";
                            String name = "";

                            try {

                                id = object.getString("id");
                                name = object.getString("name");
                                email = object.getString("email");
                                birthday = object.getString("birthday"); // 01/31/1980 format
                                gender = object.getString("gender");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (name != null) {
                                getProfileInfo(
                                        id,
                                        name,
                                        birthday,
                                        gender,
                                        email
                                );
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {}
            @Override
            public void onError(FacebookException exception) {
                connect();
            }
        });


        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {}
        };
        profileTracker.startTracking();
    }

    private void setupViewPager(ViewPager viewPager,int position) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainWorkoutsFragment(), "");
        adapter.addFragment(new SavedWorkoutsFragment(), "");

        //adapter.addFragment(new WorkoutsFinishedFragment(), "Workouts finished");
        //adapter.addFragment(new SavedWorkoutsFragment(), getActivity().getString(R.string.myworkouts_fragment_saved));

        //set adapter
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    private void getProfileInfo(String id,String first_name,String birthday,String gender,String email) {
        switch (pendingAction) {
            case NONE:
                saveUserData(id,first_name,birthday,gender,email);
                pendingAction = PendingAction.SENDED;
                break;
        }
    }

    private void saveUserData(String id,String name,String birthday,String gender,String email){
        if (account == null) {
            //current account
            account = createOrGetAccount(name);

            //store data
            storeUserData(account, getString(R.string.authentication_ID),
                    !Objects.equals(id, "") ? id : "id");

            storeUserData(account, getString(R.string.authentication_NAME),
                    !Objects.equals(name, "") ? name : "name");

            storeUserData(account, getString(R.string.authentication_EMAIL),
                    !Objects.equals(email, "") ? email : "email");

            storeUserData(account,getString(R.string.authentication_GENDER),
                    !Objects.equals(gender, "") ? gender : "gender");

            storeUserData(account,getString(R.string.authentication_AGE),
                    !Objects.equals(birthday, "") ? utilities.getAge(birthday) : "age");
        }

        //get access token
        mLoginPresenter.getAccessToken(mFbToken);
    }

    @Override
    public void displayToken(AccessToken accessToken) {
        storeToken(account, getString(R.string.authentication_TOKEN),  accessToken.getAccess_token(),  accessToken.getRefresh_token());
        storeUserData(account,getString(R.string.authentication_USER),"user");

        // finishes the activity and set this account to the "current-active" one
        startActivity(new Intent(this, UserPreferencesActivity.class));
        finalizeAuthentication(account);
    }

    @Override
    public void displayNetworkError() {
       onError();
    }

    @Override
    public void displayServerError() {
        onError();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    /**
     *
     *
     *
     *
     *     UI events
     *
     */


    private void onError(){
        onLoadingOff();
        facebookLogout();
        new Utilities().toast(this,getString(R.string.connect_internet));
    }

    private void connect(){
        utilities.toast(this,getString(R.string.connect_internet));
    }
    private void onLoadingOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void facebookLogout(){
        //facebook logout
        LoginManager.getInstance().logOut();
    }




    private void onLoadingOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

