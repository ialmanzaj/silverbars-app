package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static final String TAG = "LoginActivity";
    
    private static final String CONSUMER_KEY = "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz";
    private static final String CONSUMER_SECRET = "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button login;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    String basicMail, basicPass, email, name;
    ImageButton RefreshButton;
    View login_button;
    private MySQLiteHelper database;
    boolean checkUser = false;
    ImageView logo;
    TextView slogan;

    RelativeLayout container;
    TextView slogan_login;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"LoginActivity creada");

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mProgressView = findViewById(R.id.login_progress);
        login = (Button) findViewById(R.id.login_button);

        logo = (ImageView) findViewById(R.id.logo);
        slogan = (TextView) findViewById(R.id.slogan);

        slogan_login = (TextView) findViewById(R.id.slogan_login);
        container = (RelativeLayout) findViewById(R.id.container);




        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.app.proj.silverbars",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {}


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG,"onclick");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        container.setBackgroundColor(getResources().getColor(R.color.black,getTheme()));
                    }else {
                        container.setBackgroundColor(getResources().getColor(R.color.black));
                    }


                    slogan_login.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);

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

                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));


                }
            });




    }


    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
    }



    private void FacebookLogin(String facebook_token) {


        logo.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);



        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        final AuthPreferences authPreferences = new AuthPreferences(this);


        Call<AccessToken> call = loginService.getAccessToken("convert_token",CONSUMER_KEY,CONSUMER_SECRET,"facebook",facebook_token);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();

                    String token = gson.toJson(response.body(),AccessToken.class);
                    authPreferences.setAccessToken(token);

                    saveLogIn();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    Log.e(TAG, "statusCode:" + response.code());
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "initial token, onFailure", t);
            }
        });

    }

    private void saveLogIn(){
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sign_in),true);
        editor.apply();
        Log.v(TAG,getString(R.string.sign_in));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"Login activity onDestroy");
    }
}

