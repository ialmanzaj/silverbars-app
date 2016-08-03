package com.app.proj.silverbars;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreateWorkoutFinalActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    LinearLayout addExercise, reAdd;
    ImageView addImg, imgProfile;
    TextView addText, Sets, Rest, RestSets;
    EditText workoutName;
    Button plusSets, minusSets, plusRest, minusRest, plusRestSet, minusRestSet, Save;
    private ImageButton changeImg;

    private int value = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Workout");
        }


        workoutName = (EditText) findViewById(R.id.workoutName);
        Save = (Button) findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String name = workoutName.getText().toString();
//                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
//                sqLiteHelper.insertWorkouts(name,);
            }
        });




        plusSets = (Button) findViewById(R.id.plusSets);
        plusSets.setOnClickListener(this);
        minusSets = (Button) findViewById(R.id.minusSets);
        minusSets.setOnClickListener(this);
        minusSets.setEnabled(false);
        plusRest = (Button) findViewById(R.id.plusRest);
        plusRest.setOnClickListener(this);
        minusRest = (Button) findViewById(R.id.minusRest);
        minusRest.setOnClickListener(this);
        plusRestSet = (Button) findViewById(R.id.plusRestSets);
        plusRestSet.setOnClickListener(this);
        minusRestSet = (Button) findViewById(R.id.minusRestSets);
        minusRestSet.setOnClickListener(this);
        Sets = (TextView) findViewById(R.id.Sets);
        Rest = (TextView) findViewById(R.id.Rest);
        RestSets = (TextView) findViewById(R.id.RestSets);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);



        changeImg = (ImageButton) findViewById(R.id.chageImg);
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }

        });

        //addImg = (ImageView) findViewById(R.id.addImg);
        //addText = (TextView) findViewById(R.id.addText);




    }





    public void plusTempo(TextView view, Button button, Button button2){
        if (view == Sets){
            value = Integer.parseInt(view.getText().toString());
            if (value+1 == 10)
                view.setText(String.valueOf(value+1));
            else
                view.setText("0"+String.valueOf(value+1));
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
        }else if(view == Rest){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 5;
            if (value + 5 == 5)
                view.setText("0"+String.valueOf(value+"s"));
            else
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
        }else if(view == RestSets){
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

    public void minusTempo(TextView view, Button button, Button button2){
        if (view == Sets){
            value = Integer.parseInt(view.getText().toString());
            view.setText("0"+String.valueOf(value-1));
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
        }else if(view == Rest){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 5;
            if (value == 5)
                view.setText("0"+String.valueOf(value+"s"));
            else if(value == 0)
                view.setText("0"+String.valueOf(value+"s"));
            else
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
        }else if(view == RestSets){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 10;
            if (value == 0)
                view.setText("0"+String.valueOf(value+"s"));
            else
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.plusSets:
                plusTempo(Sets,plusSets,minusSets);
                break;
            case R.id.minusSets:
                minusTempo(Sets,minusSets,plusSets);
                break;
            case R.id.plusRest:
                plusTempo(Rest,plusRest,minusRest);
                break;
            case R.id.minusRest:
                minusTempo(Rest,minusRest,plusRest);
                break;
            case R.id.plusRestSets:
                plusTempo(RestSets,plusRestSet,minusRestSet);
                break;
            case R.id.minusRestSets:
                minusTempo(RestSets,minusRestSet,plusRestSet);
                break;

        }
    }
}