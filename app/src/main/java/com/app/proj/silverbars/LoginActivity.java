package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    String basicMail, basicPass, email, name;
    ImageButton RefreshButton;
    View login_button;
    private MySQLiteHelper database;
    boolean checkUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"LoginActivity creada");

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mProgressView = findViewById(R.id.login_progress);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.app.proj.silverbars",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {} catch (NoSuchAlgorithmException e) {}

        if (isNetworkConnected()){

            loginButton.setReadPermissions("public_profile", "email", "user_friends");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v(TAG, "loginButton: onSuccess");

                    post(loginResult.getAccessToken().getToken());

                }
                @Override
                public void onCancel() {
                    Log.e(TAG, "facebook:onCancel");

                }
                @Override
                public void onError(FacebookException exception) {
                    Log.e(TAG, "facebookonError", exception);
                }
            });
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
    }



    private  void post(final String token) {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                RequestBody formBody = new FormBody.Builder()
                        .add("grant_type", "convert_token")
                        .add("client_id", "KHeJV3Sg8ShguiYyvDf9t6i3WPpMpDWlBLN93mgz")
                        .add("client_secret", "1krO5gdrzs08Ej5WoGpLrQifbuDRNFxEnRqLKyHFJIFG2fPpGPE3t1J8nCS7K9NoSidUCibUUi985ipRiipjM0YV6PoUDMcXw08A4M8R7yfzECFGDHnxVBYgQfgjfc2e")
                        .add("backend", "facebook")
                        .add("token", token)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.silverbarsapp.com/")
                        .post(formBody)
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v(TAG,response.toString());
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.silverbarsapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        SilverbarsService service = retrofit.create(SilverbarsService.class);
        retrofit2.Call<AccessToken> call = service.getAccessToken();

        call.enqueue(new retrofit2.Callback<AccessToken>() {
            @Override
            public void onResponse(retrofit2.Call<AccessToken> call, retrofit2.Response<AccessToken> response) {

                if (response.isSuccessful()) {

                    AccessToken accessToken = response.body();

                    AuthPreferences authPreferences = new AuthPreferences(LoginActivity.this);
                    authPreferences.setToken(accessToken.getAccess_token());


                    saveLogIn();
                    finish();
                    startActivity(new Intent(LoginActivity.this,MainScreenActivity.class));
                }else {

                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG,errorBody.toString());
                    Log.e(TAG,"statusCode:"+statusCode);



                }
            }
            @Override
            public void onFailure(retrofit2.Call<AccessToken> call, Throwable t) {
                Log.e(TAG,"getAccesstoken from server, onFailure",t);
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

