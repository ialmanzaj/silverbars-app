package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerLoginComponent;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.modules.LoginModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.LoginPresenter;
import com.app.app.silverbarsapp.viewsets.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.app.app.silverbarsapp.Constants.PACKAGE;


public class LoginActivity extends BaseAuthenticationActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    LoginPresenter mLoginPresenter;


    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.login_button) Button mLoginButton;

    @BindView(R.id.logo)ImageView mLogo;

    @BindView(R.id.container)RelativeLayout container;
    @BindView(R.id.slogan_login)TextView slogan_login;


    CallbackManager callbackManager;

    private ProfileTracker mProfileTracker;

    ProfileFacebook profileFacebook;


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


        DaggerLoginComponent
                .builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {Log.e(TAG,"Exception",e);}

    }

    @OnClick(R.id.login_button)
    public void login(){

        if (!isNetworkConnected()){
            Toast.makeText(this, "Please, Connect to internet", Toast.LENGTH_LONG).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {container.setBackgroundColor(getResources().getColor(R.color.black,getTheme()));}else {container.setBackgroundColor(getResources().getColor(R.color.black));}

        onLoadingOff();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                onLoadingOn();
                getAccessToken(loginResult.getAccessToken().getToken());

                profileFacebook = new ProfileFacebook();

                if (mProfileTracker == null){
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            // profile2 is the new profile

                            Log.v("facebook - profile id", currentProfile.getId());
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            Log.v("facebook - profile link", String.valueOf(currentProfile.getLinkUri()));

                            profileFacebook.setId(Long.parseLong(currentProfile.getId()));
                            profileFacebook.setFirst_name(currentProfile.getFirstName());
                            profileFacebook.setLast_name(currentProfile.getLastName());
                        }
                    };
                }

            }
            @Override
            public void onCancel() {
                Log.e(TAG, "facebook: onCancel");
            }
            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "facebook Error", exception);
            }
        });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));
    }

    @Override
    public void displayToken(AccessToken accessToken) {

        String user = accessToken.getAccess_token();

        Account account = createOrGetAccount(user);

        storeToken(account, getString(R.string.authentication_TOKEN),  accessToken.getAccess_token(),  accessToken.getRefresh_token());

        // finishes the activity and set this account to the "current-active" one
        finalizeAuthentication(account);

        //save in database
        mLoginPresenter.saveProfile(profileFacebook);
    }


    @Override
    public void profileCreated(Boolean created) {
        if (created){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void getAccessToken(String facebook_token) {
        //get the access token
        mLoginPresenter.getAccessToken(facebook_token);
    }

    @Override
    public void displayNetworkError() {

    }

    @Override
    public void displayServerError() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void onLoadingOn(){
        mLogo.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    private void onLoadingOff(){
        slogan_login.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.GONE);
    }



}

