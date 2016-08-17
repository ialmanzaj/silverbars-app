package com.app.proj.silverbars;

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
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private RelativeLayout close_session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Opciones");
            toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

        close_session = (RelativeLayout) findViewById(R.id.close_session);
        close_session.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                View v = new MaterialDialog.Builder(view.getContext())
                        .title(getResources().getString(R.string.session_title))
                        .content(getResources().getString(R.string.session_content))
                        .positiveText(getResources().getString(R.string.positive_dialog)).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                LoginManager.getInstance().logOut();
                                saveLogOut();
                                finish();
                                MainScreenActivity.MainScreenActivity.finish();
                                startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
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
