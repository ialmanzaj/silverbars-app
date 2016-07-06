package com.app.project.silverbars;


import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.actionsheet.ActionSheet;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainScreenActivity extends AppCompatActivity {

    private ViewPager view;
    private SimpleTabAdapter adapter;
    private Button songs;
    private String email,name;
    private int id;
    private TextView emailView, nameView, Username, title;
    private ImageButton settings;
    private MySQLiteHelper database;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    public ImageView Sort;
    private CharSequence activityTitle;
    private CharSequence itemTitle;
    private String[] tagTitles;
    private Toolbar toolbar;
    private List<String> spinnerArray = new ArrayList<String>();
    private String muscle = "ALL";
    public Spinner spinner;
    private boolean Opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main_screen);

        tagTitles = getResources().getStringArray(R.array.navigation_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.bringToFront();
        Sort = (ImageView) findViewById(R.id.Sort);

        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(tagTitles[0],R.mipmap.home));
        items.add(new DrawerItem(tagTitles[1],R.mipmap.acrobatics));
        items.add(new DrawerItem(tagTitles[2],R.mipmap.profile));

        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Crear ActionBarDrawerToggle para la apertura y cierre
//        drawerToggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setTitle("Home");
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

        Sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainScreenActivity.this)
                        .title(R.string.filtertitle)
                        .items(R.array.filter_items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                if (which != -1){
                                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
                                    if (currentFragment instanceof MainFragment) {
                                        ((MainFragment) currentFragment).Task(text.toString());
                                        //place your filtering logic here using currentFragment
                                    }
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.choose)
                        .show();
            }
        });

    }

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
                    break;//place your filtering logic here using currentFragment
                }else{
//                    toast("Yes");
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
                    Sort.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Home");
                    Opened = main.isOpened();
                }

                break;
            case 1:
                if (currentFragment instanceof MainFragment) {
                    muscle = ((MainFragment) currentFragment).getMuscleData();
                    //place your filtering logic here using currentFragment
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, new WorkoutsFragment(), null)
                        .addToBackStack(null)
                        .commit();
                Sort.setVisibility(View.GONE);
                toolbar.setTitle("My Workouts");
                break;
            case 2:
                if (currentFragment instanceof MainFragment) {
                    muscle = ((MainFragment) currentFragment).getMuscleData();
                    //place your filtering logic here using currentFragment
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, new ProfileFragment(), null)
                        .addToBackStack(null)
                        .commit();
                Sort.setVisibility(View.GONE);
                toolbar.setTitle("Profile");
                break;
        }

        // Se actualiza el item seleccionado y el título, después de cerrar el drawer
        drawerList.setItemChecked(position, true);
        setTitle(tagTitles[position]);
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

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
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
}
