package com.app.proj.silverbars.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.fragments.MainWorkoutsFragment;
import com.app.proj.silverbars.fragments.MyWorkoutsFragment;
import com.app.proj.silverbars.fragments.ProfileFragment;
import com.app.proj.silverbars.utils.Utilities;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    public LinearLayout Button_filter;

    private CharSequence itemTitle;

    private String[] mMenuTitles =  new String[3];;
    private Toolbar toolbar;
    private String muscle = "ALL";
    public Spinner spinner;

    private FloatingActionButton mButtonCreateWorkout;
    private LinearLayout settings;

    private Utilities  utilities = new Utilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);





       /* Button_filter.setClickable(true);
        Button_filter.setOnClickListener(v ->

                new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.filtertitle)
                .items(R.array.filter_items_text)
                .itemsCallbackSingleChoice(-1, (dialog, view, indice, text) -> {
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    if (indice != -1) {

                        //Log.v(TAG, String.valueOf(indice));
                        String[] Muscles = getResources().getStringArray(R.array.filter_items);

                        //Log.v(TAG, Muscles[indice]);
                        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);


                       *//* if (currentFragment instanceof MainWorkoutsFragment) {
                            ((MainWorkoutsFragment) currentFragment).filterWorkouts(Muscles[indice]);
                        }*//*


                    }
                    return true;
                })
                .positiveText(R.string.choose)
                .positiveColor(getResources().getColor(R.color.white))
                .show());
*/


        //saveBodyTemplate();
    }


    public String getMuscle() {
        return muscle;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        // Reemplazar el contenido del layout principal por un fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = null;

        Bundle extras = new Bundle();

        switch (item.getItemId()) {
            case R.id.home:
                currentFragment = new MainWorkoutsFragment();
                extras.putString("Muscle",muscle);
                currentFragment.setArguments(extras);
                break;
            case R.id.workout:
                currentFragment = new MyWorkoutsFragment();
                break;
            case R.id.profile:
                currentFragment = new ProfileFragment();
                break;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, currentFragment);
        transaction.commit();


        return true;
    }


}
