package com.example.project.calisthenic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;



    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private Button plusReps;
    private Button minusReps;
    private Button SelectMusic;
    private TextView Positive, Negative, Isometric;
    private ArrayList<File> mySongs, play_list;
    private long[] position;
    private List<String> spinnerArray = new ArrayList<String>();
    private int value = 0;
    private TextView Reps;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private int ExerciseReps = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_workout);

        // ======= TOOL BAR - BACK BUTTON  ADDED
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Workouts");
        }



        FloatingActionButton startButton = (FloatingActionButton) findViewById(R.id.fab);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchWorkingOutActivity();
            }
        });

        Reps = (TextView) findViewById(R.id.Reps);

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
        plusReps = (Button) findViewById(R.id.plusReps);
        plusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Reps,plusReps,minusReps);
                ExerciseReps = Integer.valueOf(Reps.getText().toString());
            }
        });
        minusReps = (Button) findViewById(R.id.minusReps);
        minusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Reps,minusReps,plusReps);
                ExerciseReps = Integer.valueOf(Reps.getText().toString());
            }
        });
        minusReps.setEnabled(false);
        minusReps.setClickable(false);

        // Tab 3 Inicializar Exercises en el RecyclerView
        List<WorkoutInfo> items = new ArrayList<>();

        items.add(new WorkoutInfo(R.mipmap.imagen1, "Upper Body", "core", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen2, "Core", "Arms and Back", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen3, "Arms and Back", "Legs", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen4, "Legs", "Full Body", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen5, "Full body", "End Workout", String.valueOf(ExerciseReps)));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        if (recycler != null) {
            recycler.setHasFixedSize(true);
        }

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ExerciseAdapter(items);
        recycler.setAdapter(adapter);



        Positive = (TextView) findViewById(R.id.Positive);
        Isometric = (TextView) findViewById(R.id.Isometric);
        Negative = (TextView) findViewById(R.id.Negative);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinnerArray.add("Easy");
        spinnerArray.add("Normal");
        spinnerArray.add("Hard");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,spinnerArray
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        plusPositive.setEnabled(false);
                        plusPositive.setClickable(false);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(false);
                        plusNegative.setClickable(false);
                        minusPositive.setEnabled(true);
                        minusPositive.setClickable(true);
                        minusIsometric.setEnabled(true);
                        minusIsometric.setClickable(true);
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
                        minusIsometric.setEnabled(true);
                        minusIsometric.setClickable(true);
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
                        minusIsometric.setEnabled(false);
                        minusIsometric.setClickable(false);
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
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost2);
        tabHost2.setup();

        TabHost.TabSpec data1 = tabHost2.newTabSpec("Overview");
        TabHost.TabSpec data2 = tabHost2.newTabSpec("Setup");
        TabHost.TabSpec data3 = tabHost2.newTabSpec("Exercises");
        TabHost.TabSpec data4 = tabHost2.newTabSpec("Muscles");

        data1.setIndicator("Overview");
        data1.setContent(R.id.tab1);

        data2.setIndicator("Setup");
        data2.setContent(R.id.tab2);

        data3.setIndicator("Exercises");
        data3.setContent(R.id.tab3);

        data4.setIndicator("Muscles");
        data4.setContent(R.id.tab4);

        tabHost2.addTab(data1);
        tabHost2.addTab(data2);
        tabHost2.addTab(data3);
        tabHost2.addTab(data4);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (getIntent().hasExtra("songlist") && getIntent().hasExtra("pos")){
            mySongs = (ArrayList) b.getParcelableArrayList("songlist");
            position = b.getLongArray("pos");
        }

        SelectMusic = (Button) findViewById(R.id.SelectMusic);
        SelectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMusicDialog();
            }
        });
//        return rootView;
    }

    public void ShowMusicDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(WorkoutActivity.this)
                .title("Pick your Music")
                .customView(R.layout.musicmodal, true)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                toast("Password: " + passwordInput.getText().toString());
                    }
                }).build();

        dialog.show();
    }




    private void LaunchWorkingOutActivity() {
        int positive,isometric,negative, totalReps;
        totalReps = Integer.parseInt(Reps.getText().toString());
        if (totalReps > 0){
            if (Positive.getText().toString() != "" && Isometric.getText().toString() != "" && Negative.getText().toString() != ""){
                positive = Integer.parseInt(Positive.getText().toString());
                isometric = Integer.parseInt(Isometric.getText().toString());
                negative = Integer.parseInt(Negative.getText().toString());
                int tempoTotal = positive + isometric + negative;
                Intent intent = new Intent(this, MusicActivity.class);
                intent.putExtra("reps",totalReps);
                intent.putExtra("tempo", tempoTotal);
//                intent.putExtra("pos",position);
//                intent.putExtra("songlist",mySongs);
                startActivity(intent);
            }
            else{
                toast("Must select a tempo");
            }
        }
        else
            toast("Dont be lazzy, have to do more than 1 Rep");

    }

    public void LaunchMusicActivity() {
//        if (Environment.getExternalStorageDirectory().listFiles() != null){

            Intent intent = new Intent(this, MusicActivity.class);
            startActivity(intent);
//            finish();
//        }else{
//            toast("You don't have any audio file");
//        }


//        }else{
//            toast("You don't have any audio file");
//        }


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

}
