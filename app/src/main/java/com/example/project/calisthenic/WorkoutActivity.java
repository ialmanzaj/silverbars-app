package com.example.project.calisthenic;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkoutActivity extends AppCompatActivity {

    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private Button SelectMusic;
    private TextView Positive, Negative, Isometric, Reps, Workout_name;
    private String[] position;
    private List<String> spinnerArray = new ArrayList<String>();
    private int value = 0;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private int ExerciseReps = 1;
    private ArrayList<File> mySongs;
    static public int[] Exercises_reps;
    private String[] exercises;
    private String workout_name;
    private int workout_id = 0, workout_sets = 0;
    private List<WorkoutInfo> items = new ArrayList<>();
    public static JsonExercise[] ParsedExercises;
    public static JsonReps[] ParsedReps;
    private int[] exercises_id;


    private static boolean VibrationIsActivePerRep=true;
    private static boolean VibrationIsActivePerSet=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        exercises = intent.getStringArrayExtra("exercises");
        Log.v("Exercises",Arrays.toString(exercises));
        workout_name = intent.getStringExtra("name");
        workout_id = intent.getIntExtra("id",0);
        workout_sets = intent.getIntExtra("sets",0);
        exercises_id = new int[exercises.length];
        new AsyncTaskParseJson().execute();

        setContentView(R.layout.activity_workout);

//        Workout_name = (TextView) findViewById(R.id.Workout_name);
//        Workout_name.setText(workout_name);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ======= TOOL BAR - BACK BUTTON  ADDED
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(workout_name);
        }

        Button startButton = (Button) findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchWorkingOutActivity();
            }
        });

        // ACTIVAR VIBRACION POR SET O POR REPETICION

        Switch VibrationRep = (Switch) findViewById(R.id.vibration_rep);
        Switch VibrationSet = (Switch) findViewById(R.id.vibration_set);


        VibrationRep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VibrationIsActivePerRep = isChecked;
            }
        });

        VibrationSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VibrationIsActivePerSet = isChecked;
            }
        });


//
//        Tab 2

        Reps = (TextView) findViewById(R.id.Reps);

        Positive = (TextView) findViewById(R.id.Positive);
        Isometric = (TextView) findViewById(R.id.Isometric);
        Negative = (TextView) findViewById(R.id.Negative);

        plusPositive = (Button) findViewById(R.id.plusPositive);
        plusPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Positive,plusPositive,minusPositive);
            }
        });
        minusPositive = (Button) findViewById(R.id.minusPositive);
        minusPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Positive,minusPositive,plusPositive);
            }
        });
        plusIsometric = (Button) findViewById(R.id.plusIsometric);
        plusIsometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Isometric,plusIsometric,minusIsometric);
            }
        });
        minusIsometric = (Button) findViewById(R.id.minusIsometric);
        minusIsometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Isometric,minusIsometric,plusIsometric);
            }
        });
        plusNegative = (Button) findViewById(R.id.plusNegative);
        plusNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Negative,plusNegative,minusNegative);
            }
        });
        minusNegative = (Button) findViewById(R.id.minusNegative);
        minusNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Negative,minusNegative,plusNegative);
            }
        });
        minusIsometric.setEnabled(false);
        minusIsometric.setClickable(false);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinnerArray.add("Easy");
        spinnerArray.add("Normal");
        spinnerArray.add("Hard");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item,spinnerArray
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        plusPositive.setEnabled(true);
                        plusPositive.setClickable(true);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(true);
                        plusNegative.setClickable(true);
                        minusPositive.setEnabled(true);
                        minusPositive.setClickable(true);
                        minusIsometric.setEnabled(false);
                        minusIsometric.setClickable(false);
                        minusNegative.setEnabled(true);
                        minusNegative.setClickable(true);
                        Positive.setText("3");
                        Isometric.setText("1");
                        Negative.setText("3");
                        break;
                    case 1:
                        plusPositive.setEnabled(true);
                        plusPositive.setClickable(true);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(true);
                        plusNegative.setClickable(true);
                        minusPositive.setEnabled(true);
                        minusPositive.setClickable(true);
                        minusIsometric.setEnabled(false);
                        minusIsometric.setClickable(false);
                        minusNegative.setEnabled(true);
                        minusNegative.setClickable(true);
                        Positive.setText("2");
                        Isometric.setText("1");
                        Negative.setText("2");
                        break;
                    case 2:
                        plusPositive.setEnabled(true);
                        plusPositive.setClickable(true);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(true);
                        plusNegative.setClickable(true);
                        minusPositive.setEnabled(false);
                        minusPositive.setClickable(false);
                        minusIsometric.setEnabled(true);
                        minusIsometric.setClickable(true);
                        minusNegative.setEnabled(false);
                        minusNegative.setClickable(false);
                        Positive.setText("1");
                        Isometric.setText("3");
                        Negative.setText("1");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Tab 3 Inicializar Exercises en el RecyclerView

