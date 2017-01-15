package com.app.proj.silverbars.activities;


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

import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.ServiceGenerator;
import com.app.proj.silverbars.adapters.ExercisesAdapter;
import com.app.proj.silverbars.models.Exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseListActivity extends AppCompatActivity {

    private static final String TAG = ExerciseListActivity.class.getSimpleName();



    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.list)RecyclerView list;
    @BindView(R.id.add)Button mAddExercisesbt;

    @BindView(R.id.error_view) LinearLayout error_layout;
    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.reload)Button mReload;


    private RecyclerView.Adapter adapter;

    private ArrayList<String> exercises_id = new ArrayList<>();
    
    
    List<Exercise> OriginalExerciseListAll = new ArrayList<>();
    List<Exercise> ExercisesNoSelected = new ArrayList<>();
    ArrayList<String> ExercisesSelected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise_list);


        setupToolbar();


        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExercisesFromAPI();

            }
        });

        
        list.setLayoutManager(new LinearLayoutManager(this));



        mAddExercisesbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < OriginalExerciseListAll.size(); i++){
                    if (ExercisesAdapter.selectedItems.get(i)){
                        exercises_id.add(OriginalExerciseListAll.get(i).getExercise_name());
                    }
                }

                // enviar items a la actividad anterior
                Intent return_Intent = new Intent();
                return_Intent.putExtra("exercises",exercises_id);
                setResult(RESULT_OK, return_Intent);
                finish();

                //deselecionar todos los elementos elegidos
                ExercisesAdapter.selectedItems.clear();
            }
        });

        getExercisesFromAPI();
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_exercises_title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getExercisesFromAPI() {


        MainService service = ServiceGenerator.createService(MainService.class);
        service.getExercises().enqueue(new Callback<Exercise[]>() {
            @Override
            public void onResponse(Call<Exercise[]> call, Response<Exercise[]> response) {
                if (response.isSuccessful()){
                    onErrorOff();

                    Collections.addAll(OriginalExerciseListAll,response.body());

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

                        adapter = new ExercisesAdapter(ExerciseListActivity.this,OriginalExerciseListAll);

                    }else {

                        adapter = new ExercisesAdapter(ExerciseListActivity.this,ExercisesNoSelected);
                    }

                    list.setAdapter(adapter);


                }else {

                    Log.e(TAG,"response "+response.code());
                    onErrorOn();
                }


            }
            @Override
            public void onFailure(Call<Exercise[]> call, Throwable t) {
                Log.e(TAG,"onFailure ",t);
                onErrorOn();
            }
        });



    }
    
    private void onErrorOn(){
        error_layout.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        mAddExercisesbt.setVisibility(View.GONE);
    }
    
    
    private void onErrorOff(){
        mLoadingView.setVisibility(View.GONE);
        error_layout.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        mAddExercisesbt.setVisibility(View.VISIBLE);
    }



}