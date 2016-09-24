package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.app.proj.silverbars.Utilities.CreateNewView;
import static com.app.proj.silverbars.Utilities.convertArrayToString;
import static com.app.proj.silverbars.Utilities.deleteCopiesofList;
import static com.app.proj.silverbars.Utilities.getFileReady;
import static com.app.proj.silverbars.Utilities.removeLastChar;
import static com.app.proj.silverbars.Utilities.saveWorkoutImgInDevice;
import static com.app.proj.silverbars.Utilities.saveAudioInDevice;

/*import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;*/

public class WorkoutActivity extends AppCompatActivity {

    private Button plusSets;
    private Button minusSets;
    private Button plusRest;
    private Button minusRest;
    private Button plusRestSets;
    private Button minusRestSets;
    TabHost Tab_layout;
    private TextView Positive, Negative, Isometric, Reps, Workout_name, Sets, RestbySet, RestbyExercise,RestSets_dialog,Sets_dialog;
    private String[] Songs_names;
    private int value = 0;
    private RecyclerView ExercisesRecycler;
    private RecyclerView.Adapter adapter;
    private int ExerciseReps = 1;
    private ArrayList<File> Songs_files;

    static public int[] Exercises_reps;
    static public int[] Positive_Exercises;
    static public int[] Isometric_Exercises;
    static public int[] Negative_Exercises;

    RelativeLayout togle_no_internet;
    private Boolean respuesta_recibida = false;
    Button startButton;
    private LinearLayout primary_ColumnMuscle,secundary_ColumnMuscle,Progress;
    LinearLayout contentInfo;
    private boolean isTouched = false;
    private boolean loadLocal = false;

//    Workout Data
    private int workoutId = 0, workoutSets = 0;
    private String workoutName, workoutLevel, mainMuscle, workoutImgUrl;
    private String[] exercises;


    private List<WorkoutInfo> exercisesToRecycler = new ArrayList<>();
    public static JsonExercise[] ParsedExercises;
    public static JsonWorkoutReps[] ExerciseswithReps;

    List<JsonExercise> exerciseList = new ArrayList<>();


    private static final String TAG ="WORKOUT ACTIVITY";

    private LinearLayout error_layout;

    private  boolean VibrationIsActivePerRep=false;
    private  boolean VibrationIsActivePerSet=false;
    private boolean DownloadAudioExercise = false;

    private String partes = "";
    private WebView webview;
    private TextView Rest_by_exercise_dialog;

    private  List<String> MusclesArray = new ArrayList<>();
    private  List<String> TypeExercises = new ArrayList<>();


    String PlaylistSpotify,Token;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        workoutId = intent.getIntExtra("id",0);
        workoutName = intent.getStringExtra("name");
        workoutImgUrl = intent.getStringExtra("image");
        workoutSets = intent.getIntExtra("sets", 1);
        workoutLevel = intent.getStringExtra("level");
        mainMuscle = intent.getStringExtra("muscle");
        exercises = intent.getStringArrayExtra("exercises");


        Boolean user_workout = intent.getBooleanExtra("user_workout", false);

        ParsedExercises = new JsonExercise[exercises.length];
        
        
        setContentView(R.layout.activity_workout);

        startButton = (Button) findViewById(R.id.start_button);
        startButton.setEnabled(false);


        // MUSCLES TEXT VIEW
        contentInfo = (LinearLayout)findViewById(R.id.content_info);
        primary_ColumnMuscle = (LinearLayout) findViewById(R.id.column1);
        secundary_ColumnMuscle = (LinearLayout) findViewById(R.id.column2);


        Progress = (LinearLayout) findViewById(R.id.progress_bar_);
        //error layout
        error_layout = (LinearLayout) findViewById(R.id.error_layout);
        Button button_error_reload = (Button) findViewById(R.id.error_reload_workout);

