package com.app.proj.silverbars.activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andretietz.retroauth.AuthAccountManager;
import com.app.proj.silverbars.R;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.close_session) RelativeLayout close_session;
    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        FacebookSdk.sdkInitialize(getApplicationContext());


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Opciones");


        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
                }
        });


        close_session.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                View v = new MaterialDialog.Builder(view.getContext())
                        .title(getResources().getString(R.string.session_title))
                        .content(getResources().getString(R.string.session_content))

                        .contentColor(getResources().getColor(R.color.white))
                        .positiveColor(getResources().getColor(R.color.white))
                        .negativeColor(getResources().getColor(R.color.white))
                        .positiveText(getResources().getString(R.string.positive_dialog)).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                                //facebook logout
                                LoginManager.getInstance().logOut();


                                AuthAccountManager authAccountManager = new AuthAccountManager();
                                authAccountManager.removeActiveAccount(getString(R.string.authentication_ACCOUNT));


                                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authentication_ACCOUNT));
                                startActivity(intent);
                                finish();
                            }
                        })
                        .negativeText(getResources().getString(R.string.negative_dialog)).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show().getContentView();

                }
            });




    }


    private void saveLogOut(){
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sign_in),false);
        editor.commit();
        Log.v(TAG,getString(R.string.sign_in));

    }
}
