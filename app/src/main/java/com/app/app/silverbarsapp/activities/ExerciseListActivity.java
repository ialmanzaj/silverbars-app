package com.app.app.silverbarsapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExercisesAdapter;
import com.app.app.silverbarsapp.components.DaggerExerciseListComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.modules.ExerciseListModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ExerciseListPresenter;
import com.app.app.silverbarsapp.viewsets.ExerciseListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseListActivity extends BaseActivity implements ExerciseListView {

    private static final String TAG = ExerciseListActivity.class.getSimpleName();

    @Inject
    ExerciseListPresenter mExerciseListPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.exercises_list)RecyclerView list;
    @BindView(R.id.add_exercises)Button mAddExercises;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload)Button mReload;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    private ExercisesAdapter adapter;

    private ArrayList<String> mExercisesNames = new ArrayList<>();

    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<String> mExercisesSelected;

    @Override
    protected int getLayout() {
        return R.layout.activity_exercise_list;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mExerciseListPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerExerciseListComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .exerciseListModule(new ExerciseListModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null)
            setExtras(extras);

        setupToolbar();

        list.setLayoutManager(new LinearLayoutManager(this));

        mExerciseListPresenter.getExercises();


        //onProgressViewOff();
        //mExercises = new Gson().fromJson(getJson(),new TypeToken<ArrayList<Exercise>>(){}.getType());
        //setAdapter();
    }


    private void setExtras(Bundle extras){
        mExercisesSelected = extras.getStringArrayList("exercises");
        verifyExercises();
    }

    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_exercises_title));
    }


    private String getJson(){
        String json = null;
        try {
            InputStream is = this.getAssets().open("exercises.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    @OnClick(R.id.reload)
    public void reload(){
        //mExerciseListPresenter.getExercises();
    }


    @OnClick(R.id.add_exercises)
    public void addExercises(){


        for(int i = 0; i < adapter.getExercisesSelected().size(); i++) {
            int key = adapter.getExercisesSelected().keyAt(i);

            // get the object by the key.
            Object obj = adapter.getExercisesSelected().get(key);
            if ((Boolean)obj){
                Log.i(TAG, String.valueOf(i));
                mExercisesNames.add(mExercises.get(i).getExercise_name());
                Log.i(TAG, String.valueOf(mExercisesNames));
            }
        }


        if (mExercisesNames.size() > 0)
            sentToCreateWorkout();

    }

    private void sentToCreateWorkout(){
        // enviar items a la actividad anterior
        Intent return_intent = new Intent();
        return_intent.putExtra("exercises",mExercises);
        return_intent.putExtra("exercises_selected",mExercisesNames);
        setResult(RESULT_OK, return_intent);
        finish();
    }


    @Override
    public void displayExercises(List<Exercise> exercises) {
        onErrorOff();
        onProgressViewOff();

        mExercises.addAll(exercises);

        verifyExercises();
    }

    @Override
    public void displayNetworkError() {
        onErrorOn();
    }

    @Override
    public void displayServerError() {
        onErrorOn();
    }

    private void verifyExercises(){

        if (mExercisesSelected == null){
            Log.i(TAG, "no se ha seleccionado ningun ejercicio todavia");
            setAdapter(mExercises);
        }else {
            Log.v(TAG,"Exercises Selected"+mExercisesSelected);
            setAdapter(getExercisesNoSelected());
        }

    }


    private ArrayList<Exercise> getExercisesNoSelected(){
        ArrayList<Exercise> mExercisesNoSelected = mExercises;

        for (int c = 0;c < mExercisesSelected.size();c++){
            for (int a = 0; a < mExercises.size(); a++){
                //ejercicio seleccionado lo elimina en la siguiente seleccion
                if (Objects.equals(mExercises.get(a).getExercise_name(), mExercisesSelected.get(c))){
                    mExercisesNoSelected.remove(a);
                }
            }
        }

        return mExercisesNoSelected;
    }

    private void setAdapter(List<Exercise> exercises){
        adapter = new ExercisesAdapter(this,exercises);
        list.setAdapter(adapter);
    }

    private void onErrorOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorOff(){
        mErrorView.setVisibility(View.GONE);
    }

    private void onProgressViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onProgressViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}