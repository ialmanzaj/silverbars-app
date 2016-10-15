package com.app.proj.silverbars;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.*;
import com.facebook.AccessToken;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.getFileReady;
import static com.app.proj.silverbars.Utilities.saveHtmInDevice;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN SCREEN ACTIVITY";
    private ViewPager view;

    private Button songs;
    private String email,name;
    private int id;
    private TextView emailView, nameView, Username, title;

    private MySQLiteHelper database;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    public LinearLayout Button_filter;
    private CharSequence activityTitle;
    private CharSequence itemTitle;
    private String[] tagTitles =  new String[3];;
    private Toolbar toolbar;
    private List<String> spinnerArray = new ArrayList<String>();
    private String muscle = "ALL";
    public Spinner spinner;
    private boolean Opened = false;
    private FloatingActionButton ButtonCreateWorkout;
    LinearLayout settings;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"Main Activity creada");
        setContentView(R.layout.activity_main_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());


        if (!Fresco.hasBeenInitialized()){
            Fresco.initialize(this);
        }





        settings = (LinearLayout) findViewById(R.id.settings);
        tagTitles = this.getResources().getStringArray(R.array.navigation_array);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24px);
        toolbar.setTitle(tagTitles[0]);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(drawerList)){
                    drawerLayout.closeDrawer(drawerList);
                }
                else
                    drawerLayout.openDrawer(drawerList);
            }
        });



        ButtonCreateWorkout = (FloatingActionButton) findViewById(R.id.fab);
        ButtonCreateWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CreateWorkoutActivity.class);
                startActivityForResult(i,1);
            }
        });

        Button_filter = (LinearLayout) toolbar.findViewById(R.id.Sort);

        final ArrayList<DrawerItem> items_drawer = new ArrayList<DrawerItem>();
        items_drawer.add(new DrawerItem(tagTitles[0],R.drawable.ic_home_black_24px));
        items_drawer.add(new DrawerItem(tagTitles[1],R.drawable.ic_apps_black_24dp));
        items_drawer.add(new DrawerItem(tagTitles[2],R.drawable.ic_person_outline_black_24px));



        drawerList.setAdapter(new DrawerListAdapter(this, items_drawer));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
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
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

                                if (which != -1) {
                                    Log.v(TAG, String.valueOf(which));
                                    String[] Items = getResources().getStringArray(R.array.filter_items);

                                    Log.v(TAG, Items[which]);
                                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
                                    if (currentFragment instanceof MainFragment) {
                                        ((MainFragment) currentFragment).getWorkoutsData(Items[which]);


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


        File Dir = getFileReady(this,"/html/");
        if (Dir.isDirectory()){
            Log.v(TAG,"EXISTE DIR"+Dir.getPath());
            File file = getFileReady(this,"/html/"+"index.html");
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


    }//oncreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* La escucha del ListView en el Drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public String getMuscle() {
        return muscle;
    }

    private void selectItem(int position) {
        // Reemplazar el contenido del layout principal por un fragmento
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);

        switch (position){
            case 0:
                if (currentFragment instanceof MainFragment) {
                    break;
                }else{
                    MainFragment main = new MainFragment();
                    Bundle bundle = new Bundle();

                    bundle.putString("Muscle",muscle);
                    bundle.putBoolean("Opened",Opened);

                    main.setArguments(bundle);
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, main, null)
                            .addToBackStack(null)
                            .commit();
                    Button_filter.setVisibility(View.VISIBLE);
                    ButtonCreateWorkout.setVisibility(View.VISIBLE);
                    settings.setVisibility(View.GONE);
                    Opened = main.isOpened();
                }
                break;

            case 1:// MY WORKOUTS TAB
                if (currentFragment instanceof MainFragment) {
                    muscle = ((MainFragment) currentFragment).getMuscleData();
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, new MyWorkoutsFragment(), null)
                        .addToBackStack(null)
                        .commit();
                Button_filter.setVisibility(View.GONE);
                ButtonCreateWorkout.setVisibility(View.GONE);
                settings.setVisibility(View.GONE);
                break;

            case 2:// PROFILE TAB
                if (currentFragment instanceof MainFragment) {
                    muscle = ((MainFragment) currentFragment).getMuscleData();
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, new ProfileFragment(), null)
                        .addToBackStack(null)
                        .commit();

                Button_filter.setVisibility(View.GONE);
                ButtonCreateWorkout.setVisibility(View.GONE);
                settings.setVisibility(View.VISIBLE);
                break;
        }
        // Se actualiza el item seleccionado y el título, después de cerrar el drawer
        drawerList.setItemChecked(position, true);
        toolbar.setTitle(tagTitles[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    /* Método auxiliar para setear el titulo de la action bar */
    @Override
    public void setTitle(CharSequence title) {
        itemTitle = title;
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    private void MuscleTemplateDownload(){
        Log.v(TAG,"MuscleTemplateDownload");
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/")
                .build();

        final SilverbarsService downloadhtmlService = retrofit.create(SilverbarsService.class);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<ResponseBody> call = downloadhtmlService.getMusclesTemplate();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Boolean write = saveHtmInDevice(MainActivity.this,response.body(),"index.html");
                        }
                        else {Log.e(TAG, "Download server contact failed");}
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Download server contact failed",t);
                    }
                });
                return null;
            };
        }.execute();
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


}
