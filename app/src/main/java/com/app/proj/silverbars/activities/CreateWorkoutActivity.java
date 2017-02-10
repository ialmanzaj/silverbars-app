package com.app.proj.silverbars.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SilverbarsApp;
import com.app.proj.silverbars.adapters.ExercisesSelectedAdapter;
import com.app.proj.silverbars.components.DaggerCreateWorkoutComponent;
import com.app.proj.silverbars.models.Exercise;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Muscle;
import com.app.proj.silverbars.modules.CreateWorkoutModule;
import com.app.proj.silverbars.presenters.BasePresenter;
import com.app.proj.silverbars.presenters.CreateWorkoutPresenter;
import com.app.proj.silverbars.utils.OnStartDragListener;
import com.app.proj.silverbars.utils.SimpleItemTouchHelperCallback;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.CreateWorkoutView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;


public class CreateWorkoutActivity extends BaseActivity implements CreateWorkoutView,OnStartDragListener {

    private static final String TAG = CreateWorkoutActivity.class.getSimpleName();


    @Inject
    CreateWorkoutPresenter mCreateWorkoutPresenter;


    private Utilities utilities = new Utilities();



    @BindView(R.id.toolbar) Toolbar toolbar;


    @BindView(R.id.content_empty) LinearLayout empty_content;

    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.content_info) LinearLayout contentView;
    @BindView(R.id.readd) Button mButtonReAdd;

    @BindView(R.id.recycler_exercises_selected) RecyclerView list;

    @BindView(R.id.column1) LinearLayout primary_ColumnMuscle;
    @BindView(R.id.column2) LinearLayout secundary_ColumnMuscle;

    @BindView(R.id.progress) LinearLayout ProgressView;
    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button button_error_reload;


    @BindView(R.id.next) Button nextButton;
    @BindView(R.id.add_exercises) Button addExercise;



    private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

    private ItemTouchHelper mItemTouchHelper;


    private  List<String> mMuscles = new ArrayList<>();
    private List<Exercise> AllExercisesList = new ArrayList<>();

    private String partes = "";

    private ExercisesSelectedAdapter adapter;
    
    private List<ExerciseRep> mExercisesAdapter = new ArrayList<>();

    private ArrayList<String> exercises_id;



    @Override
    protected int getLayout() {
        return R.layout.activity_create_workout;
    }


    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mCreateWorkoutPresenter;
    }


    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerCreateWorkoutComponent
                .builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .createWorkoutModule(new CreateWorkoutModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupToolbar();

        setupTabs();

        setupAdapter();



        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles_);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {scrollView.setFillViewport(true);}


        button_error_reload.setOnClickListener(v -> {

           /* if (exercises_id.size() > 0){
                onErrorViewOff();
                onProgressOn();
                setExercisesSelected(exercises_id);
            }

*/

        });


        nextButton.setOnClickListener(view -> {
            if (adapter != null){

                if (adapter.getItemCount() > 0){

                    ArrayList<ExerciseRep> exerciseReps = new ArrayList<>();
                    for (ExerciseRep exerciseRep: adapter.getSelectedExercises()){exerciseReps.add(exerciseRep);}

                    Intent intent = new Intent(CreateWorkoutActivity.this, CreateWorkoutFinalActivity.class);
                    intent.putParcelableArrayListExtra("exercises",exerciseReps);
                    startActivityForResult(intent,3);

                }else {
                    Toast.makeText(CreateWorkoutActivity.this, getResources().getString(R.string.exercises_no_selected),
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CreateWorkoutActivity.this, getResources().getString(R.string.exercises_no_selected),
                        Toast.LENGTH_SHORT).show();
            }
        });


        addExercise.setOnClickListener(v -> {
            Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);
            startActivityForResult(intent,1);
        });


        mButtonReAdd.setOnClickListener(v -> {

            Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);
            intent.putExtra("exercises",adapter.getSelectedExercisesName());
            startActivityForResult(intent,1);
        });





        utilities.setBodyInWebwView(this,webView);

    }//  close create workout



    private void setupAdapter(){
        list.setLayoutManager(new LinearLayoutManager(this));


        adapter = new ExercisesSelectedAdapter(this,this);
        list.setAdapter(adapter);


        //touch listener
        mItemTouchHelper  = new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(list);
    }


    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_create_workout));

        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
        toolbar.setNavigationOnClickListener(v -> finish());
    }



    private void setupTabs(){

        //Defining Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost3);
        tabHost2.setup();

        TabHost.TabSpec rutina = tabHost2.newTabSpec(getResources().getString(R.string.tab_overview));
        TabHost.TabSpec muscles = tabHost2.newTabSpec(getResources().getString(R.string.tab_muscles));
        rutina.setIndicator(getResources().getString(R.string.tab_overview));
        rutina.setContent(R.id.rutina_);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles_);

        tabHost2.addTab(rutina);
        tabHost2.addTab(muscles);
    }


    private List<ExerciseRep> findExerciseByName(List<String> exercises_names){
        //Log.v(TAG,"exercise names:"+exercises_names);

        List<ExerciseRep> exerciseList = new ArrayList<>();

            for (String exercise: exercises_names){
                for (int a = 0; a < AllExercisesList.size(); a++){

                    if (Objects.equals(exercise, AllExercisesList.get(a).getExercise_name())){

                        ExerciseRep exerciserep = new ExerciseRep();
                        exerciserep.setExercise(AllExercisesList.get(a));
                        exerciseList.add(exerciserep);

                    }

                }
            }


        return exerciseList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null){
                if (data.hasExtra("exercises")) {

                    exercises_id = data.getStringArrayListExtra("exercises");
                      /*  if (exercises_id.size() > 0){
                            setExercisesSelected(exercises_id);
                        }*/
                }
            }
        }

    }



    @Override
    public void displayExercises(List<Exercise> exercises) {
        setEmptyContentOff();

/*
        AllExercisesList = exercises;


        //putTypesInWorkout(TypeExercises);
        setMuscles(mExercisesAdapter);
        mExercisesAdapter = findExerciseByName(new_exercises);


        adapter.setOnDataChangeListener(new ExercisesSelectedAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int size) {
                setMuscles(adapter.getSelectedExercises());
            }
        });*/
    }



    @Override
    public void displayServerError() {

    }

    @Override
    public void displayNetworkError() {

    }


    private void getCountTimes(List<String> list){
        for (int a = 0; a<list.size();a++) {
            if (list.get(a).equals("ISOMETRIC")) {
                ISOMETRIC = ISOMETRIC+1;
            }if (list.get(a).equals("CARDIO")){
                CARDIO=CARDIO+1;
            }if(list.get(a).equals("PYLOMETRICS")){
                PYLOMETRICS = PYLOMETRICS+1;
            }if (list.get(a).equals("STRENGTH")){
                STRENGTH = STRENGTH+1;
            }
        }
    }


    private void putTypesInWorkout(List<String> types){
        List<String> typesExercise;
        typesExercise = utilities.deleteCopiesofList(types);
        getCountTimes(typesExercise);

        Log.v(TAG,"ISOMETRIC: "+ISOMETRIC);
        Log.v(TAG,"CARDIO: "+CARDIO);
        Log.v(TAG,"PYLOMETRICS: "+PYLOMETRICS);
        Log.v(TAG,"STRENGTH: "+STRENGTH);

        int[] porcentaje = new int[typesExercise.size()];



        for (int a = 0;a<typesExercise.size();a++) {

            TextView textView = new TextView(this);
            LinearLayout linear = new LinearLayout(this);
            linear.setOrientation(LinearLayout.HORIZONTAL);
            ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);

            if (ISOMETRIC > 0) {
                porcentaje[a] = (int) (((double) ISOMETRIC / typesExercise.size()) * 100);
            }
            if (CARDIO > 0) {
                porcentaje[a] = (int) (((double) CARDIO / typesExercise.size()) * 100);
            }
            if (STRENGTH > 0) {
                porcentaje[a] = (int) (((double) STRENGTH / typesExercise.size()) * 100);
            }
            if (PYLOMETRICS > 0) {
                porcentaje[a] = (int) (((double) PYLOMETRICS / typesExercise.size()) * 100);
            }

            Log.v(TAG, "porcentaje: " + porcentaje[a]);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT);

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params3.weight = 1.0f;

            progress.setLayoutParams(params2);
            progress.setLayoutParams(params);

            progress.setProgress(porcentaje[a]);
            progress.setMax(100);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
                progress.setBackgroundTintList((ColorStateList.valueOf(Color.RED)));
            } else {
                progress.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            }

            textView.setText(typesExercise.get(a));
            textView.setGravity(Gravity.START);
            textView.setLayoutParams(params3);

            textView.setTextColor(getResources().getColor(R.color.black));
            linear.setPadding(15, 15, 15, 15);
            linear.addView(textView);
            linear.addView(progress);

            linear.setMinimumHeight(45);

            //contentView.addView(linear);

        }
    }

    private void addNewExercisesToAdapter(List<String> exercises){
        for (ExerciseRep exercise: findExerciseByName(exercises) ){
            mExercisesAdapter.add(exercise);
            adapter.notifyItemInserted(mExercisesAdapter.size());
        }

        setMuscles(adapter.getSelectedExercises());
    }

    private void setMuscles(List<ExerciseRep> exercises){
        mMuscles.clear();

        for (ExerciseRep exerciseRep: exercises){
            //Log.v(TAG,"exercise: "+exerciseRep.getExercise().getExercise_name());
            for (Muscle muscle: exerciseRep.getExercise().getMuscles()){

                mMuscles.add(muscle.getMuscleName());
                //Log.v(TAG,"muscle: "+muscle.getMuscleName());
            }
        }

        setMusclesToView(mMuscles);
    }

    private void setMusclesToView(List<String> musculos){
        //Log.v(TAG,"setMusclesToView: "+musculos);

        partes = "";

        if (musculos.size() > 0){


            List<String> mMusclesFinal = utilities.deleteCopiesofList(musculos);


            for (int a = 0;a<mMusclesFinal.size();a++) {
                TextView mMusclesTextView = new TextView(this);

                partes += "#"+ mMusclesFinal.get(a) + ",";

                //Log.v(TAG,"partes:" +partes);

                mMusclesTextView.setText(mMusclesFinal.get(a));
                mMusclesTextView.setGravity(Gravity.CENTER);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {mMusclesTextView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));}else {mMusclesTextView.setTextColor(getResources().getColor(R.color.gray_active_icon));}

                if (a%2 == 0){
                    //secundary_ColumnMuscle.addView(mMusclesTextView);
                }else {
                    //primary_ColumnMuscle.addView(mMusclesTextView);
                }


            }
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                utilities.injectJS(partes,webView);
                super.onPageFinished(view, url);
            }
        });

        //webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setJavaScriptEnabled(true);


        utilities.setBodyInWebwView(this,webView);
    }



    private void setEmptyContentOff(){
        empty_content.setVisibility(View.GONE);
        mButtonReAdd.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);
    }


    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);
    }

    private void onProgressOn(){
        ProgressView.setVisibility(View.VISIBLE);
    }

    private void onProgressOff(){
        ProgressView.setVisibility(View.GONE);
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {mItemTouchHelper.startDrag(viewHolder);}



}
