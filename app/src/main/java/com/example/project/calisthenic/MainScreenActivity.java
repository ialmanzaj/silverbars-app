package com.example.project.calisthenic;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.actionsheet.ActionSheet;
import com.facebook.FacebookSdk;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;

import java.io.File;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainScreenActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener {

    private ViewPager view;
    private SimpleTabAdapter adapter;
    private Button songs;
    private String email,name;
    private int id;
    private TextView emailView, nameView, Username;
    private ImageButton settings;
    private MySQLiteHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main_screen);




        int[] image = {R.mipmap.home, R.mipmap.acrobatics, R.mipmap.profile};

        int[] color = {ContextCompat.getColor(this, R.color.BarColor), ContextCompat.getColor(this, R.color.BarColor),
                ContextCompat.getColor(this, R.color.BarColor)};


        adapter = new SimpleTabAdapter(getSupportFragmentManager());

        view = (ViewPager) findViewById(R.id.view_pager);
        view.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.isColoredBackground(false);

        bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
        view.setAdapter(adapter);
        bottomNavigationView.setViewPager(view , color , image);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baritems, menu);
        return true;
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index){
            case 0:
                break;
            case 1:
//                LoginManager.getInstance().logOut();
//                database.updateUser(email,0);
//                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancle) {
//        Toast.makeText(getApplicationContext(), "dismissed isCancle = " + isCancle, Toast.LENGTH_LONG).show();
    }

    public void WorkoutActivity(){
        Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
        startActivity(intent);
    }

    public void Setting_popup(){
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("About", "Log out")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        finish();
    }




}
