package com.app.app.silverbarsapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.andretietz.retroauth.AuthAccountManager;
import com.app.app.silverbarsapp.R;


public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AuthAccountManager mAuthAccountManager = new AuthAccountManager();
        Account activeAccount = mAuthAccountManager.getActiveAccount(getString(R.string.authentication_ACCOUNT));

        if (activeAccount != null){

            String active_user = AccountManager.get(this).getUserData(activeAccount, getString(R.string.authentication_USER));

            if (active_user.equals("user")){

                startActivity(new Intent(this, UserPreferencesActivity.class));

            }else {
                startMainActivity();
            }

        } else {
            startLogin();
        }
    }

    private void startLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authentication_ACCOUNT));
        startActivity(intent);
        finish();
    }

    private void startMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
