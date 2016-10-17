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
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExerciseListActivity extends AppCompatActivity {

    private static final String TAG = "EXERCISE LIST";
    Toolbar toolbar;
    RecyclerView recycler;
    Button add_button;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> exercises_id = new ArrayList<>();
    List<Exercise> OriginalExerciseListAll = new ArrayList<>();
    List<Exercise> ExercisesNoSelected = new ArrayList<>();
    ArrayList<String> ExercisesSelected = new ArrayList<>();

    private LinearLayout error_layout;
    private LinearLayout Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.add_exercises));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        Progress = (LinearLayout) findViewById(R.id.progress_bar_);
        error_layout = (LinearLayout) findViewById(R.id.error_layout);


        Button button_error_reload = (Button) findViewById(R.id.error_reload_workout);
        button_error_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExercisesFromAPI();

            }
        });


        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));


        add_button = (Button) findViewById(R.id.done_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < OriginalExerciseListAll.size(); i++){
                    if (AllExercisesAdapter.selectedItems.get(i)){
                        exercises_id.add(OriginalExerciseListAll.get(i).getExercise_name());
                    }
                }

                // enviar items a la actividad anterior
                Intent return_Intent = new Intent();
                return_Intent.putExtra("exercises",exercises_id);
                setResult(RESULT_OK, return_Intent);
                finish();

                //deselecionar todos los elementos elegidos
                AllExercisesAdapter.selectedItems.clear();
            }
        });

        getExercisesFromAPI();
    }

    private void getExercisesFromAPI() {

        TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(this);
        SilverbarsService service = ServiceGenerator.createService(SilverbarsService.class, tokenAuthenticator.getToken());

        service.getAllExercises().enqueue(new Callback<Exercise[]>() {
            @Override
            public void onResponse(Call<Exercise[]> call, Response<Exercise[]> response) {
                if (response.isSuccessful()){
                    onErrorOff();


                    try {
                        Intent i = getIntent();
                        Bundle bundle = i.getExtras();


                        ExercisesSelected = bundle.getStringArrayList("exercises");
                        Log.v(TAG,"exercise recibido: "+ExercisesSelected);

                        ExercisesNoSelected = OriginalExerciseListAll;

                        for (int c = 0;c < ExercisesSelected.size();c++){
                            for (int a = 0; a < OriginalExerciseListAll.size(); a++){

                                //ejercicio seleccionado lo elimina en la siguiente seleccion
                                if (Objects.equals(OriginalExerciseListAll.get(a).getExercise_name(), ExercisesSelected.get(c))){
                                    ExercisesNoSelected.remove(a);
                                }
                            }
                        }
                    }catch (NullPointerException e){
                        Log.i(TAG, "no se ha seleccionado ningun ejercicio todavia");
                    }

                    if (ExercisesNoSelected.isEmpty()){

                        adapter = new AllExercisesAdapter(ExerciseListActivity.this,OriginalExerciseListAll);

                    }else {

                        adapter = new AllExercisesAdapter(ExerciseListActivity.this,ExercisesNoSelected);
                    }

                    recycler.setAdapter(adapter);


                }else {

                    onErrorOn();
                }


            }
            @Override
            public void onFailure(Call<Exercise[]> call, Throwable t) {
                onErrorOn();
            }
        });
    }
    
    private void onErrorOn(){
        error_layout.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        add_button.setVisibility(View.GONE);
    }
    
    
    private void onErrorOff(){
        Progress.setVisibility(View.GONE);
        error_layout.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        add_button.setVisibility(View.VISIBLE);
    }



}