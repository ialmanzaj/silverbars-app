package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExercisesSelectedAdapter;
import com.app.app.silverbarsapp.components.DaggerCreateWorkoutComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.modules.CreateWorkoutModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.CreateWorkoutPresenter;
import com.app.app.silverbarsapp.utils.OnStartDragListener;
import com.app.app.silverbarsapp.utils.SimpleItemTouchHelperCallback;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class CreateWorkoutActivity extends BaseActivity implements CreateWorkoutView,OnStartDragListener {

    private static final String TAG = CreateWorkoutActivity.class.getSimpleName();

    private static final int FINAL_CREATE_WORKOUT = 2;
    private static final int LIST_EXERCISES_SELECTION = 1;
    

    @Inject
    CreateWorkoutPresenter mCreateWorkoutPresenter;

    
    private Utilities utilities = new Utilities();


    @BindView(R.id.toolbar) Toolbar toolbar;


    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReloadbutton;
    @BindView(R.id.progress_view) LinearLayout ProgressView;
    

    @BindView(R.id.content_empty) LinearLayout mEmptyView;
    @BindView(R.id.webview) WebView webView;
    
   
    @BindView(R.id.readd) Button mButtonReAdd;
    @BindView(R.id.next) Button mNextbutton;
    @BindView(R.id.add_exercises) Button mAddExercise;


    @BindView(R.id.content_info) LinearLayout contentView;
    @BindView(R.id.column1) LinearLayout PrimMuscleColumn;
    @BindView(R.id.column2) LinearLayout SecMuscleColumn;
    
    @BindView(R.id.exercises_selected) RecyclerView mExercisesSelectedList;
    
    
    private ExercisesSelectedAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    private String muscles = "";

    private ArrayList<Exercise> mExercisesAllList;
    private ArrayList<Integer> mListExercisesSelectedids;


    //private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

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
        setupWebview();


        utilities.setBodyInWebView(this,webView);
    }

    private void setupWebview(){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                utilities.injectJS(muscles,webView);
                super.onPageFinished(view, url);
            }
        });

        //webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setJavaScriptEnabled(true);


        utilities.setBodyInWebView(this,webView);
    }

    private void setupAdapter(){

        mExercisesSelectedList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExercisesSelectedAdapter(this,this);

        mExercisesSelectedList.setAdapter(adapter);

        //touch listener
        mItemTouchHelper  = new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(mExercisesSelectedList);
    }


    public void setupToolbar(){
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.text_create_workout));
            toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
        }
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


    @OnClick(R.id.reload)
    public void reload(){
          /* if (mListExercisesSelectedids.size() > 0){
                onErrorViewOff();
                onProgressOn();
                setExercisesAdapter(mListExercisesSelectedids);
            }
            */
    }

    @OnClick(R.id.next)
    public void next(){

        if (adapter.getItemCount() < 1) {
            Toast.makeText(this, getResources().getString(R.string.exercises_no_selected), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CreateWorkoutFinalActivity.class);
        intent.putParcelableArrayListExtra("exercises", mExercisesAllList);
        intent.putParcelableArrayListExtra("exercises_selected", adapter.getSelectedExercises());
        startActivityForResult(intent,FINAL_CREATE_WORKOUT);
    }



    @OnClick(R.id.add_exercises)
    public void addExercise(){
        Intent intent = new Intent(this,ExerciseListActivity.class);
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }

    @OnClick(R.id.readd)
    public void readExercise(){
        Intent intent = new Intent(this,ExerciseListActivity.class);
        Log.v(TAG,"exercises"+adapter.getSelectedExercisesName());
        intent.putExtra("exercises",adapter.getSelectedExercisesName());
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIST_EXERCISES_SELECTION) {
            if (resultCode == RESULT_OK){
                if (data.hasExtra("exercises") && data.hasExtra("exercises_selected")) {

                    mExercisesAllList =  data.getParcelableArrayListExtra("exercises");
                    mListExercisesSelectedids = data.getIntegerArrayListExtra("exercises_selected");

                    setExercisesAdapter(mExercisesAllList,mListExercisesSelectedids);
                }
            }

        } else if (requestCode == FINAL_CREATE_WORKOUT){
            if (resultCode == RESULT_OK ){
                finish();
            }
        }
    }

    @Override
    public void displayExercises(List<Exercise> exercises) {
        mExercisesAllList.addAll(exercises);
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }


    private void setExercisesAdapter(List<Exercise> list_exercises,List<Integer> list_exercises_id){
        onEmptyViewOff();

        List<String> muscles_names = new ArrayList<>();
        List<String> types_of_exercise = new ArrayList<>();

        for (Exercise exercise: getExercisesSelected(list_exercises,list_exercises_id) ){

            adapter.add(exercise);

            //add muscles array
            for (MuscleExercise muscle: exercise.getMuscles()){
                muscles_names.add(muscle.getMuscle());
            }

            //add types array
            for (String type: exercise.getType_exercise()){
                types_of_exercise.add(type);
            }

        }

        setMusclesToView(muscles_names);
        //setTypes(types_of_exercise);

        //adapter.setOnDataChangeListener(size -> setMuscles(adapter.getSelectedExercises()));
    }
/*
    private void setTypes(List<String> types){
        List<String> typesExercise  = utilities.deleteCopiesofList(types);
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
    }*/

    private List<Exercise> getExercisesSelected(List<Exercise> all_exercises_list, List<Integer> exercises_id){
        List<Exercise> exerciseList = new ArrayList<>();
        for (Integer exercise_id: exercises_id){
            exerciseList.add(getExerciseById(all_exercises_list,exercise_id));
        }
        return exerciseList;
    }

    private Exercise getExerciseById(List<Exercise> exercises,int exercise_id){
        for (Exercise exercise: exercises){
            if (exercise.getId() == exercise_id){
                return exercise;
            }
        }
        return null;
    }

    private void setMusclesToView(List<String> musculos){
        //Log.v(TAG,"setMusclesToView: "+musculos);
        if (musculos.size() < 0){
            return;
        }


        muscles = "";
        List<String> mMusclesFinal = utilities.deleteCopiesofList(musculos);


        for (int a = 0;a<mMusclesFinal.size();a++) {
            TextView mMusclesTextView = new TextView(this);

            muscles += "#"+ mMusclesFinal.get(a) + ",";

            //Log.v(TAG,"muscles:" +muscles);

            mMusclesTextView.setText(mMusclesFinal.get(a));
            mMusclesTextView.setGravity(Gravity.CENTER);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mMusclesTextView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));
            }else {mMusclesTextView.setTextColor(getResources().getColor(R.color.gray_active_icon));}

            if (a%2 == 0){
                //SecMuscleColumn.addView(mMusclesTextView);
            }else {
                //PrimMuscleColumn.addView(mMusclesTextView);
            }

        }


        utilities.injectJS(muscles,webView);
    }


    private void onEmptyViewOff(){
        mEmptyView.setVisibility(View.GONE);
        mButtonReAdd.setVisibility(View.VISIBLE);
        mExercisesSelectedList.setVisibility(View.VISIBLE);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
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
