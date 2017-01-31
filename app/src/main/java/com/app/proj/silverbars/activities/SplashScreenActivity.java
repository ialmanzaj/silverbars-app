package com.app.proj.silverbars.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.andretietz.retroauth.AuthAccountManager;
import com.app.proj.silverbars.R;


public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AuthAccountManager authAccountManager = new AuthAccountManager();
        Account activeAccount = authAccountManager.getActiveAccount(getString(R.string.authentication_ACCOUNT));


        startMainActivity();
       /*
        if (activeAccount != null){


            
        } else {
            startLogin();
        }*/
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



}
