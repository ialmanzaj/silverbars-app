package com.example.project.calisthenic;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.actionsheet.ActionSheet;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;

import java.util.Set;

public class MainScreen extends AppCompatActivity implements ActionSheet.ActionSheetListener {

    ViewPager view;
    SimpleTabAdapter adapter;
    Button songs;
    String email,name;
    int id;
    TextView emailView, nameView, Username;
    ImageButton settings;
    MySQLiteHelper database;
    String[] results = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setContentView(R.layout.activity_main_screen);

        settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Setting_popup();
            }
        });
        Username = (TextView) findViewById(R.id.Username);
        Username.setText("Welcome 'name' ");

        int[] image = {R.mipmap.home, R.mipmap.acrobatics,
                R.mipmap.progress, R.mipmap.ic_stars_black_24dp, R.mipmap.profile};
        int[] color = {ContextCompat.getColor(this, R.color.BarColor), ContextCompat.getColor(this, R.color.BarColor),
                ContextCompat.getColor(this, R.color.BarColor), ContextCompat.getColor(this, R.color.BarColor), ContextCompat.getColor(this, R.color.BarColor)};
        adapter = new SimpleTabAdapter(getSupportFragmentManager());

        view = (ViewPager) findViewById(R.id.view);
        view.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.isColoredBackground(false);
        bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        view.setAdapter(adapter);
        bottomNavigationView.setViewPager(view , color , image);
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index){
            case 0:
                break;
            case 1:
                LoginManager.getInstance().logOut();
//                database.updateUser(email,0);
                finish();
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
        Intent intent = new Intent(getApplicationContext(), Workout.class);
        startActivity(intent);
    }

    public void Setting_popup(){
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("About", "Log out")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

}
