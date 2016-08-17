package com.app.proj.silverbars;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.facebook.FacebookSdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.isExternalStorageWritable;

//import com.afollestad.materialdialogs.MaterialDialog;

public class MainScreenActivity extends AppCompatActivity {

    private static final String TAG = "MAIN SCREEN ACTIVITY";
    private ViewPager view;
    private SimpleTabAdapter adapter;
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
    private FloatingActionButton fab_create_new_workout;
    LinearLayout settings;

    public static Activity MainScreenActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"Main screenActivity creada");
        FacebookSdk.sdkInitialize(getApplicationContext());

        MainScreenActivity = this;


        setContentView(R.layout.activity_main_screen);

        settings = (LinearLayout) findViewById(R.id.settings);

        tagTitles = this.getResources().getStringArray(R.array.navigation_array);

        Log.v(TAG,tagTitles[0]);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        
        //guardar html de svg en el celular

        if (isExternalStorageWritable()){
            File Dir = new File(Environment.getExternalStorageDirectory()+"/html/");
            if (Dir.isDirectory()){
                File file = new File(Environment.getExternalStorageDirectory()+"/html/"+"index.html");
                setMusclePath(file);
                if (!file.exists()){
                    MuscleTemplateDownload();
                }
            }else {
                boolean success = Dir.mkdir();
                if (success)
                    MuscleTemplateDownload();

                else
                    Log.e(TAG,"Error creating dir");
            }
        }else {

            File Dir = new File(this.getFilesDir()+"/html/");
            if (Dir.isDirectory()){
                File file = new File(this.getFilesDir()+"/html/"+"index.html");
                setMusclePath(file);
                if (!file.exists()){
                    MuscleTemplateDownload();
                }
            }else {
                boolean success = Dir.mkdir();
                if (success)
                    MuscleTemplateDownload();
                else
                    Log.e(TAG,"Error creating dir");
            }
        }


        fab_create_new_workout = (FloatingActionButton) findViewById(R.id.fab);
        
        if (fab_create_new_workout != null) {
            fab_create_new_workout.bringToFront();
        }

        fab_create_new_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainScreenActivity.this,CreateWorkoutActivity.class);
                startActivityForResult(i,1);
            }
        });

        Button_filter = (LinearLayout) toolbar.findViewById(R.id.Sort);

        final ArrayList<DrawerItem> items_drawer = new ArrayList<DrawerItem>();
        items_drawer.add(new DrawerItem(tagTitles[0],R.drawable.ic_home_black_24dp));
        items_drawer.add(new DrawerItem(tagTitles[1],R.drawable.ic_apps_black_24dp));
        items_drawer.add(new DrawerItem(tagTitles[2],R.drawable.ic_person_outline_black_24dp));

        drawerList.setAdapter(new DrawerListAdapter(this, items_drawer));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreenActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
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


        if (savedInstanceState == null) {
            selectItem(0);
        }

        Button_filter.setClickable(true);
        Button_filter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainScreenActivity.this)
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
                        .show();
            }
        });

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
            case 0:// ALL WORKOUTS TAB
                if (currentFragment instanceof MainFragment) {
                    break;//place your filtering logic here using currentFragment
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
                    fab_create_new_workout.setVisibility(View.VISIBLE);
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
                        .replace(R.id.content_frame, new WorkoutsFragment(), null)
                        .addToBackStack(null)
                        .commit();
                Button_filter.setVisibility(View.GONE);
                fab_create_new_workout.setVisibility(View.GONE);
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
                fab_create_new_workout.setVisibility(View.GONE);
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
//        getSupportActionBar().setTitle(itemTitle);
    }
    public void WorkoutActivity(){
        Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed(){
        finish();
    }

    public void fragmentLoaded(){
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
        if (currentFragment instanceof MainFragment) {
            muscle = ((MainFragment) currentFragment).getMuscleData();
            //place your filtering logic here using currentFragment
        }
    }



    public void MuscleTemplateDownload(){
        //Log.v(TAG, "METODO MUSCLE TEMPLATE LLAMADO");
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
                            Boolean write = writeHmlToDisk(response.body(),"index.html");
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




    public boolean writeHmlToDisk(ResponseBody body, String name) {
        Log.v(TAG,"writeMuscleHmlToDisk");
        try {
            File file;
            if (isExternalStorageWritable()){

                file = new File(Environment.getExternalStorageDirectory()+"/html/"+name);
            }else {
                file = new File(this.getFilesDir()+"/html/"+name);
            }

            InputStream input = null;
            OutputStream output = null;

            try {
                input = body.byteStream();
                output = new FileOutputStream(file);

                int size = input.available();
                //Log.d("size ", String.valueOf(size));

                byte[] buffer = new byte[size];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                while (true) {
                    int read = input.read(buffer);
                    if (read == -1) {break;}
                    output.write(buffer, 0, read);
                    fileSizeDownloaded += read;
                    //Log.d("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                output.flush();
                return true;

            } catch (IOException e) {
                Log.e(TAG,"IOEXEPTION",e);
                return false;
            } finally {
                if (input != null) {input.close();}
                if (output != null) {output.close();}
            }
        } catch (IOException e) { return false;}
    }

    private void setMusclePath(File file){

        Log.v(TAG,"setMusclePath: "+file.getPath());
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.muscle_path), file.getPath());
        editor.commit();

    }




}
