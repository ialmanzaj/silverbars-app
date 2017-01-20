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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andretietz.retroauth.AuthenticationActivity;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.AccessToken;
import com.app.proj.silverbars.viewsets.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.app.proj.silverbars.Constants.PACKAGE;


public class LoginActivity extends AuthenticationActivity implements LoginView{

    private static final String TAG = LoginActivity.class.getSimpleName();


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private View mProgressView;
    private View mLoginFormView;

    private Button mLoginButton;
    CallbackManager callbackManager;

    ProfileTracker profileTracker;
    String basicMail, basicPass, email, name;

    ImageButton RefreshButton;
    View login_button;


    boolean checkUser = false;
    ImageView mLogo;
    TextView slogan;

    RelativeLayout container;
    TextView slogan_login;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v(TAG,"LoginActivity creada");

        FacebookSdk.sdkInitialize(getApplicationContext());



        mProgressView = findViewById(R.id.login_progress);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLogo = (ImageView) findViewById(R.id.logo);
        slogan = (TextView) findViewById(R.id.slogan);

        slogan_login = (TextView) findViewById(R.id.slogan_login);
        container = (RelativeLayout) findViewById(R.id.container);


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



        mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG,"onclick");


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

                                Log.v(TAG, "loginButton: onSuccess");

                                FacebookLogin(loginResult.getAccessToken().getToken());

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

                        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));



                    }else
                        Toast.makeText(LoginActivity.this, "Please, Connect to internet", Toast.LENGTH_LONG).show();
                }
            });

    }

    private void FacebookLogin(String facebook_token) {
        mLogo.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);



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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

