package com.app.project.silverbars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreateWorkout extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout addExercise;
    ImageView addImg;
    TextView addText;

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
        addImg = (ImageView) findViewById(R.id.addImg);
        addText = (TextView) findViewById(R.id.addText);
        addExercise = (LinearLayout) findViewById(R.id.addExercise);
        addExercise.setClickable(true);
        addExercise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        addImg.setImageResource(R.drawable.add_white);
                        addText.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case MotionEvent.ACTION_UP:
                        addImg.setImageResource(R.drawable.add);
                        addText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        Intent intent = new Intent(CreateWorkout.this,exerciseList.class);
                        startActivityForResult(intent,1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
