package com.app.proj.silverbars;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class ExerciseListActivity extends AppCompatActivity {

    private static final String TAG = "EXERCISE LIST";
    Toolbar toolbar;
    RecyclerView recycler;
    Button add_button;
    public static JsonExercise[] Exercises;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> sExercises_Id = new ArrayList<>();
    
    List<JsonExercise> OriginalExerciseListAll = new ArrayList<>();

    List<JsonExercise> ExercisesNoSelected = new ArrayList<>();


    ArrayList<String> SelectedItemsIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Exercises");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);



        add_button = (Button) findViewById(R.id.done_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (int i = 0; i < OriginalExerciseListAll.size(); i++){
                    if (AllExercisesAdapter.selectedItems.get(i)){
                        sExercises_Id.add(OriginalExerciseListAll.get(i).getExercise_name());

                    }
                }



                // enviar items a la actividad anterior
                Intent return_Intent = new Intent();
                return_Intent.putExtra("Items",sExercises_Id);
                setResult(RESULT_OK, return_Intent);
                finish();


                //deselecionar todos los elementos elegidos
                AllExercisesAdapter.selectedItems.clear();



            }
        });

        Exercises();


    }

    public void Exercises() {


        MySQLiteHelper database = new MySQLiteHelper(ExerciseListActivity.this);

        for (int i = 0; i < database.getAllExercises().length; i++){
                Log.v(TAG,"exercises:"+database.getExercise(database.getExercisesIds()[i]).getExercise_name() );
                OriginalExerciseListAll.add( database.getExercise(database.getExercisesIds()[i]) );

            }

            Log.v(TAG,"OriginalExerciseListAll size: "+OriginalExerciseListAll.size());

            try {

                Intent i = getIntent();
                Bundle b = i.getExtras();

                SelectedItemsIds = b.getStringArrayList("items_selected");
                Log.v(TAG,"items recibido: "+SelectedItemsIds);

                ExercisesNoSelected = OriginalExerciseListAll;

                for (int c = 0;c < SelectedItemsIds.size();c++){
                    for (int a = 0; a < OriginalExerciseListAll.size(); a++){

                        if (Objects.equals(OriginalExerciseListAll.get(a).getExercise_name(), SelectedItemsIds.get(c))){
                            ExercisesNoSelected.remove(a);


                        }
                    }
                }


            }catch (NullPointerException e){
                Log.e(TAG, "no se ha seleccionado ningun ejercicio todavia");
            }


            if (ExercisesNoSelected.isEmpty()){
                adapter = new AllExercisesAdapter(ExerciseListActivity.this,OriginalExerciseListAll);
                Log.v(TAG,"OriginalExerciseListAll: active");
            }else {
                adapter = new AllExercisesAdapter(ExerciseListActivity.this,ExercisesNoSelected);
                Log.v(TAG,"ExercisesNoSelected: active");
            }

            recycler.setAdapter(adapter);



    }
}