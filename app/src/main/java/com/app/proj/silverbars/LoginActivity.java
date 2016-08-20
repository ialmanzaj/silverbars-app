package com.app.proj.silverbars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static final String TAG = "LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
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


        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v(TAG, "loginButton: onSuccess");

                //Log.v(TAG,"access token"+loginResult.getAccessToken().getToken());

                POST(loginResult.getAccessToken().getToken());

                saveLogIn();
                startActivity(new Intent(getApplicationContext(), MainScreenActivity.class));
                finish();

            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");

            }
            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "facebook:onError", exception);

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
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void checkConn() {

        boolean connection = isNetworkConnected();

        if (!connection){
//            loginButton.setEnabled(false);
//            loginButton.setClickable(false);
            toast("Verifique su conexion a internet");
        }else{
//            loginButton.setEnabled(true);
//            loginButton.setClickable(true);
        }
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

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

    private void POST(final String accessToken){
        final Boolean respuesta = false;
        Log.v(TAG,"envio POST");
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Log.v(TAG,"ha enviado el post");

                    URL url = new URL("http://api.silverbarsapp.com/rest-auth/facebook/"); //Enter URL here
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                    httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                    httpURLConnection.connect();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("access_token",accessToken);
                    jsonObject.put("code", "183093092104411");

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();

                    int responseCode = httpURLConnection.getResponseCode();

                    Log.v(TAG,"Sending 'POST' request to URL : " + url);
                    Log.v(TAG,"Response Code : " + responseCode);

                    if (responseCode == 200){

                        Log.v(TAG,"respuesta correcta ");

                    }else{
                        Log.e(TAG,"respuesta incorrecta ");
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line = "";
                    StringBuilder responseOutput = new StringBuilder();
                    System.out.println("output===============" + br);
                    while((line = br.readLine()) != null ) {
                        responseOutput.append(line);
                    }
                    br.close();


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }


        }.execute();


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"Login activity onDestroy");
    }
}

