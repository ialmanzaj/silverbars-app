package com.app.proj.silverbars.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.adapters.ExerciseAdapter;
import com.app.proj.silverbars.components.DaggerWorkoutComponent;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.modules.WorkoutModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.WorkoutPresenter;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.WorkoutView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutActivity extends BaseActivity implements WorkoutView{

    private static final String TAG = WorkoutActivity.class.getSimpleName();


    // UI FIELDS
    private Button plusSets;
    private Button minusSets;
    private Button plusRest;
    private Button minusRest;
    private Button plusRestSets;
    private Button minusRestSets;


    @Inject
    WorkoutPresenter mWorkoutPresenter;


    @BindView(R.id.toolbar) Toolbar myToolbar;
    @BindView(R.id.tabHost2) TabHost mTabLayout;
    
    
    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout error_layout;
    @BindView(R.id.reload) Button button_error_reload;
    
    @BindView(R.id.column1) LinearLayout primary_ColumnMuscle;
    @BindView(R.id.column2) LinearLayout secundary_ColumnMuscle;
    

    @BindView(R.id.content_info)LinearLayout contentInfo;
    
    @BindView(R.id.sets) TextView Sets;
    @BindView(R.id.RestbySet) TextView RestbySet;
    @BindView(R.id.RestbyExercise) TextView RestbyExercise;
   

    @BindView(R.id.list) RecyclerView list;
    @BindView(R.id.muscles) ScrollView scrollView;
    @BindView(R.id.webview) WebView webview;

    @BindView(R.id.SelectMusic) RelativeLayout selectMusic;

    @BindView(R.id.enableLocal) SwitchCompat enableLocal;
    @BindView(R.id.vibration_per_rep) SwitchCompat vibration_per_rep;
    @BindView(R.id.vibration_per_set) SwitchCompat vibration_per_set;

    @BindView(R.id.voice_per_exercise)SwitchCompat voice_per_exercise;

    @BindView(R.id.start_button) Button startButton;



    private ExerciseAdapter adapter;

    private String[] Songs_names;
    private ArrayList<File> Songs_files;


    private boolean isTouched = false;
    private boolean loadLocal = false;

    private int workoutId = 0, workoutSets = 0;
    private String workoutName, workoutLevel, mainMuscle, workoutImgUrl;

    private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

    boolean isVibrationPerRepActive = false;
    boolean isVibrationPerSetActive = false;
    
    boolean isDownloadAudioExerciseActive = false;

    private String partes = "";

    private String mPlaylistSpotify,mSpotifyToken;

    private  List<String> MusclesArray = new ArrayList<>();
    private  List<String> TypeExercises = new ArrayList<>();

    private ArrayList<ExerciseRep> mExercises;


    private Utilities utilities = new Utilities();

    @Override
    protected int getLayout() {
        return R.layout.activity_workout;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mWorkoutPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();


        DaggerWorkoutComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .workoutModule(new WorkoutModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        setExtras(extras);


        setupToolbar();
        setupTabs();
        

        button_error_reload.setOnClickListener(v -> {
            
          /*  error_layout.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            putExercisesInAdapter(mExercises);*/
            
            
        });

        

        voice_per_exercise.setOnCheckedChangeListener((compoundButton, isChecked) -> {
         /*   
            isDownloadAudioExerciseActive = isChecked;

            if (isDownloadAudioExerciseActive){
                if (mExercises.size() > 0){
                    for (int a = 0;a<mExercises.size();a++){
                        utilities.createExerciseAudio(this, isDownloadAudioExerciseActive, mExercises.get(a).getExercise().getExercise_audio());
                    }

                }

            }*/
            
        });

        
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setNestedScrollingEnabled(false);
        list.setHasFixedSize(false);
       
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }

        // CREAR BASE DE DATOS



        //SWITCH save local workout BUTTON CONFIGURACIONES
        enableLocal.setEnabled(true);

        enableLocal.setOnTouchListener((view, motionEvent) -> {
            isTouched = true;
            return false;
        });

        enableLocal.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isTouched) {
                isTouched = false;

                if (isChecked && !loadLocal){

                }else{
                    logMessage("Switch off");
                }
            }
        });


        Sets.setText(String.valueOf(workoutSets));


        Sets.setOnClickListener(view -> {
           /* 
            View v = new MaterialDialog.Builder(view.getContext())
                    .title(R.string.set_edit)
                    .customView(R.layout.edit_set_setup, true)
                    .positiveText(R.string.done_text).onPositive((dialog, which) -> {
                        dialog.dismiss();
                        //On Dialog "Done" ClickListener
                        Sets.setText(Sets_dialog.getText());

                    })
                    .show()
                    .getCustomView();

            if (v != null) {
                
                Sets_dialog = (TextView) v.findViewById(R.id.Sets_dialog);
                Sets_dialog.setText(String.valueOf(Sets.getText()));

                plusSets = (Button) v.findViewById(R.id.plusSets);
                plusSets.setOnClickListener(view16 -> plusTempo(Sets_dialog,plusSets,minusSets));
                minusSets = (Button) v.findViewById(R.id.minusSets);
                minusSets.setOnClickListener(view15 -> minusTempo(Sets_dialog,minusSets,plusSets));
            }
            
            
            int setsValue = Integer.parseInt(Sets.getText().toString());
            
            
            
            if (setsValue <= 1){
                plusSets.setEnabled(true);
                plusSets.setClickable(true);
                minusSets.setEnabled(false);
                minusSets.setClickable(false);
            }else if(setsValue >=10){
                plusSets.setEnabled(false);
                plusSets.setClickable(false);
                minusSets.setEnabled(true);
                minusSets.setClickable(true);
            }
            
            
            */
            

        });


        RestbyExercise.setOnClickListener(view -> {
/*
            TextView Rest_by_exercise_dialog;
            
            
            View v = new MaterialDialog.Builder(view.getContext())
                    .title(R.string.rest_exercise_text)
                    .customView(R.layout.edit_rest_exercise_setup, true)
                    .positiveText(R.string.done_text).onPositive((dialog, which) -> {
                        dialog.dismiss();
                        //On Dialog "Done" ClickListener
                        RestbyExercise.setText(Rest_by_exercise_dialog.getText());
                    })
                    .show()
                    .getCustomView();

            if (v != null) {

                Rest_by_exercise_dialog = (TextView) v.findViewById(R.id.Rest_exercise);
                Rest_by_exercise_dialog.setText(String.valueOf(RestbyExercise.getText()));


                plusRest = (Button) v.findViewById(R.id.plusRest);
                plusRest.setOnClickListener(view14 -> plusTempo(Rest_by_exercise_dialog,plusRest,minusRest));
                minusRest = (Button) v.findViewById(R.id.minusRest);
                minusRest.setOnClickListener(view13 -> minusTempo(Rest_by_exercise_dialog,minusRest,plusRest));
            }
            */
            
        });



        RestbySet.setOnClickListener(view -> {

            TextView RestSets_dialog;
            TextView Sets_dialog;
            
            
           /* View v = new MaterialDialog.Builder(view.getContext())
                    .title(R.string.rest_sets_text)
                    .customView(R.layout.edit_rest_sets_setup, true)
                    .positiveText(R.string.done_text).onPositive((dialog, which) -> {
                        dialog.dismiss();
                        //On Dialog "Done" ClickListener
                        RestbySet.setText(RestSets_dialog.getText());
                    })
                    .show()
                    .getCustomView();

            if (v != null) {

                RestSets_dialog = (TextView) v.findViewById(R.id.RestSets_dialog);
                RestSets_dialog.setText(String.valueOf(RestbySet.getText()));

                plusRestSets = (Button) v.findViewById(R.id.plusRestSets);
                plusRestSets.setOnClickListener(view12 -> plusTempo(RestSets_dialog,plusRestSets,minusRestSets));
                minusRestSets = (Button) v.findViewById(R.id.minusRestSets);
                minusRestSets.setOnClickListener(view1 -> minusTempo(RestSets_dialog,minusRestSets,plusRestSets));
            }*/
            
            
            
        });



        // vibracion por repeticion y por set colocado cada switch
        vibration_per_rep.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isVibrationPerRepActive = isChecked;
        });


        vibration_per_set.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isVibrationPerSetActive = isChecked;
        });


        startButton.setOnClickListener(view -> LaunchWorkingOutActivity() );
    }


    private void setExtras(Bundle extras){
        workoutId = extras.getInt("workout_id",0);
        workoutName = extras.getString("name");
        workoutImgUrl = extras.getString("image");
        workoutSets = extras.getInt("sets", 1);
        workoutLevel = extras.getString("level");
        mainMuscle = extras.getString("main_muscle");
        mExercises =  getIntent().getParcelableArrayListExtra("exercises");
        Log.i(TAG,"exercises"+mExercises);
        boolean user_workout = extras.getBoolean("user_workout", false);

        setExercisesInAdapter(mExercises);
    }



    @OnClick(R.id.SelectMusic)
    public void selectMusic(){
        startActivity(new Intent(this,SelectionMusicActivity.class));
    }


    public void setupToolbar(){
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(workoutName);
    }


    private void setupTabs(){

        //Defining Tabs
        mTabLayout.setup();

        TabHost.TabSpec overview = mTabLayout.newTabSpec(getResources().getString(R.string.tab_overview));
        TabHost.TabSpec muscles = mTabLayout.newTabSpec(getResources().getString(R.string.tab_muscles));
        TabHost.TabSpec exercises = mTabLayout.newTabSpec(getResources().getString(R.string.tab_exercises));

        overview.setIndicator(getResources().getString(R.string.tab_overview));
        overview.setContent(R.id.overview);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        exercises.setIndicator(getResources().getString(R.string.tab_exercises));
        exercises.setContent(R.id.exercises);

        mTabLayout.addTab(overview);
        mTabLayout.addTab(exercises);
        mTabLayout.addTab(muscles);
    }



    @Override
    public void displayWorkoutfromDatabase(Workout workout) {


    }

    private void setExercisesInAdapter(ArrayList<ExerciseRep> exercises){

        for (ExerciseRep exerciseRep: exercises){

            exerciseRep.setTempo_positive(1);
            exerciseRep.setTempo_isometric(1);
            exerciseRep.setTempo_negative(1);


            //Collections.addAll(TypeExercises, new List<String>[]{exerciseRep.getExercise().getType_exercise()});



          /*  for (Muscle muscle:  exerciseRep.getExercise().getMuscles()){
                Collections.addAll(MusclesArray,muscle.getMuscleName());
            }*/



        }


        adapter = new ExerciseAdapter(this,exercises);
        list.setAdapter(adapter);
        //setMusclesToView(MusclesArray);
        //putTypesInWorkout(TypeExercises);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){

            if (data.hasExtra("playlist_spotify") && data.hasExtra("token")){

                mPlaylistSpotify = data.getStringExtra("playlist_spotify");
                mSpotifyToken =  data.getStringExtra("token");

            }else if (data.hasExtra("songs") && data.hasExtra("positions")){

                Songs_names = data.getStringArrayExtra("positions");
                Songs_files = (ArrayList<File>) data.getSerializableExtra("songs");
            }
        }
    }

    private void LaunchWorkingOutActivity() {

        int sets,restbyexercise,restbyset;

        sets = Integer.parseInt(Sets.getText().toString());
        restbyexercise = Integer.parseInt(utilities.removeLastChar(RestbyExercise.getText().toString()));
        restbyset = Integer.parseInt(utilities.removeLastChar(RestbySet.getText().toString()));


        Intent intent = new Intent(this, WorkingOutActivity.class);

        intent.putParcelableArrayListExtra("exercises", adapter.getExercises());
        intent.putExtra("Sets",sets);

        //rests
        intent.putExtra("RestByExercise",restbyexercise);
        intent.putExtra("RestBySet",restbyset);

        //vibrations option
        intent.putExtra("VibrationPerSet",isVibrationPerSetActive);
        intent.putExtra("VibrationPerRep",isVibrationPerRepActive);

        //exercise audio option
        intent.putExtra("play_exercise_audio",isDownloadAudioExerciseActive);

        //songs of the phone
        intent.putExtra("songs",Songs_names);
        intent.putExtra("songlist",Songs_files);

        //songs of spotify
        intent.putExtra("playlist_spotify",mPlaylistSpotify);
        intent.putExtra("token",mSpotifyToken);

        startActivity(intent);
    }


    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){

            List<String> musculos_oficial =  utilities.deleteCopiesofList(musculos);


            for (int a = 0;a<musculos_oficial.size();a++) {

                TextView MuscleTextView = new TextView(this);
                partes += "#"+ musculos_oficial.get(a) + ",";
                MuscleTextView.setText(musculos_oficial.get(a));
                MuscleTextView.setGravity(Gravity.CENTER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MuscleTextView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));
                }else {
                    MuscleTextView.setTextColor(getResources().getColor(R.color.gray_active_icon));
                }

                if (a%2 == 0){
                    secundary_ColumnMuscle.addView(MuscleTextView);
                }else {
                    primary_ColumnMuscle.addView(MuscleTextView);

                }




            }
        }

       
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                utilities.injectJS(partes, webview);
                super.onPageFinished(view, url);
            }
        });


        webview.getSettings().setJavaScriptEnabled(true);
        utilities.setBodyInWebwView(this,webview);
    }


    private void getCountTimes(List<String> list){
        for (int a = 0; a<list.size();a++) {
            if (list.get(a).equals("ISOMETRIC")) {
                ISOMETRIC = ISOMETRIC+1;
            }if (list.get(a).equals("CARDIO")){
                CARDIO=CARDIO+1;
            }if(list.get(a).equals("PYLOMETRICS")){
                PYLOMETRICS = PYLOMETRICS+1;
            }if (list.get(a).equals("STRENGTH")){
                STRENGTH = STRENGTH+1;
            }
        }
    }

    private void putTypesInWorkout(List<String> types){
        
        getCountTimes(types);

        List<String> typesExercise_to_Layout;
        typesExercise_to_Layout = utilities.deleteCopiesofList(types);

        //Log.v(TAG,"typesExercise_to_Layout: "+typesExercise_to_Layout);
        //Log.v(TAG,"typesExercise_to_Layout size: "+typesExercise_to_Layout.size());


        //Log.v(TAG,"ISOMETRIC: "+ISOMETRIC);
        //Log.v(TAG,"CARDIO: "+CARDIO);
        //Log.v(TAG,"PYLOMETRICS: "+PYLOMETRICS);
        //Log.v(TAG,"STRENGTH: "+STRENGTH);


        int ISOMETRIC_ = 0, CARDIO_ = 0, STRENGTH_ = 0, PYLOMETRICS_ = 0;

        if (ISOMETRIC > 0){
            //Log.v(TAG,"porcentaje ISOMETRIC: "+ISOMETRIC+"/"+types.size());
            ISOMETRIC_ = (ISOMETRIC *100 / types.size());
            //Log.v(TAG,"porcentaje: "+ISOMETRIC_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(this,getResources().getString(R.string.ISOMETRIC),ISOMETRIC_);
            contentInfo.addView(relativeLayout);

        }if (CARDIO > 0){
            //Log.v(TAG,"porcentaje CARDIO: "+CARDIO+"/"+types.size());
            CARDIO_ = ( CARDIO*100 / types.size());
            //Log.v(TAG,"porcentaje: "+CARDIO_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(this,getResources().getString(R.string.CARDIO),CARDIO_);
            contentInfo.addView(relativeLayout);

        }if (STRENGTH > 0){
            //Log.v(TAG,"porcentaje STRENGTH: "+STRENGTH+"/"+types.size());
            STRENGTH_ = ((STRENGTH*100/ types.size()));
            //Log.v(TAG,"porcentaje: "+STRENGTH_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(this,getResources().getString(R.string.STRENGTH),STRENGTH_);
            contentInfo.addView(relativeLayout);

        }if (PYLOMETRICS > 0) {
            //Log.v(TAG, "porcentaje PYLOMETRICS: " + PYLOMETRICS + "/" + types.size());
            PYLOMETRICS_ = ((PYLOMETRICS * 100 / types.size()));
            //Log.v(TAG, "porcentaje: " + PYLOMETRICS_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(this,getResources().getString(R.string.PYLOMETRICS),PYLOMETRICS_);
            contentInfo.addView(relativeLayout);
        }
    }

    private void logMessage(String msg) {
        Log.v(TAG, msg);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