        button_error_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                error_layout.setVisibility(View.GONE);
                Progress.setVisibility(View.VISIBLE);
                getExercisesFromAPI();

            }
        });


        SwitchCompat enableLocal = (SwitchCompat) findViewById(R.id.enableLocal);
        SwitchCompat vibration_per_rep = (SwitchCompat) findViewById(R.id.vibration_per_rep);
        SwitchCompat vibration_per_set = (SwitchCompat) findViewById(R.id.vibration_per_set);
        SwitchCompat voice_per_exercise = (SwitchCompat) findViewById(R.id.voice_per_exercise);


        voice_per_exercise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                DownloadAudioExercise = isChecked;
            }
        });
        
        ExercisesRecycler = (RecyclerView) findViewById(R.id.reciclador);

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(workoutName);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


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


        if (ExercisesRecycler != null){
            ExercisesRecycler.setNestedScrollingEnabled(false);
            ExercisesRecycler.setHasFixedSize(false);
        }


        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        ExercisesRecycler.setLayoutManager(lManager);

        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }


        // CREAR BASE DE DATOS
        MySQLiteHelper database = new MySQLiteHelper(WorkoutActivity.this);


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
                        Log.v(TAG,"respuesta:"+respuesta_recibida);
                        saveExercisesinDatabase();
                    }else{

                        MySQLiteHelper database = new MySQLiteHelper(WorkoutActivity.this);
                        loadLocal = false;
                        database.updateLocal(workoutId,"false");
                        logMessage("Switch off");
                    }
                }
            }
        });


        //COMPROBAR SI EL WORKOUT ESTA ACTIVADO EN LA BASE DE DATOS - SAVED WORKOUTS
        if (database.checkWorkouts(workoutId) && Objects.equals(database.checkWorkoutLocal(workoutId), "true")){

            loadLocal = true;
            enableLocal.setChecked(true);
            
            ExerciseswithReps = database.getWorkout(workoutId);
            ParsedExercises = new JsonExercise[ExerciseswithReps.length];
            Exercises_reps = new int[ExerciseswithReps.length];
            
            // inicializar tempo arrays por ejercicio
            Positive_Exercises = new int[ExerciseswithReps.length];
            Isometric_Exercises = new int[ExerciseswithReps.length];
            Negative_Exercises = new int[ExerciseswithReps.length];

            
            for (int i = 0; i < ExerciseswithReps.length; i++){
                ParsedExercises[i] = database.getExercise(Integer.valueOf(ExerciseswithReps[i].getExercise()));
                Exercises_reps[i] = ExerciseswithReps[i].getRepetition();

                // inicializar cada parte del tempo
                WorkoutActivity.Positive_Exercises[i] = 1;
                WorkoutActivity.Negative_Exercises[i] = 1;
                WorkoutActivity.Isometric_Exercises[i] = 1;

                //RECORRER CADA EJECICIOS BUSCANDO MUSCULOS
                Collections.addAll(MusclesArray, ParsedExercises[i].muscle);
                Collections.addAll(TypeExercises,ParsedExercises[i].getType_exercise());

                Log.v(TAG,"MusclesArray: "+MusclesArray);
                Log.v(TAG,"TypeExercises: "+TypeExercises);

                //agregar json a array exercisesToRecycler
                exercisesToRecycler.add(new WorkoutInfo(ParsedExercises[i].exercise_name, String.valueOf(ExerciseReps), WorkoutActivity.ParsedExercises[i].getExercise_image()));
            }
            
            //start button encendido ya que sin la data null exeption in Activity WORKINGOUT
            startButton.setEnabled(true);
            adapter = new ExerciseAdapter(exercisesToRecycler,WorkoutActivity.this);

            ExercisesRecycler.setAdapter(adapter);
            setMusclesToView(MusclesArray);
            putTypesInWorkout(TypeExercises);

        }else if (user_workout && database.checkUserWorkouts(workoutId) ){
            //USER WORKOUTS
            togle_no_internet.setVisibility(View.GONE);// this only for workouts in API

             ExerciseswithReps = database.getUserWorkout(workoutId);
            
            ParsedExercises = new JsonExercise[ExerciseswithReps.length];
            Exercises_reps = new int[ExerciseswithReps.length];

            // inicializar tempo arrays por ejercicio
            Positive_Exercises = new int[ExerciseswithReps.length];
            Isometric_Exercises = new int[ExerciseswithReps.length];
            Negative_Exercises = new int[ExerciseswithReps.length];

            for (int i = 0; i < ExerciseswithReps.length; i++){

                ParsedExercises[i] = database.getExercise(Integer.valueOf(ExerciseswithReps[i].getExercise()));
                Log.v(TAG,"Exercises"+ParsedExercises[i].getExercise_name());


                Exercises_reps[i] = ExerciseswithReps[i].getRepetition();
                
                // inicializar cada parte del tempo
                WorkoutActivity.Positive_Exercises[i] = 1;
                WorkoutActivity.Negative_Exercises[i] = 1;
                WorkoutActivity.Isometric_Exercises[i] = 1;
                
                //RECORRER CADA EJECICIOS BUSCANDO MUSCULOS
                Collections.addAll(MusclesArray, ParsedExercises[i].muscle);
                Collections.addAll(TypeExercises,ParsedExercises[i].getType_exercise());

                Log.v(TAG,"MusclesArray: "+MusclesArray);
                Log.v(TAG,"TypeExercises: "+TypeExercises);


                //agregar json a array exercisesToRecycler
                exercisesToRecycler.add(new WorkoutInfo(ParsedExercises[i].exercise_name, String.valueOf(ExerciseReps), WorkoutActivity.ParsedExercises[i].getExercise_image()));

            }

            startButton.setEnabled(true);
            adapter = new ExerciseAdapter(exercisesToRecycler,WorkoutActivity.this);
            ExercisesRecycler.setAdapter(adapter);
            setMusclesToView(MusclesArray);
            putTypesInWorkout(TypeExercises);

        } else {
            loadLocal = false;
            enableLocal.setChecked(false);
            getExercisesFromAPI();
        }


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
                Log.v(TAG,"START BUTTON: ACTIVADO");
                LaunchWorkingOutActivity();
            }
        });

    }// on create close


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            Log.v(TAG,"RESULT_OK: YES");


            if (data.hasExtra("playlist_spotify") && data.hasExtra("token")){

                PlaylistSpotify = data.getStringExtra("playlist_spotify");
                Token =  data.getStringExtra("token");


                Log.v(TAG,"Token: "+Token);
                Log.v(TAG,"PlaylistSpotify: "+PlaylistSpotify);

            }else if (data.hasExtra("songs") && data.hasExtra("positions")){

                Songs_files = (ArrayList<File>) data.getSerializableExtra("songs");
                Songs_names = data.getStringArrayExtra("positions");
                
                Log.v("Songs_names: ",Arrays.toString(Songs_names));
                Log.v("Songs_files: ",Songs_files.toString());

            }

        }

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void LaunchWorkingOutActivity() {
        int sets,restbyexercise,restbyset;

        sets = Integer.parseInt(Sets.getText().toString());
        restbyexercise = Integer.parseInt(removeLastChar(RestbyExercise.getText().toString()));
        restbyset = Integer.parseInt(removeLastChar(RestbySet.getText().toString()));

        Collections.addAll(exerciseList,ParsedExercises);

        for (int a = 0;a<Exercises_reps.length;a++){
            exerciseList.get(a).setRep(Exercises_reps[a]);
        }

        Intent intent = new Intent(this, WorkingOutActivity.class);
        intent.putExtra("exercises", (Serializable) exerciseList);
        intent.putExtra("pos",Songs_names);
        intent.putExtra("songlist",Songs_files);
        intent.putExtra("Sets",sets);
        intent.putExtra("RestByExercise",restbyexercise);
        intent.putExtra("RestBySet",restbyset);
        intent.putExtra("VibrationPerSet",VibrationIsActivePerSet);
        intent.putExtra("VibrationPerRep",VibrationIsActivePerRep);
        intent.putExtra("Array_Positive_Exercises",Positive_Exercises);
        intent.putExtra("Array_Isometric_Exercises",Isometric_Exercises);
        intent.putExtra("Array_Negative_Exercises",Negative_Exercises);

        intent.putExtra("playlist_spotify",PlaylistSpotify);
        intent.putExtra("token",Token);
        intent.putExtra("audio_exercise",DownloadAudioExercise);
        startActivity(intent);
    }


    private void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
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
            List<String> musculos_oficial;
            musculos_oficial = deleteCopiesofList(musculos);

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
                        injectJS();
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

    private void injectJS() {
        try {
            if (!Objects.equals(partes, "")){
                partes = removeLastChar(partes);
                webview.loadUrl("javascript: ("+ "window.onload = function () {"+
                        "partes = Snap.selectAll('"+partes+"');"+
                        "partes.forEach( function(elem,i) {"+
                        "elem.attr({stroke:'#602C8D',fill:'#602C8D'});"+
                        "});"+ "}"+  ")()");
            }
        } catch (Exception e) {
            Log.e(TAG,"JAVASCRIPT Exception",e);
           onErrorOn();
        }
    }

    private void getExerciseRepsFromAPI(){
        Log.v(TAG,"getExerciseRepsFromAPI");

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v(TAG,response.toString());
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.silverbarsapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

         SilverbarsService service = retrofit.create(SilverbarsService.class);

        Call<JsonWorkoutReps[]> call = service.getExerciseReps();
        call.enqueue(new Callback<JsonWorkoutReps[]>() {
                    @Override
                    public void onResponse(Call<JsonWorkoutReps[]> call, Response<JsonWorkoutReps[]> response) {
                        if (response.isSuccessful()) {

                           onErrorOff();

                            JsonWorkoutReps[] Reps = response.body();
                            ExerciseswithReps = new JsonWorkoutReps[exercisesToRecycler.size()];

                            int y = 0;
                            for (JsonWorkoutReps Rep : Reps) {
                                String workout = Rep.getWorkout_id();
                                if (workout.indexOf("v1/workouts/" + workoutId) > 0) {
                                    ExerciseswithReps[y] = Rep;
                                    y++;
                                }
                            }

                            Exercises_reps = new int[exercisesToRecycler.size()];

                            // inicializar tempo arrays por ejercicio
                            Positive_Exercises = new int[exercisesToRecycler.size()];
                            Isometric_Exercises = new int[exercisesToRecycler.size()];
                            Negative_Exercises = new int[exercisesToRecycler.size()];

                            for (int i = 0; i <exercisesToRecycler.size() ; i++){

                                try {
                                    String[] audioDir = ParsedExercises[i].getExercise_audio().split("exercises");
                                    String Parsedurl = "exercises" + audioDir[1];
                                    String[] splitName = Parsedurl.split("/");
                                    String mp3Name = splitName[2];

                                    File Dir = getFileReady(WorkoutActivity.this,"/SilverbarsMp3");
                                   
                                    if (Dir.isDirectory()) {
                                        File file = getFileReady(WorkoutActivity.this,"/SilverbarsMp3/"+mp3Name);
                                        if (!file.exists()) {

                                            if (DownloadAudioExercise){
                                                DownloadMp3(Parsedurl, mp3Name);
                                            }
                                        }
                                    } else {
                                        boolean success = Dir.mkdir();
                                        if (success) {

                                            if (DownloadAudioExercise){
                                                DownloadMp3(Parsedurl, mp3Name);
                                            }
                                        }else
                                            Log.e(TAG, "Error creating dir");
                                    }

                                }catch (NullPointerException e){
                                    Log.e(TAG,"NullPointerException",e);
                                    
                                    Tab_layout.setVisibility(View.GONE);
                                    error_layout.setVisibility(View.VISIBLE);
                                }
                                
                                Exercises_reps[i] = ExerciseswithReps[i].getRepetition();

                                // inicializar cada parte del tempo
                                WorkoutActivity.Positive_Exercises[i] = 1;
                                WorkoutActivity.Isometric_Exercises[i] = 1;
                                WorkoutActivity.Negative_Exercises[i] = 1;

                            }

                            adapter = new ExerciseAdapter(exercisesToRecycler,WorkoutActivity.this);

                            ExercisesRecycler.setAdapter(adapter);
                            setMusclesToView(MusclesArray);
                            putTypesInWorkout(TypeExercises);
                            startButton.setEnabled(true);

                        } else {
                            onErrorOn();
                            int statusCode = response.code();
                            ResponseBody errorBody = response.errorBody();
                            Log.e(TAG, "getExerciseRepsFromAPI: "+errorBody);
                            Log.e(TAG, "statusCode: "+statusCode);
                        }
                    }
                    @Override
                    protected void finalize() throws Throwable {
                        super.finalize();
                    }

                    @Override
                    public void onFailure(Call<JsonWorkoutReps[]> call, Throwable t) {
                        Log.e(TAG,"getExercisesfromJson, onfailure",t);
                        onErrorOn();
                    }
                });
    }

    private void onErrorOn(){
        Tab_layout.setVisibility(View.GONE);
        error_layout.setVisibility(View.VISIBLE);
    }

    private void onErrorOff(){
        error_layout.setVisibility(View.GONE);
        Tab_layout.setVisibility(View.VISIBLE);
    }

    private void DownloadMp3(final String url, final String getAudioName) {
        Log.v(TAG,"DownloadMp3: "+getAudioName);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        final SilverbarsService downloadService = retrofit.create(SilverbarsService.class);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... workouts_ids) {
                Call<ResponseBody> call = downloadService.downloadFile(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            boolean writtenToDisk = saveAudioInDevice(WorkoutActivity.this,response.body(),getAudioName);
                            Log.v(TAG, String.valueOf(writtenToDisk));
                        }
                        else {Log.e(TAG, "DownloadMp3, Download server failed:");}
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "DownloadMp3: onFailure",t);
                    }
                });
                return null;
            };
        }.execute();
    }

    private void logMessage(String msg) {
        Log.v(TAG, msg);
    }

    //guardar ejercicios en la base de datos
    private void saveExercisesinDatabase(){

        MySQLiteHelper database = new MySQLiteHelper(WorkoutActivity.this);
        for (int i = 0; i < ParsedExercises.length; i++){
            if (!database.checkExercise(ParsedExercises[i].getId()) ){
                File imgDir,mp3Dir;
                imgDir = getFileReady(this,"/SilverbarsImg/"+getImageName(i));
                mp3Dir = getFileReady(this,"/SilverbarsMp3/"+getAudioName(i));

                if (!imgDir.exists()){
                    Log.v(TAG,"img url: "+ParsedExercises[i].getExercise_image());
                    DownloadImage(ParsedExercises[i].getExercise_image(),getImageName(i));
                }
                database.insertExercises(
                        ParsedExercises[i].getId(),
                        ParsedExercises[i].getExercise_name(),
                        ParsedExercises[i].getLevel(),
                        convertArrayToString(ParsedExercises[i].getType_exercise()),
                        convertArrayToString(ParsedExercises[i].getMuscle()),
                        mp3Dir.getPath(),
                        imgDir.getPath()
                );
            }else{
                database.updateLocal(workoutId,"true");
            }
        }
        saveWorkoutinTableWorkouts();
    }

    private void saveWorkoutinTableWorkouts(){
        String[] exercises_ids = new String[ParsedExercises.length];
        MySQLiteHelper database = new MySQLiteHelper(WorkoutActivity.this);
        File imgDir = getFileReady(this,"/SilverbarsImg/"+getWorkoutImage(workoutImgUrl));
        if (!imgDir.exists()){
            Log.v(TAG,"img url "+workoutImgUrl);
            DownloadImage(workoutImgUrl,getWorkoutImage(workoutImgUrl));
        }
        for (int i = 0; i < exercises_ids.length; i++){
            exercises_ids[i] = String.valueOf(ParsedExercises[i].getId());
        }
        if (!database.checkWorkouts(workoutId)) {
            database.insertWorkouts(
                    workoutId,
                    workoutName,
                    imgDir.getPath(),
                    workoutSets,
                    workoutLevel,
                    mainMuscle,
                    convertArrayToString(exercises_ids),
                    1,
                    "true"
            );
        }
        saveWorkout();
    }

    private String getWorkoutImage(String workoutUrl){
        String[] workoutImgDir = workoutUrl.split("workouts");
        String Parsedurl = "workouts"+workoutImgDir[1];
        String[] imagesName = Parsedurl.split("/");
        String workoutImgName = imagesName[2];
        Log.v(TAG,"Image Name: "+workoutImgName);
        return workoutImgName;
    }

    private void saveWorkout(){
        MySQLiteHelper database = new MySQLiteHelper(WorkoutActivity.this);
        for (int i = 0; i < ParsedExercises.length; i++) {
            if (!database.checkWorkout(workoutId,ParsedExercises[i].getId())) {
                database.insertWorkout(
                        workoutId,
                        ParsedExercises[i].getId(),
                        Exercises_reps[i]
                );
            }
        }
    }


    private void DownloadImage(String url, final String imgName){
        Log.v(TAG,"DownloadImage ");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        saveWorkoutImgInDevice(WorkoutActivity.this,response.body(),imgName);
                    } else {
                        Log.v(TAG, "Download server contact failed");
                    }
                } else {
                    Log.v(TAG,"State server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,"onFAILURE",t);
            }
        });
    }

    private String getImageName(int Songs_names){
        String[] imageDir = ParsedExercises[Songs_names].getExercise_image().split("exercises");
        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        return imagesName[2];
    }

    private String getAudioName(int Songs_names){
        String[] audioDir = ParsedExercises[Songs_names].getExercise_audio().split("exercises");;
        String Parsedurl = "exercises"+audioDir[1];
        String[] splitName = Parsedurl.split("/");
        return splitName[2];
    }


    private void getExercisesFromAPI(){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();
                okhttp3.Response response = chain.proceed(request);
                Log.v(TAG,response.toString());
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.silverbarsapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        SilverbarsService service = retrofit.create(SilverbarsService.class);

        for (int i = 0; i < exercises.length; i++){
            final int a = i;
            String [] parts = exercises[i].split("exercises");
            exercises[i] = "v1/exercises"+parts[1];

            Call<JsonExercise> call = service.getExercise(exercises[i]);
            call.enqueue(new Callback<JsonExercise>() {
                @Override
                public void onResponse(Call<JsonExercise> call, Response<JsonExercise> response) {
                    if (response.isSuccessful()) {
                        ParsedExercises[a] = response.body();

                        Progress.setVisibility(View.GONE);
                        Tab_layout.setVisibility(View.VISIBLE);

                        exercisesToRecycler.add(new WorkoutInfo(ParsedExercises[a].exercise_name, String.valueOf(ExerciseReps), WorkoutActivity.ParsedExercises[a].getExercise_image()));
                        Collections.addAll(TypeExercises,ParsedExercises[a].getType_exercise());

                        Log.v(TAG,"TypeExercises"+TypeExercises);
                        //RECORRER CADA EJECICIOS BUSCANDO MUSCULOS
                        Collections.addAll(MusclesArray, ParsedExercises[a].muscle);

                        if (exercisesToRecycler.size() == exercises.length){
                            getExerciseRepsFromAPI();
                        }

                    } else {
                        onErrorOn();
                        int statusCode = response.code();
                        ResponseBody errorBody = response.errorBody();
                        Log.e(TAG, "getExercisesFromAPI: "+errorBody);
                        Log.e(TAG, "statusCode: "+statusCode);
                    }
                }
                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
                @Override
                public void onFailure(Call<JsonExercise> call, Throwable t) {
                    Log.e(TAG,"getExercisesFromAPI, onFailure",t);
                    onErrorOn();
                }
            });
        }
    }

    private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

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
        typesExercise_to_Layout = deleteCopiesofList(types);

        Log.v(TAG,"typesExercise_to_Layout: "+typesExercise_to_Layout);
        Log.v(TAG,"typesExercise_to_Layout size: "+typesExercise_to_Layout.size());


        Log.v(TAG,"ISOMETRIC: "+ISOMETRIC);
        Log.v(TAG,"CARDIO: "+CARDIO);
        Log.v(TAG,"PYLOMETRICS: "+PYLOMETRICS);
        Log.v(TAG,"STRENGTH: "+STRENGTH);


        int ISOMETRIC_ = 0,CARDIO_ = 0,STRENGTH_ = 0,PYLOMETRICS_ = 0;

        if (ISOMETRIC > 0){
            Log.v(TAG,"porcentaje ISOMETRIC: "+ISOMETRIC+"/"+types.size());
            ISOMETRIC_ = (ISOMETRIC *100 / types.size());
            //Log.v(TAG,"porcentaje: "+ISOMETRIC_);

            RelativeLayout relativeLayout = CreateNewView(this,getResources().getString(R.string.ISOMETRIC),ISOMETRIC_);
            contentInfo.addView(relativeLayout);

        }if (CARDIO > 0){
            Log.v(TAG,"porcentaje CARDIO: "+CARDIO+"/"+types.size());
            CARDIO_ = ( CARDIO*100 / types.size());
            Log.v(TAG,"porcentaje: "+CARDIO_);

            RelativeLayout relativeLayout = CreateNewView(this,getResources().getString(R.string.CARDIO),CARDIO_);
            contentInfo.addView(relativeLayout);

        }if (STRENGTH > 0){
            Log.v(TAG,"porcentaje STRENGTH: "+STRENGTH+"/"+types.size());
            STRENGTH_ = ((STRENGTH*100/ types.size()));
            Log.v(TAG,"porcentaje: "+STRENGTH_);

            RelativeLayout relativeLayout = CreateNewView(this,getResources().getString(R.string.STRENGTH),STRENGTH_);
            contentInfo.addView(relativeLayout);

        }if (PYLOMETRICS > 0) {
            Log.v(TAG, "porcentaje PYLOMETRICS: " + PYLOMETRICS + "/" + types.size());
            PYLOMETRICS_ = ((PYLOMETRICS * 100 / types.size()));
            Log.v(TAG, "porcentaje: " + PYLOMETRICS_);

            RelativeLayout relativeLayout = CreateNewView(this,getResources().getString(R.string.PYLOMETRICS),PYLOMETRICS_);
            contentInfo.addView(relativeLayout);
        }

    }


}
