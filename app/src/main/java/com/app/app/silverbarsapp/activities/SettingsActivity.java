package com.app.app.silverbarsapp.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andretietz.retroauth.AuthAccountManager;
import com.app.app.silverbarsapp.R;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        ButterKnife.bind(this);


        setupToolbar();

        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);

        close_session.setOnClickListener(this::dialogLogOut);
    }

    private void dialogLogOut(View view){
        new MaterialDialog.Builder(view.getContext())
                .title(getResources().getString(R.string.session_title))
                .content(getResources().getString(R.string.session_content))
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveText(getResources().getString(R.string.positive_dialog)).onPositive((dialog, which) -> {
                    dialog.dismiss();
                    logout();
                })
                .negativeText(getResources().getString(R.string.negative_dialog))
                .onNegative((dialog, which) -> dialog.dismiss()
                ).show().getContentView();
    }

    private void logout(){
        //facebook logout
        LoginManager.getInstance().logOut();

        //remove account
        AuthAccountManager authAccountManager = new AuthAccountManager();
        authAccountManager.removeActiveAccount(getString(R.string.authentication_ACCOUNT));

        //return to login activity
        loginActivity();
    }

    private void loginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authentication_ACCOUNT));
        startActivity(intent);
        finish();
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_settings_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
