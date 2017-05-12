package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerLoginComponent;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.models.AccessToken;
import com.app.app.silverbarsapp.modules.LoginModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.LoginPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
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

    @BindView(R.id.loading)RelativeLayout mLoadingView;

    @BindView(R.id.login_button) LoginButton mFacebookButton;

    private CallbackManager callbackManager;

    private String mFacebookToken;
    private ProfileFacebook profile_fb;
    private ProfileTracker mProfileTracker;

    Utilities utilities = new Utilities();
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
        mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        mFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onLoadingOn();

                //saving the token
                mFacebookToken = loginResult.getAccessToken().getToken();

                updateUI();
            }
            @Override
            public void onCancel() {}
            @Override
            public void onError(FacebookException exception) {
                connect();
            }
        });

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
            }
        };

        mProfileTracker.startTracking();
    }

    private void connect(){
        utilities.toast(this,getString(R.string.connect_internet));
    }

    private void updateUI() {
        switch (pendingAction) {
            case NONE:
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    saveProfile(createProfile(profile));
                    pendingAction = PendingAction.SENDED;
                }
                break;
        };
    }

    @Override
    public void onProfileSaved(ProfileFacebook profile_fb) {
        this.profile_fb = profile_fb;
        mLoginPresenter.getAccessToken(mFacebookToken);
    }

    @Override
    public void displayToken(AccessToken accessToken) {
        Account account = createOrGetAccount(profile_fb.getFirst_name());
        storeToken(account, getString(R.string.authentication_TOKEN),  accessToken.getAccess_token(),  accessToken.getRefresh_token());
        storeUserData(account,getString(R.string.authentication_USER),"user");

        // finishes the activity and set this account to the "current-active" one
        startActivity(new Intent(this, UserPreferencesActivity.class));
        finalizeAuthentication(account);
    }

    @Override
    public void displayNetworkError() {
        onLoadingOff();
        facebookLogout();
        new Utilities().toast(this,getString(R.string.connect_internet));
    }

    @Override
    public void displayServerError() {
        onLoadingOff();
        facebookLogout();
        new Utilities().toast(this,getString(R.string.connect_internet));
    }

    private void saveProfile(ProfileFacebook profile){
        mLoginPresenter.saveProfile(profile);
    }

    private ProfileFacebook createProfile(Profile currentProfile){
        ProfileFacebook profileFacebook = new ProfileFacebook();

        profileFacebook.setId(Long.parseLong(currentProfile.getId()));
        profileFacebook.setFirst_name(currentProfile.getFirstName());
        profileFacebook.setLast_name(currentProfile.getLastName());

        return profileFacebook;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void facebookLogout(){
        //facebook logout
        LoginManager.getInstance().logOut();
    }

    private void onLoadingOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingOff(){
        mLoadingView.setVisibility(View.GONE);
    }
}

