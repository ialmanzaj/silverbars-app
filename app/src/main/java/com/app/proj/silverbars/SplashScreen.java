package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FacebookSdk.sdkInitialize(getApplicationContext());

        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        Boolean signIn = sharedPref.getBoolean(getString(R.string.sign_in), false);


        if (signIn){

            startMainActivity();
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {

                }
            };

        }else {
            startLogin();
        }

    }



    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void startLogin(){
        Log.v(TAG,"startLogin");
        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void startMainActivity(){
        Log.v(TAG,"startMainActivity");
        Intent i = new Intent(SplashScreen.this, MainScreenActivity.class);
        startActivity(i);
        finish();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG," onStart()");

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume()");

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause()");


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"splash screen destruida");


    }

}
