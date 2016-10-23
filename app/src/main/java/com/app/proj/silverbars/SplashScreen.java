package com.app.proj.silverbars;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.andretietz.retroauth.AuthAccountManager;
import com.andretietz.retroauth.AuthenticationActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        //Boolean signIn = sharedPref.getBoolean(getString(R.string.sign_in), false);


        AuthAccountManager authAccountManager = new AuthAccountManager();
        Account activeAccount = authAccountManager.getActiveAccount(getString(R.string.authentication_ACCOUNT));

        if (activeAccount != null){

            startMainActivity();
            
        } else {

            startLogin();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void startLogin(){
        Log.v(TAG,"startLogin");
        Intent i = new Intent(this,LoginActivity.class);
        i.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authentication_ACCOUNT));
        startActivity(i);
        finish();
    }

    private void startMainActivity(){
        Log.v(TAG,"startMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG," onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"splash screen destruida");
    }


}
