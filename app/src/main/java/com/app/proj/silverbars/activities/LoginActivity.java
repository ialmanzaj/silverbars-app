package com.app.proj.silverbars.activities;

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

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.components.DaggerLoginComponent;
import com.app.proj.silverbars.models.AccessToken;
import com.app.proj.silverbars.modules.LoginModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.LoginPresenter;
import com.app.proj.silverbars.viewsets.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;

import static com.app.proj.silverbars.Constants.PACKAGE;


public class LoginActivity extends BaseLoginActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getSimpleName();


    @Inject
    LoginPresenter mLoginPresenter;



    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.login_button) Button mLoginButton;

    @BindView(R.id.logo)ImageView mLogo;

    @BindView(R.id.container)RelativeLayout container;
    @BindView(R.id.slogan_login)TextView slogan_login;


    CallbackManager callbackManager;


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
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {}




        mLoginButton.setOnClickListener(view -> {
            //Log.v(TAG,"onclick");


            if (isNetworkConnected()){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        container.setBackgroundColor(getResources().getColor(R.color.black,getTheme()));
                    }else {
                        container.setBackgroundColor(getResources().getColor(R.color.black));
                    }


                slogan_login.setVisibility(View.VISIBLE);
                mLoginButton.setVisibility(View.GONE);

                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        //Log.v(TAG, "loginButton: onSuccess");

                        getAccessToken(loginResult.getAccessToken().getToken());

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

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email", "user_friends"));



            }else
                Toast.makeText(this, "Please, Connect to internet", Toast.LENGTH_LONG).show();
        });

    }
    

    @Override
    public void displayToken(AccessToken accessToken) {

        String user;
        user = accessToken.getAccess_token();


        Account account = createOrGetAccount(user);

        storeToken(account, getString(R.string.authentication_TOKEN),  accessToken.getAccess_token(),  accessToken.getRefresh_token());

        // finishes the activity and set this account to the "current-active" one
        finalizeAuthentication(account);


        startActivity(new Intent(this, MainActivity.class));
        finish();
    }



    @Override
    public void displayNetworkError() {

    }

    @Override
    public void displayServerError() {

    }
    

    private void getAccessToken(String facebook_token) {
        mLogo.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);



        //get the access token
        mLoginPresenter.getAccessToken(facebook_token);
    }




    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}

