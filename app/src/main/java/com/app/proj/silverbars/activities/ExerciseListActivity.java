package com.app.proj.silverbars.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.adapters.ExercisesAdapter;
import com.app.proj.silverbars.models.Exercise;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.ExerciseListPresenter;
import com.app.proj.silverbars.viewsets.ExerciseListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseListActivity extends BaseActivity implements ExerciseListView {

    private static final String TAG = ExerciseListActivity.class.getSimpleName();


    ExerciseListPresenter mExerciseListPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;


    @BindView(R.id.list)RecyclerView list;
    @BindView(R.id.add)Button mAddExercisesbt;


    @BindView(R.id.error_view) LinearLayout error_layout;
    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.reload)Button mReload;


    private RecyclerView.Adapter adapter;

    private ArrayList<String> exercises_id = new ArrayList<>();
    
    
    List<Exercise> mExercises = new ArrayList<>();
    List<Exercise> mExercisesNoSelected;
    ArrayList<String> mExercisesSelected;



    @Override
    protected int getLayout() {
        return R.layout.activity_exercise_list;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar();


        //mReload.setOnClickListener(v -> getExercisesFromAPI());

        
        list.setLayoutManager(new LinearLayoutManager(this));



        mAddExercisesbt.setOnClickListener(v -> {
            
            for (int i = 0; i < mExercises.size(); i++){
                if (ExercisesAdapter.selectedItems.get(i)){
                    exercises_id.add(mExercises.get(i).getExercise_name());
                }
            }

            // enviar items a la actividad anterior
            Intent return_Intent = new Intent();
            return_Intent.putExtra("exercises",exercises_id);
            setResult(RESULT_OK, return_Intent);
            finish();

            //deselecionar todos los elementos elegidos
            ExercisesAdapter.selectedItems.clear();
        });

        //getExercisesFromAPI();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        setExtras(extras);
    }

    private void setExtras(Bundle extras){

        if (extras.isEmpty()){
            Log.i(TAG, "no se ha seleccionado ningun ejercicio todavia");
            return;
        }


        mExercisesSelected = extras.getStringArrayList("exercises");


        for (int c = 0;c < mExercisesSelected.size();c++){
            for (int a = 0; a < mExercises.size(); a++){

                //ejercicio seleccionado lo elimina en la siguiente seleccion
                if (Objects.equals(mExercises.get(a).getExercise_name(), mExercisesSelected.get(c))){
                    mExercisesNoSelected.remove(a);
                }
            }
        }

        setAdapter();
    }



    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_exercises_title));
    }


    @Override
    public void displayExercises(List<Exercise> exercises) {
        onErrorOff();

        mExercises = exercises;
        mExercisesNoSelected = exercises;

        setAdapter();
    }

    private void setAdapter(){
        if (mExercisesNoSelected.isEmpty()){

            adapter = new ExercisesAdapter(this,mExercises);

        }else {

            adapter = new ExercisesAdapter(this,mExercisesNoSelected);
        }

        list.setAdapter(adapter);
    }




    @Override
    public void displayNetworkError() {
        onErrorOn();
    }

    @Override
    public void displayServerError() {
        onErrorOn();
    }


    private void onErrorOn(){
    }


    private void onErrorOff(){
    }

}