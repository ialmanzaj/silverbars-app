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

import static com.app.proj.silverbars.Utilities.deleteCopiesofList;
import static com.app.proj.silverbars.Utilities.injectJS;


public class CreateWorkoutActivity extends AppCompatActivity implements OnStartDragListener {

    private static final String TAG = "CreateWorkoutActivity" ;
    private WebView webView;
    private RecyclerView recycler;
    private RecyclerExerciseSelectedAdapter adapter;
    private Button re_addExercise;

    private LinearLayout empty_content;
    private ItemTouchHelper mItemTouchHelper;

    private  List<String> mMuscles = new ArrayList<>();

    private String partes = "";
    
    private List<ExerciseRep> ExercisesToAdapter = new ArrayList<>();

    private LinearLayout primary_ColumnMuscle,secundary_ColumnMuscle,Progress,progressLayout,error_layout;
    LinearLayout contentInfo;

    ArrayList<String> exercises_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.text_create_workout));
            toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        contentInfo = (LinearLayout)findViewById(R.id.content_info);
        primary_ColumnMuscle = (LinearLayout) findViewById(R.id.column1);
        secundary_ColumnMuscle = (LinearLayout) findViewById(R.id.column2);

        empty_content = (LinearLayout) findViewById(R.id.content_empty);

        ScrollView scrollView = (ScrollView) findViewById(R.id.muscles_);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            scrollView.setFillViewport(true);
        }


        progressLayout = (LinearLayout) findViewById(R.id.progress_bar_);
        error_layout = (LinearLayout) findViewById(R.id.error_layout);

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
                if ( adapter != null ){

                    if (adapter.getItemCount() > 0){

                        Intent intent = new Intent(CreateWorkoutActivity.this, CreateWorkoutFinalActivity.class);
                        intent.putExtra("exercises", adapter.getSelectedExercisesName());
                        intent.putExtra("reps",adapter.getExerciseReps());
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
        re_addExercise = (Button) findViewById(R.id.reAdd);
        re_addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);
                intent.putExtra("exercises",adapter.getSelectedExercisesName() );
                Log.v(TAG,"exercises: "+adapter.getSelectedExercisesName() );
                startActivityForResult(intent,2);
            }
        });


        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.recycler_exercises_selected);

        recycler.setLayoutManager( new LinearLayoutManager(this) );


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

    private void putExercisesinRecycler(final ArrayList<String> new_items_to_list){
        //Log.v(TAG,"putExercisesinRecycler: "+new_items_to_list);

        setEmptyContentOff();

        TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(this);
        SilverbarsService service = ServiceGenerator.createService(SilverbarsService.class,tokenAuthenticator.getToken());

        service.getAllExercises().enqueue(new Callback<Exercise[]>() {
            @Override
            public void onResponse(Call<Exercise[]> call, Response<Exercise[]> response) {
                if (response.isSuccessful()){
                    onProgressOff();
                    onErrorViewOff();

                    List<Exercise> AllExercisesList = new ArrayList<Exercise>();
                    Collections.addAll(AllExercisesList,response.body());

                    for (int c = 0;c < new_items_to_list.size();c++){
                        for (int a = 0; a < AllExercisesList.size(); a++){

                            // si el item seleccionado esta en  la lista principal agregalo
                            //Log.v(TAG,""+AllExercisesList.get(a).getExercise_name()+" : "+new_items_to_list.get(c));
                            if (Objects.equals(AllExercisesList.get(a).getExercise_name(), new_items_to_list.get(c))){
                                //.v(TAG,""+AllExercisesList.get(a).getExercise_name()+":"+exercises_ids_from_activity.get(c));
                                if (adapter == null){

                                    ExercisesToAdapter.add(new ExerciseRep(AllExercisesList.get(a)));

                                }else {

                                    ExercisesToAdapter.add(new ExerciseRep(AllExercisesList.get(a)));
                                    //Log.v(TAG,"ITEM INSERTED:"+(ExercisesToAdapter.size()));
                                    adapter.notifyItemInserted(ExercisesToAdapter.size());

                                }

                                //Collections.addAll(TypeExercises,ExercisesToAdapter.get(a).getTypes_exercise());
                                //Log.v(TAG,"TypeExercises"+TypeExercises);

                                for (int b = 0; b < ExercisesToAdapter.get(c).getExercise().getMuscles().length; b++){
                                    String name;
                                    name = ExercisesToAdapter.get(c).getExercise().getMuscles()[b].getMuscleName();
                                    mMuscles.add(name);
                                }
                            }
                        }
                    }


                    adapter = new RecyclerExerciseSelectedAdapter(CreateWorkoutActivity.this,ExercisesToAdapter,CreateWorkoutActivity.this);
                    recycler.setAdapter( adapter );

                    setMusclesToView(mMuscles);
                    //putTypesInWorkout(TypeExercises);

                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                    mItemTouchHelper  = new ItemTouchHelper(callback);
                    mItemTouchHelper.attachToRecyclerView(recycler);

                }
            }

            @Override
            public void onFailure(Call<Exercise[]> call, Throwable t) {
                onErrorViewOn();

            }
        });

    }

    private void onErrorViewOn(){
        error_layout.setVisibility(View.VISIBLE);
        Progress.setVisibility(View.GONE);
    }
    private void onErrorViewOff(){
        error_layout.setVisibility(View.GONE);

    }
    private void onProgressOn(){
        error_layout.setVisibility(View.GONE);
        Progress.setVisibility(View.VISIBLE);
    }

    private void onProgressOff(){
        error_layout.setVisibility(View.GONE);
        Progress.setVisibility(View.GONE);
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

        }else if (requestCode == 2 ) {
            Log.v(TAG, "requestCode: 2");
            if (resultCode == RESULT_OK && data != null){

                if (data.hasExtra("exercises")) {
                    exercises_id = data.getStringArrayListExtra("exercises");
                    putExercisesinRecycler(exercises_id);
                }




            }
        }
    }



    @SuppressLint("SetJavaScriptEnabled")
    private void setMusclesToView(List<String> musculos){
        if (musculos.size() > 0){
            List<String> musculos_oficial;
            musculos_oficial = deleteCopiesofList(musculos);
            for (int a = 0;a<musculos_oficial.size();a++) {
                final TextView MuscleTextView = new TextView(this);

                partes += "#"+ musculos_oficial.get(a) + ",";
                MuscleTextView.setText(musculos_oficial.get(a));
                MuscleTextView.setGravity(Gravity.CENTER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MuscleTextView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));
                }else {
                    MuscleTextView.setTextColor(getResources().getColor(R.color.gray_active_icon));
                }

                if (a%2 == 0){
                    //secundary_ColumnMuscle.addView(MuscleTextView);
                }else {
                    //primary_ColumnMuscle.addView(MuscleTextView);
                }
            }
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJS(partes,webView);
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
        re_addExercise.setVisibility(View.VISIBLE);
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
        typesExercise = deleteCopiesofList(types);
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

            //contentInfo.addView(linear);

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
}
