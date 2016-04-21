package com.example.project.calisthenic;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

public class MainScreen extends AppCompatActivity {

    ViewPager view;
    SimpleTabAdapter adapter;
    Button songs;
    private String email,name;
    TextView emailView, nameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        songs = (Button) findViewById(R.id.songs);
        Intent intent = this.getIntent();
        email = intent.getStringExtra("Email");
        name = intent.getStringExtra("Name");

        emailView = (TextView) findViewById(R.id.email);
        nameView = (TextView) findViewById(R.id.name);

        emailView.setText(email);
        nameView.setText(name);

        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkoutActivity();
            }
        });


        int[] image = {R.mipmap.home, R.mipmap.search,
                R.mipmap.acrobatics, R.mipmap.user};
        int[] color = {ContextCompat.getColor(this, R.color.firstColor), ContextCompat.getColor(this, R.color.secondColor),
                ContextCompat.getColor(this, R.color.thirdColor), ContextCompat.getColor(this, R.color.fourthColor)};
        adapter = new SimpleTabAdapter(getSupportFragmentManager());
//        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
//        view = (ViewPager) findViewById(R.id.view);
//        view.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
                ("Home", getResources().getColor(R.color.firstColor), R.mipmap.home);
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
                ("Search", getResources().getColor(R.color.secondColor), R.mipmap.search);
        BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem
                ("Workouts", getResources().getColor(R.color.thirdColor), R.mipmap.acrobatics);
        BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem
                ("Profile", getResources().getColor(R.color.fourthColor), R.mipmap.user);



        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);
        bottomNavigationView.addTab(bottomNavigationItem2);
        bottomNavigationView.addTab(bottomNavigationItem3);

//        view.setAdapter(adapter);
//        bottomNavigationView.setViewPager(view , color , image);

    }

    public void WorkoutActivity(){
        Intent intent = new Intent(getApplicationContext(), Workout.class);
        startActivity(intent);
    }

}
