package com.app.proj.silverbars.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.adapters.ExerciseAdapter;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Muscle;
import com.app.proj.silverbars.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutActivity extends AppCompatActivity {

    private static final String TAG = WorkoutActivity.class.getSimpleName();
    

    // UI FIELDS
    private Button plusSets;
    private Button minusSets;
    private Button plusRest;
    private Button minusRest;
    private Button plusRestSets;
    private Button minusRestSets;



    @BindView()TabHost Tab_layout;

    @BindView() LinearLayout primary_ColumnMuscle;
    @BindView() LinearLayout secundary_ColumnMuscle;
    @BindView() LinearLayout Progress;


    @BindView()LinearLayout contentInfo;
    
    @BindView() LinearLayout error_layout;
    Button button_error_reload;




    @BindView() TextView Positive;

    private TextView Negative,
            Isometric,
            Reps,
            Workout_name,
            Sets,
            RestbySet,
            RestbyExercise,
            RestSets_dialog,
            Sets_dialog;

    @BindView() RecyclerView list;

    @BindView() WebView webview;

    @BindView() TextView Rest_by_exercise_dialog;

    @BindView() Toolbar myToolbar;

    @BindView()SwitchCompat enableLocal;
    @BindView()SwitchCompat vibration_per_rep;
    @BindView()SwitchCompat vibration_per_set;
    @BindView()SwitchCompat vibration_per_set;

    @BindView()SwitchCompat voice_per_exercise;



    private ExerciseAdapter adapter;

    //VARIABLES
    private String[] Songs_names;
    private int value = 0;

    private int ExerciseReps = 1;
    private ArrayList<File> Songs_files;

    RelativeLayout togle_no_internet;
    private Boolean respuesta_recibida = false;
    Button startButton;

    private boolean isTouched = false;
    private boolean loadLocal = false;

    private int workoutId = 0, workoutSets = 0;
    private String workoutName, workoutLevel, mainMuscle, workoutImgUrl;

    private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

    private  boolean VibrationIsActivePerRep=false;
    private  boolean VibrationIsActivePerSet=false;
    private boolean DownloadAudioExercise = false;

    private String partes = "";

    String PlaylistSpotify,Token;

    private  List<String> MusclesArray = new ArrayList<>();
    private  List<String> TypeExercises = new ArrayList<>();
    private ArrayList<ExerciseRep> mExercises;


    private Utilities utilities = new Utilities();

    Boolean user_workout;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        workoutId = bundle.getInt("workout_id",0);
        workoutName = bundle.getString("name");
        workoutImgUrl = bundle.getString("image");
        workoutSets = bundle.getInt("sets", 1);
        workoutLevel = bundle.getString("level");
        mainMuscle = bundle.getString("main_muscle");
        user_workout = bundle.getBoolean("user_workout", false);
        mExercises =  intent.getParcelableArrayListExtra("exercises");







        startButton = (Button) findViewById(R.id.start_button);

        // MUSCLES TEXT VIEW
        contentInfo = (LinearLayout)findViewById(R.id.content_info);
        primary_ColumnMuscle = (LinearLayout) findViewById(R.id.column1);
        secundary_ColumnMuscle = (LinearLayout) findViewById(R.id.column2);

        Progress = (LinearLayout) findViewById(R.id.progress_bar_);

        error_layout = (LinearLayout) findViewById(R.id.error_layout);

         button_error_reload = (Button) findViewById(R.id.error_reload_workout);

        enableLocal = (SwitchCompat) findViewById(R.id.enableLocal);
        vibration_per_rep = (SwitchCompat) findViewById(R.id.vibration_per_rep);
        vibration_per_set = (SwitchCompat) findViewById(R.id.vibration_per_set);




        button_error_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_layout.setVisibility(View.GONE);
                Progress.setVisibility(View.VISIBLE);
                putExercisesInAdapter(mExercises);
            }
        });






        voice_per_exercise = (SwitchCompat) findViewById(R.id.voice_per_exercise);
        voice_per_exercise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                DownloadAudioExercise = isChecked;

                if (DownloadAudioExercise){
                    if (mExercises.size() > 0){
                        for (int a = 0;a<mExercises.size();a++){
                            utilities.createExerciseAudio(WorkoutActivity.this, DownloadAudioExercise, mExercises.get(a).getExercise().getExercise_audio());
                        }

                    }

                }
            }
        });


        
        list = (RecyclerView) findViewById(R.id.reciclador);

        final RelativeLayout selectMusic = (RelativeLayout) findViewById(R.id.SelectMusic);

        //TEXTVIEW OF REPS,SETS AND REST

        // cantidad de sets
        Sets = (TextView) findViewById(R.id.Sets);

        //descanso entre ejercicio
        RestbyExercise = (TextView) findViewById(R.id.RestbyExercise);

        // descanso entre sets
        RestbySet = (TextView) findViewById(R.id.RestbySet);

        // Web view
        webview = (WebView) findViewById(R.id.webview);


        // layout to put local workouts
        togle_no_internet = (RelativeLayout) findViewById(R.id.togle_no_internet);


        //  TOOL BAR - BACK BUTTON  ADDED
        myToolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(workoutName);


        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (list != null){
            list.setNestedScrollingEnabled(false);
            list.setHasFixedSize(false);
        }


        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        list.setLayoutManager(lManager);

        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }

        // CREAR BASE DE DATOS



        //SWITCH save local workout BUTTON CONFIGURACIONES
        enableLocal.setEnabled(true);

        enableLocal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });


        enableLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isTouched) {
                    isTouched = false;

                    if (isChecked && !loadLocal){

                    }else{
                        logMessage("Switch off");
                    }
                }
            }
        });


        Sets.setText(String.valueOf(workoutSets));
        Sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.set_edit)
                        .customView(R.layout.edit_set_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                Sets.setText(Sets_dialog.getText());

                            }
                        })
                        .show()
                        .getCustomView();
                if (v != null) {
                    Sets_dialog = (TextView) v.findViewById(R.id.Sets_dialog);
                    Sets_dialog.setText(String.valueOf(Sets.getText()));

                    plusSets = (Button) v.findViewById(R.id.plusSets);
                    plusSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            plusTempo(Sets_dialog,plusSets,minusSets);

                        }
                    });
                    minusSets = (Button) v.findViewById(R.id.minusSets);
                    minusSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(Sets_dialog,minusSets,plusSets);
                        }
                    });
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

            }
        });


        RestbyExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rest_exercise_text)
                        .customView(R.layout.edit_rest_exercise_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                RestbyExercise.setText(Rest_by_exercise_dialog.getText());
                            }
                        })
                        .show()
                        .getCustomView();
                if (v != null) {
                    Rest_by_exercise_dialog = (TextView) v.findViewById(R.id.Rest_exercise);
                    Rest_by_exercise_dialog.setText(String.valueOf(RestbyExercise.getText()));


                    plusRest = (Button) v.findViewById(R.id.plusRest);
                    plusRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plusTempo(Rest_by_exercise_dialog,plusRest,minusRest);
                        }
                    });
                    minusRest = (Button) v.findViewById(R.id.minusRest);
                    minusRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(Rest_by_exercise_dialog,minusRest,plusRest);
                        }
                    });
                }
            }
        });



        RestbySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rest_sets_text)
                        .customView(R.layout.edit_rest_sets_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                RestbySet.setText(RestSets_dialog.getText());
                            }
                        })
                        .show()
                        .getCustomView();

                if (v != null) {

                    RestSets_dialog = (TextView) v.findViewById(R.id.RestSets_dialog);
                    RestSets_dialog.setText(String.valueOf(RestbySet.getText()));

                    plusRestSets = (Button) v.findViewById(R.id.plusRestSets);
                    plusRestSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plusTempo(RestSets_dialog,plusRestSets,minusRestSets);



                        }
                    });
                    minusRestSets = (Button) v.findViewById(R.id.minusRestSets);
                    minusRestSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(RestSets_dialog,minusRestSets,plusRestSets);

                        }
                    });
                }
            }
        });

        selectMusic.setClickable(true);
        selectMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            selectMusic.setBackgroundColor(getResources().getColor(R.color.onTouch,null));
                        }else {
                            selectMusic.setBackgroundColor(getResources().getColor(R.color.onTouch));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            selectMusic.setBackgroundColor(getResources().getColor(R.color.white,null));
                        }else {
                            selectMusic.setBackgroundColor(getResources().getColor(R.color.white));
                        }
                        startActivityForResult(new Intent(WorkoutActivity.this, SelectionMusicActivity.class),1);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });

        //START WORKOUT BUTTON

        // vibracion por repeticion y por set colocado cada switch
        vibration_per_rep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VibrationIsActivePerRep = isChecked;
                Log.v(TAG,"VibrationIsActivePerRep: "+VibrationIsActivePerRep);
            }
        });


        vibration_per_set.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VibrationIsActivePerSet = isChecked;
                Log.v(TAG,"VibrationIsActivePerSet: "+VibrationIsActivePerSet);
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchWorkingOutActivity();
            }
        });

    }// on create close


    private void setupTabs(){

        //Defining Tabs
        Tab_layout = (TabHost) findViewById(R.id.tabHost2);
        Tab_layout.setup();

        TabHost.TabSpec overview = Tab_layout.newTabSpec(getResources().getString(R.string.tab_overview));
        TabHost.TabSpec muscles = Tab_layout.newTabSpec(getResources().getString(R.string.tab_muscles));
        TabHost.TabSpec exercises = Tab_layout.newTabSpec(getResources().getString(R.string.tab_exercises));

        overview.setIndicator(getResources().getString(R.string.tab_overview));
        overview.setContent(R.id.overview);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        exercises.setIndicator(getResources().getString(R.string.tab_exercises));
        exercises.setContent(R.id.exercises);

        Tab_layout.addTab(overview);
        Tab_layout.addTab(exercises);
        Tab_layout.addTab(muscles);
    }



    private void putExercisesInAdapter(ArrayList<ExerciseRep> exercises){

        for (ExerciseRep exerciseRep: exercises){

            exerciseRep.setTempo_positive(1);
            exerciseRep.setTempo_isometric(1);
            exerciseRep.setTempo_negative(1);

            Collections.addAll(TypeExercises, exerciseRep.getExercise().getTypes_exercise());

            for (Muscle muscle:  exerciseRep.getExercise().getMuscles()){
                Collections.addAll(MusclesArray,muscle.getMuscleName());
            }
        }

        adapter = new ExerciseAdapter(this,exercises);
        list.setAdapter(adapter);
        setMusclesToView(MusclesArray);
        putTypesInWorkout(TypeExercises);
    }


    private void LaunchWorkingOutActivity() {

        int sets,restbyexercise,restbyset;

        sets = Integer.parseInt(Sets.getText().toString());
        restbyexercise = Integer.parseInt(utilities.removeLastChar(RestbyExercise.getText().toString()));
        restbyset = Integer.parseInt(utilities.removeLastChar(RestbySet.getText().toString()));


        Intent intent = new Intent(this, WorkingOutActivity.class);
        intent.putParcelableArrayListExtra("exercises", adapter.getExercises());


        intent.putExtra("songs",Songs_names);
        intent.putExtra("songlist",Songs_files);
        intent.putExtra("Sets",sets);
        intent.putExtra("RestByExercise",restbyexercise);
        intent.putExtra("RestBySet",restbyset);
        intent.putExtra("VibrationPerSet",VibrationIsActivePerSet);
        intent.putExtra("VibrationPerRep",VibrationIsActivePerRep);
        intent.putExtra("playlist_spotify",PlaylistSpotify);
        intent.putExtra("token",Token);
        intent.putExtra("play_exercise_audio",DownloadAudioExercise);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            if (data.hasExtra("playlist_spotify") && data.hasExtra("token")){

                PlaylistSpotify = data.getStringExtra("playlist_spotify");
                Token =  data.getStringExtra("token");

            }else if (data.hasExtra("songs") && data.hasExtra("positions")){

                Songs_names = data.getStringArrayExtra("positions");
                Songs_files = (ArrayList<File>) data.getSerializableExtra("songs");


            }
        }
    }



    private void plusTempo(TextView view, Button button, Button button2){
        if (view == Reps){
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value+1));
            value++;
            if (value == 20){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == Rest_by_exercise_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 5;
            view.setText(String.valueOf(value+"s"));
            if (value == 60){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 0){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == RestSets_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 10;
            view.setText(String.valueOf(value+"s"));
            if (value == 180){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 0){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value+1));
            value++;
            if (value == 10){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }

    }

    private void minusTempo(TextView view, Button button, Button button2){
        if (view == Reps){
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value-1));
            value--;
            if ((value)==1){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 20){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == Rest_by_exercise_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 5;
            view.setText(String.valueOf(value+"s"));
            if (value == 0){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 60){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == RestSets_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 10;
            view.setText(String.valueOf(value+"s"));
            if (value == 0){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 180){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value-1));
            value--;
            if ((value)==1){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 10){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }
    }

    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){

            List<String> musculos_oficial =  utilities.deleteCopiesofList(musculos);


            for (int a = 0;a<musculos_oficial.size();a++) {

                final TextView MuscleTextView = new TextView(this);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webview.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        utilities.injectJS(partes,webview);
                        super.onPageFinished(view, url);
                    }

                });
            }
        });

        webview.getSettings().setJavaScriptEnabled(true);

        // ACCEDER A LA URL DEL HTML GUARDADO EN EL PHONE
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);

        if (muscle_url.equals("/")){
            webview.loadUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html");
        }else {
            String fileurl = "file://"+muscle_url;
            webview.loadUrl(fileurl);
        }
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


}
