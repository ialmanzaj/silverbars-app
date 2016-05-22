package com.example.project.calisthenic;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fabtransitionactivity.SheetLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Workout extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    ImageView star_on, star_off;
    Button workout, playlist, plusPositive, minusPositive, plusIsometric, minusIsometric, plusNegative, minusNegative, plusReps, minusReps;
    TextView Positive, Negative, Isometric;
    ArrayList<File> mySongs, play_list;
    long[] position;
    View rootView;
    TabHost tabHost2;
    FloatingActionButton mFab;
    Spinner spinner;
    List<String> spinnerArray = new ArrayList<String>();
    int value = 0, tempoTotal = 0;
    TextView Reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                working();
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
            }
        });
        minusReps = (Button) findViewById(R.id.minusReps);
        minusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Reps,minusReps,plusReps);
            }
        });
        minusReps.setEnabled(false);
        minusReps.setClickable(false);


        Positive = (TextView) findViewById(R.id.Positive);
        Isometric = (TextView) findViewById(R.id.Isometric);
        Negative = (TextView) findViewById(R.id.Negative);

        spinner = (Spinner) findViewById(R.id.spinner);
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
                        Positive.setText("10");
                        Isometric.setText("5");
                        Negative.setText("10");
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
                        Positive.setText("5");
                        Isometric.setText("2");
                        Negative.setText("5");
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
                        Isometric.setText("1");
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




//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.activity_workout, container, false);

//        star_off = (ImageView) findViewById(R.id.star_off);
//        star_on = (ImageView) findViewById(R.id.star_on);
//        playlist = (Button) findViewById(R.id.playlist);
//        playlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                S_playlist();
//            }
//        });
//        workout =(Button) findViewById(R.id.workout);
//        workout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                working();
//            }
//        });
//        star_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Favorite(view);
//            }
//        });
//
//        star_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Unfavorite(view);
//            }
//        });

        //Tabs
        tabHost2 = (TabHost) findViewById(R.id.tabHost2);
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

//        return rootView;
    }


    public void Favorite(View v){
        star_off.setVisibility(v.GONE);
        star_on.setVisibility(v.VISIBLE);
    }

    public void Unfavorite(View v){
        star_on.setVisibility(v.GONE);
        star_off.setVisibility(v.VISIBLE);
    }

    public void working() {
        int positive,isometric,negative, totalReps;
        totalReps = Integer.parseInt(Reps.getText().toString());
        if (totalReps > 0){
            positive = Integer.parseInt(Positive.getText().toString());
            isometric = Integer.parseInt(Isometric.getText().toString());
            negative = Integer.parseInt(Negative.getText().toString());
            tempoTotal = positive+isometric+negative;
            Intent intent = new Intent(this, WorkingOut.class);
            intent.putExtra("reps",totalReps);
            intent.putExtra("tempo",tempoTotal);
            intent.putExtra("pos",position);
            intent.putExtra("songlist",mySongs);
            startActivity(intent);
        }
        else
            toast("Dont be lazzy, have to do more than 1 Rep");

    }

    public void S_playlist() {
        Intent intent = new Intent(this, Playlist_Picker.class);
        startActivity(intent);
        finish();
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