//        items.add(new WorkoutInfo(R.mipmap.imagen1, "Upper Body", "core", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen2, "Core", "Arms and Back", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen3, "Arms and Back", "Legs", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen4, "Legs", "Full Body", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen5, "Full body", "End Workout", String.valueOf(ExerciseReps)));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        if (recycler != null) {
            recycler.setHasFixedSize(true);
        }

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        //Defining Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost2);
        tabHost2.setup();


        TabHost.TabSpec setup = tabHost2.newTabSpec("Setup");
        TabHost.TabSpec exercises = tabHost2.newTabSpec("Exercises");
        TabHost.TabSpec muscles = tabHost2.newTabSpec("Muscles");



        setup.setIndicator("Setup");
        setup.setContent(R.id.setup);

        exercises.setIndicator("Exercises");
        exercises.setContent(R.id.exercises);

        muscles.setIndicator("Muscles");
        muscles.setContent(R.id.muscles);

        tabHost2.addTab(exercises);
        tabHost2.addTab(setup);
        tabHost2.addTab(muscles);



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
            toast("Activity result");
        }
        else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            mySongs = null;
            position = null;
            toast("No result");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    public void ShowMusicDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(WorkoutActivity.this)
                .theme(Theme.LIGHT)
                .title("Choose source of your music")
                .adapter(new MusicSourceAdapter(WorkoutActivity.this), new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which){
                            case 0:
                                LaunchMusicActivity();
                                break;
                            case 1:
                                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                    }
                })
                .negativeText(android.R.string.cancel)
                .build();
        dialog.show();
    }

    private void LaunchWorkingOutActivity() {
        int positive,isometric,negative;
            if (Positive.getText().toString() != "" && Isometric.getText().toString() != "" && Negative.getText().toString() != ""){
                positive = Integer.parseInt(Positive.getText().toString());
                isometric = Integer.parseInt(Isometric.getText().toString());
                negative = Integer.parseInt(Negative.getText().toString());
                int tempoTotal = positive + isometric + negative;
                Intent intent = new Intent(this, WorkingOutActivity.class);
                intent.putExtra("ExercisesReps",Exercises_reps);
                intent.putExtra("tempo", tempoTotal);
                intent.putExtra("pos",position);
                intent.putExtra("songlist",mySongs);
                intent.putExtra("Sets",workout_sets);
                intent.putExtra("VibrationPerSet",VibrationIsActivePerSet);
                intent.putExtra("VibrationPerRep",VibrationIsActivePerRep);
                startActivity(intent);
            }
            else{
                toast("Must select a tempo");
            }
    }

    public void LaunchMusicActivity() {
        Intent intent = new Intent(this, PlaylistPickerActivity.class);
        startActivityForResult(intent,1);
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void plusTempo(TextView view, Button button, Button button2){
        value = Integer.parseInt(view.getText().toString());
        view.setText(String.valueOf(value+1));
        value++;
        if (view == Reps){
            if (value == 20){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
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

    public void minusTempo(TextView view, Button button, Button button2){
        value = Integer.parseInt(view.getText().toString());
        view.setText(String.valueOf(value-1));
        value--;
        if (view == Reps){
            if ((value)==1){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 20){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
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

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {
            JsonParser JsonData = new JsonParser();

            ParsedExercises = new JsonExercise[exercises.length];
            try {
                for (int i = 0; i < exercises.length; i++){
                    JsonExercise ExerciseData = JsonData.getExercise(exercises[i]);
                    ParsedExercises[i] = ExerciseData;
                    items.add(new WorkoutInfo(ExerciseData.exercise_name, String.valueOf(ExerciseReps)));
                    exercises_id[i] = ExerciseData.id;
                }
                JsonReps[] RepsData = JsonData.getReps("http://api.silverbarsapp.com/workout/?format=json",workout_id,exercises.length);
                ParsedReps = RepsData;
                Log.v("Reps",Arrays.toString(ParsedReps));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            Exercises_reps = new int[items.size()];
            for (int i = 0; i <items.size() ; i++){
                String exercise = ParsedReps[i].exercise;
                if (exercise.indexOf("exercises/"+exercises_id[i])>0){
                    Exercises_reps[i] = ParsedReps[i].repetition;
                }
//                Log.v("Repetitions",String.valueOf(Exercises_reps[i]));
            }
            adapter = new ExerciseAdapter(items);
            recycler.setAdapter(adapter);
        }
    }


}
