package com.app.proj.silverbars;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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

    private Utilities utilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"Main Activity creada");
        setContentView(R.layout.activity_main_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());

        mTitle = mDrawerTitle = getTitle();

        utilities = new Utilities();


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



        mButtonCreateWorkout = (FloatingActionButton) findViewById(R.id.fab);
        mButtonCreateWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CreateWorkoutActivity.class);
                startActivityForResult(i,1);
            }
        });

        Button_filter = (LinearLayout) toolbar.findViewById(R.id.Sort);


        mDrawerList.setAdapter(new DrawerListAdapter(this, items_drawer));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        settings = (LinearLayout) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        if (savedInstanceState == null) {
            selectItem(0);
        }

        Button_filter.setClickable(true);
        Button_filter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.filtertitle)
                        .items(R.array.filter_items_text)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int indice, CharSequence text) {
                                FragmentManager fragmentManager = getSupportFragmentManager();

                                if (indice != -1) {
                                    Log.v(TAG, String.valueOf(indice));
                                    String[] Muscles = getResources().getStringArray(R.array.filter_items);

                                    Log.v(TAG, Muscles[indice]);
                                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
                                    if (currentFragment instanceof MainFragment) {
                                        ((MainFragment) currentFragment).filterWorkouts(Muscles[indice]);
                                    }
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.choose)
                        .positiveColor(getResources().getColor(R.color.white))
                        .show();
            }
        });



        saveBodyTemplate();
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
                if (currentFragment instanceof MainFragment) {
                    break;
                }else{

                    MainFragment mainFragment = new MainFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("Muscle",muscle);
                    mainFragment.setArguments(bundle);

                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, mainFragment)
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


    private void MuscleTemplateDownload(){

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/")
                .build();

        MainService mDownloadService = retrofit.create(MainService.class);
        
        mDownloadService.downloadFile("silverbarsmedias3/html/index.html").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Boolean write = utilities.saveHtmInDevice(MainActivity.this,response.body(),"index.html");
                }
                else {Log.e(TAG, "Download server contact failed");}
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Download server contact failed",t);}
        });


    }

    private void saveBodyTemplate(){
        File Dir = utilities.getFileReady(this,"/html/");
        if (Dir.isDirectory()){
            Log.v(TAG,"EXISTE DIR"+Dir.getPath());
            File file = utilities.getFileReady(this,"/html/"+"index.html");
            if (!file.exists()){
                MuscleTemplateDownload();
                setMusclePath(file);
            }else
                setMusclePath(file);
        }else {
            boolean success = Dir.mkdir();
            if (success)
                MuscleTemplateDownload();
            else
                Log.e(TAG,"Error creating dir");
        }

    }

    public String getMuscle() {
        return muscle;
    }
    
    private void setMusclePath(File file){
        Log.v(TAG,"setMusclePath: "+file.getPath());
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.muscle_path), file.getPath());
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG,"onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
    }
}
