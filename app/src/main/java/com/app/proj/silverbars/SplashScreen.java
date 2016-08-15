package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    AccessTokenTracker accessTokenTracker;
    MySQLiteHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"SplashScreen creada");

        setContentView(R.layout.activity_splash_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };


        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                Log.v(TAG,"first name"+ newProfile.getFirstName());

            }
        };


    }



    private void updateWithToken(AccessToken currentAccessToken) {
        Log.v(TAG,"updateWithToken ()");



        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            Log.v(TAG,"user: "+currentAccessToken.getUserId());


            //main screen activity
            Log.v(TAG,"currentAccessToken: correcto");
            Intent i = new Intent(SplashScreen.this, MainScreenActivity.class);
            startActivity(i);
            finish();
        } else {
            Log.v(TAG,"currentAccessToken: incorrecto");
            Intent i = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        accessTokenTracker.stopTracking();

    }

}
