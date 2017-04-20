package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerLoginComponent;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.modules.LoginModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.LoginPresenter;
import com.app.app.silverbarsapp.viewsets.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;

import static com.app.app.silverbarsapp.Constants.PACKAGE;


public class LoginActivity extends BaseAuthenticationActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    LoginPresenter mLoginPresenter;

    @BindView(R.id.loading)LinearLayout mLoadingView;

    @BindView(R.id.login_button) LoginButton mFacebookButton;

    private CallbackManager callbackManager;

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
    protected void setFacebookSettings() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {Log.e(TAG,"NameNotFoundException or NoSuchAlgorithmException",e);}

        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        mFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onLoadingOn();
                mLoginPresenter.onLoginSuccess(loginResult);
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

    }

    @Override
    public void displayToken(AccessToken accessToken,String account_name) {
        Log.d(TAG,"displayToken "+account_name);
        Account account = createOrGetAccount(account_name);
        storeToken(account, getString(R.string.authentication_TOKEN),  accessToken.getAccess_token(),  accessToken.getRefresh_token());

        // finishes the activity and set this account to the "current-active" one
        startActivity(new Intent(this, UserPreferencesActivity.class));
        finalizeAuthentication(account);
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
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
        mLoadingView.setVisibility(View.VISIBLE);
    }

}

