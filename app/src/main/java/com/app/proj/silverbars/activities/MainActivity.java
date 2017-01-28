package com.app.proj.silverbars.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.app.proj.silverbars.DrawerItem;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.adapters.DrawerListAdapter;
import com.app.proj.silverbars.fragments.MainWorkoutsFragment;
import com.app.proj.silverbars.fragments.MyWorkoutsFragment;
import com.app.proj.silverbars.fragments.ProfileFragment;
import com.app.proj.silverbars.utils.Utilities;
import com.facebook.FacebookSdk;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


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

        //Log.v(TAG,"Main Activity creada");

        //facebook init
        FacebookSdk.sdkInitialize(getApplicationContext());



        mTitle = mDrawerTitle = getTitle();



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24px);


        mMenuTitles = getResources().getStringArray(R.array.navigation_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        ArrayList<DrawerItem> items_drawer = new ArrayList<>();
        items_drawer.add(new DrawerItem(mMenuTitles[0],R.drawable.ic_home_black_24px));
        items_drawer.add(new DrawerItem(mMenuTitles[1],R.drawable.ic_apps_black_24dp));
        items_drawer.add(new DrawerItem(mMenuTitles[2],R.drawable.ic_person_outline_black_24px));


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerList.setAdapter(new DrawerListAdapter(this, items_drawer));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



        mButtonCreateWorkout = (FloatingActionButton) findViewById(R.id.fab);


        mButtonCreateWorkout.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,CreateWorkoutActivity.class);
            startActivityForResult(i,1);
        });

        Button_filter = (LinearLayout) toolbar.findViewById(R.id.Sort);




        settings = (LinearLayout) findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


        if (savedInstanceState == null) {
            selectItem(0);
        }



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
    }//oncreate


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        // Reemplazar el contenido del layout principal por un fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);

        switch (position){
            case 0:
                if (currentFragment instanceof MainWorkoutsFragment) {
                    break;
                }else{

                    MainWorkoutsFragment mainWorkoutsFragment = new MainWorkoutsFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("Muscle",muscle);
                    mainWorkoutsFragment.setArguments(bundle);

                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, mainWorkoutsFragment)
                            .commit();

                    Button_filter.setVisibility(View.VISIBLE);
                    mButtonCreateWorkout.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.GONE);
                }
                break;
            case 1:// MY WORKOUTS
                if (currentFragment instanceof MyWorkoutsFragment) {
                    break;
                }else {
                    
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, new MyWorkoutsFragment())
                            .commit();

                    Button_filter.setVisibility(View.GONE);
                    mButtonCreateWorkout.setVisibility(View.GONE);
                    settings.setVisibility(View.GONE);
                }
                break;
            
            case 2:
                // PROFILE 
                if (currentFragment instanceof ProfileFragment) {
                   break;
                }else {

                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, new ProfileFragment())
                            .commit();

                    Button_filter.setVisibility(View.GONE);
                    mButtonCreateWorkout.setVisibility(View.GONE);
                    settings.setVisibility(View.VISIBLE);
                }
                break;
        }


        // Se actualiza el item seleccionado y el título, después de cerrar el drawer
        mDrawerList.setItemChecked(position, true);
        toolbar.setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the ap icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed(){
        finish();
    }

    public String getMuscle() {
        return muscle;
    }
    


}
