package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    AccessTokenTracker accessTokenTracker;
    MySQLiteHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
        setContentView(R.layout.activity_splash_screen);
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
                    Intent i = new Intent(SplashScreen.this, MainScreenActivity.class);
                    startActivity(i);

                    finish();
//                }
//            }, 3000);
        } else {
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);

                    finish();
//                }
//            }, 3000);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
