package com.app.proj.silverbarsapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.app.proj.silverbarsapp.R;

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


        setupToolbar();


        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);


        close_session.setOnClickListener(view -> {
/*
            View v = new MaterialDialog.Builder(view.getContext())
                    .title(getResources().getString(R.string.session_title))
                    .content(getResources().getString(R.string.session_content))

                    .contentColor(getResources().getColor(R.color.white))
                    .positiveColor(getResources().getColor(R.color.white))
                    .negativeColor(getResources().getColor(R.color.white))
                    .positiveText(getResources().getString(R.string.positive_dialog)).onPositive((dialog, which) -> {
                        dialog.dismiss();

                        //facebook logout
                        LoginManager.getInstance().logOut();


                        AuthAccountManager authAccountManager = new AuthAccountManager();
                        authAccountManager.removeActiveAccount(getString(R.string.authentication_ACCOUNT));


                        Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authentication_ACCOUNT));
                        startActivity(intent);
                        finish();
                    })
                    .negativeText(getResources().getString(R.string.negative_dialog)).onNegative((dialog, which) -> dialog.dismiss())
                    .show().getContentView();*/

            });

    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Opciones");

    }

    private void saveLogOut(){
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sign_in),false);
        editor.apply();
        Log.v(TAG,getString(R.string.sign_in));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
