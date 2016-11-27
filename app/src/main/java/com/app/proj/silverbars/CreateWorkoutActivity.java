package com.app.proj.silverbars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateWorkoutActivity extends AppCompatActivity implements OnStartDragListener {

    private static final String TAG = "CreateWorkout";


    private LinearLayout empty_content;
    private WebView webView;
    private Button mButtonReAdd;
    private RecyclerView recycler;

    private LinearLayout primary_ColumnMuscle,secundary_ColumnMuscle, ProgressView,mErrorView;
    private LinearLayout contentView;


    private ItemTouchHelper mItemTouchHelper;


    private  List<String> mMuscles = new ArrayList<>();
    private List<Exercise> AllExercisesList = new ArrayList<Exercise>();

    private String partes = "";

    private ExercisesSelectedAdapter adapter;
    
    private List<ExerciseRep> mExercisesAdapter = new ArrayList<>();

    private ArrayList<String> exercises_id;

    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        utilities = new Utilities();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_create_workout));
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });



        contentView = (LinearLayout)findViewById(R.id.content_info);
        primary_ColumnMuscle = (LinearLayout) findViewById(R.id.column1);
        secundary_ColumnMuscle = (LinearLayout) findViewById(R.id.column2);

        empty_content = (LinearLayout) findViewById(R.id.content_empty);
        ProgressView = (LinearLayout) findViewById(R.id.progress);


        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles_);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {scrollView.setFillViewport(true);}

        
        mErrorView = (LinearLayout) findViewById(R.id.error_layout);

        Button button_error_reload = (Button) findViewById(R.id.error_reload_workout);
        button_error_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exercises_id.size() > 0){
                    onErrorViewOff();
                    onProgressOn();
                    putExercisesinRecycler(exercises_id);
                }

            }
        });


        /// boton de siguiente
        Button nextButton = (Button) findViewById(R.id.Next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });



        //botton de agregar ejercicios
        Button addExercise = (Button) findViewById(R.id.add_exercises);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);
                startActivityForResult(intent,1);
            }
        });


        // boton para volver agregar mas ejercicios
        mButtonReAdd = (Button) findViewById(R.id.reAdd);
        mButtonReAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);
                intent.putExtra("exercises",adapter.getSelectedExercisesName());
                startActivityForResult(intent,1);
            }
        });


        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.recycler_exercises_selected);
        recycler.setLayoutManager(new LinearLayoutManager(this));


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

        webView = (WebView) findViewById(R.id.WebView_create);



        getBodyView();

    }//  close create workout

    private void putExercisesinRecycler(final List<String> new_exercises){
        Log.v(TAG,"selected: "+new_exercises);
        setEmptyContentOff();


            MainService service = ServiceGenerator.createService(MainService.class);
            service.getAllExercises().enqueue(new Callback<Exercise[]>() {
                @Override
                public void onResponse(Call<Exercise[]> call, Response<Exercise[]> response) {
                    if (response.isSuccessful()){
                        onErrorViewOff();

                        if (adapter == null){
                            Collections.addAll(AllExercisesList,response.body());

                            
                            mExercisesAdapter = findExerciseByName(new_exercises);
                            adapter = new ExercisesSelectedAdapter(CreateWorkoutActivity.this,mExercisesAdapter,CreateWorkoutActivity.this);

                            onProgressOff();
                            recycler.setAdapter(adapter);


                            //putTypesInWorkout(TypeExercises);
                            setmMuscles(mExercisesAdapter);

                            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                            mItemTouchHelper  = new ItemTouchHelper(callback);
                            mItemTouchHelper.attachToRecyclerView(recycler);


                        }else {

                            addNewExercisesToAdapter(new_exercises);
                        }

                        if (adapter != null){


                            adapter.setOnDataChangeListener(new ExercisesSelectedAdapter.OnDataChangeListener() {
                                @Override
                                public void onDataChanged(int size) {
                                    Log.v(TAG,"onDataChanged");
                                    setmMuscles(adapter.getSelectedExercises());
                                }
                            });

                        }
                    }else
                        onErrorViewOn();
                }
                @Override
                public void onFailure(Call<Exercise[]> call, Throwable t) {
                    onErrorViewOn();
                }
            });

    }


    private List<ExerciseRep> findExerciseByName(List<String> exercises_names){
        Log.v(TAG,"exercise names:"+exercises_names);

        List<ExerciseRep> exerciseList = new ArrayList<>();

            for (String exercise: exercises_names){
                for (int a = 0; a < AllExercisesList.size(); a++){

                    if (Objects.equals(exercise, AllExercisesList.get(a).getExercise_name())){
                        exerciseList.add(new ExerciseRep(AllExercisesList.get(a)));
                    }

                }
            }


        return exerciseList;
    }


    private void addNewExercisesToAdapter(List<String> exercises){

        for (ExerciseRep exercise: findExerciseByName(exercises) ){
            mExercisesAdapter.add(exercise);
            adapter.notifyItemInserted(mExercisesAdapter.size());
        }

        setmMuscles(adapter.getSelectedExercises());
    }

    private void setmMuscles(List<ExerciseRep> exercises){
        Log.v(TAG,"setmMuscles true");
        mMuscles.clear();

        for (ExerciseRep exerciseRep: exercises){
            Log.v(TAG,"exercise: "+exerciseRep.getExercise().getExercise_name());
            for (Muscle muscle: exerciseRep.getExercise().getMuscles()){

                mMuscles.add(muscle.getMuscleName());
                Log.v(TAG,"muscle: "+muscle.getMuscleName());
            }
        }

        setMusclesToView(mMuscles);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 ) {
            if (resultCode == RESULT_OK && data != null){

                    if (data.hasExtra("exercises")) {
                        exercises_id = data.getStringArrayListExtra("exercises");

                        if (exercises_id.size() > 0){
                            putExercisesinRecycler(exercises_id);

                        }
                    }



            }

        }

    }


    
    private void setMusclesToView(List<String> musculos){
        Log.v(TAG,"setMusclesToView: "+musculos);

        partes = "";

        if (musculos.size() > 0){

            
            List<String> mMusclesFinal = utilities.deleteCopiesofList(musculos);
            

            for (int a = 0;a<mMusclesFinal.size();a++) {
                TextView mMusclesTextView = new TextView(this);

                partes += "#"+ mMusclesFinal.get(a) + ",";

                Log.v(TAG,"partes:" +partes);

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

        webView.reload();
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                utilities.injectJS(partes,webView);
                super.onPageFinished(view, url);
            }
        });

        //webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setJavaScriptEnabled(true);
        getBodyView();
    }



    private void getBodyView(){

        
        // ACCEDER A LA URL DEL HTML GUARDADO EN EL PHONE
        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);
        if (muscle_url.equals("/")){
            webView.loadUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/html/index.html");
        }else {
            String fileurl = "file://"+muscle_url;
            webView.loadUrl(fileurl);
        }
    }

    private void setEmptyContentOff(){
        empty_content.setVisibility(View.GONE);
        mButtonReAdd.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.VISIBLE);
    }


    private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {super.onStop();}

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {mItemTouchHelper.startDrag(viewHolder);}


    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
        ProgressView.setVisibility(View.GONE);
    }
    private void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);

    }
    private void onProgressOn(){
        mErrorView.setVisibility(View.GONE);
        ProgressView.setVisibility(View.VISIBLE);
    }

    private void onProgressOff(){
        mErrorView.setVisibility(View.GONE);
        ProgressView.setVisibility(View.GONE);
    }

}
