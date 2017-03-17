package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.adapters.ExercisesSelectedAdapter;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.utils.OnStartDragListener;
import com.app.app.silverbarsapp.utils.SimpleItemTouchHelperCallback;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CreateWorkoutActivity extends AppCompatActivity implements OnStartDragListener, ExercisesSelectedAdapter.OnExerciseListener {

    private static final String TAG = CreateWorkoutActivity.class.getSimpleName();

    private static final int FINAL_CREATE_WORKOUT = 2;
    private static final int LIST_EXERCISES_SELECTION = 1;


    private Utilities utilities = new Utilities();

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.content) LinearLayout mMainContentLayout;

    @BindView(R.id.content_empty) LinearLayout mEmptyView;
    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.readd) Button mReAddButton;
    @BindView(R.id.next) Button mNextbutton;
    @BindView(R.id.add_exercises) Button mAddExerciseButton;


    @BindView(R.id.exercises_selected) RecyclerView mExercisesSelectedList;


    private ExercisesSelectedAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    private ArrayList<Exercise> mExercisesAllList;

    private String sMusclesBodyView = "";

    //private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);
        ButterKnife.bind(this);



        setupToolbar();
        setupTabs();
        setupAdapter();
        setupWebview();
    }

    private void setupToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
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


    private void setupWebview(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        utilities.loadUrlOfMuscleBody(this,webView);
    }


    private void setupAdapter(){
        mExercisesSelectedList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExercisesSelectedAdapter(this,this);

        //listener to know what happen in the adapter
        adapter.setOnDataChangeListener(this);

        mExercisesSelectedList.setAdapter(adapter);

        //touch listener
        mItemTouchHelper  = new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(mExercisesSelectedList);
    }

    @OnClick(R.id.next)
    public void nextButton(){

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
    public void addExerciseButton(){
        Intent intent = new Intent(this,ExerciseListActivity.class);
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }

    @OnClick(R.id.readd)
    public void readExerciseButton(){
        Intent intent = new Intent(this,ExerciseListActivity.class);
        intent.putExtra("exercises",utilities.getExercisesIds(adapter.getSelectedExercises()));
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIST_EXERCISES_SELECTION) {
            if (resultCode == RESULT_OK){
                if (data.hasExtra("exercises") && data.hasExtra("exercises_selected")) {

                    mExercisesAllList =  data.getParcelableArrayListExtra("exercises");
                    ArrayList<Integer> mExercisesSelectedListIds = data.getIntegerArrayListExtra("exercises_selected");

                    setExercisesAdapter(mExercisesAllList, mExercisesSelectedListIds);
                }
            }

        } else if (requestCode == FINAL_CREATE_WORKOUT){
            if (resultCode == RESULT_OK ){
                setResult(RESULT_OK, new Intent());
                finish();
            }
        }
    }

    @Override
    public void onExerciseDeleted(Exercise exercise,int position) {
        Log.d(TAG,"onExerciseDeleted");

        //show snackbar of exercise deleted
        Snackbar.make(mMainContentLayout, "Exercise deleted", Snackbar.LENGTH_LONG).setAction("UNDO", view -> {

            //re add element to the adapter
            adapter.insert(position,exercise);

            //update muscle view
            updateMusclesView();

            //show again the snackbar of restored
            Snackbar snackbar_restored = Snackbar.make(mMainContentLayout, "Exercise is restored!", Snackbar.LENGTH_SHORT);
            snackbar_restored.show();

        }).show();
    }

    @Override
    public void onUpdateMuscleView() {
        Log.d(TAG,"onUpdateMuscleView");
        updateMusclesView();
    }

    private void updateMusclesView(){
        updateMuscleView(utilities.getMusclesFromExercises(adapter.getSelectedExercises()));
        reloadWebview();
    }

    private void setExercisesAdapter(List<Exercise> list_all_exercises,List<Integer> list_exercises_id){
        //empty view off
        onEmptyViewOff();

        //set exercises in adapter
        adapter.setExercises(utilities.getExercisesById(list_all_exercises,list_exercises_id));

        //updated muscles view
        updateMuscleView(utilities.getMusclesFromExercises(utilities.getExercisesById(list_all_exercises,list_exercises_id)));
        utilities.injectJS(webView,sMusclesBodyView);
    }

    private void reloadWebview(){
        webView.reload();
        utilities.loadUrlOfMuscleBody(this,webView);
        utilities.onWebviewReady(webView,sMusclesBodyView);
    }

    private void updateMuscleView(List<String> musculos){
        //Log.d(TAG,"muscle size "+musculos.size());
        if (musculos.size() < 1){
            sMusclesBodyView = " ";
        }else {
            sMusclesBodyView = utilities.getMusclesReadyForWebview(utilities.deleteCopiesofList(musculos));
        }
    }



    private void onEmptyViewOff(){
        mEmptyView.setVisibility(View.GONE);
        mReAddButton.setVisibility(View.VISIBLE);
        mExercisesSelectedList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {mItemTouchHelper.startDrag(viewHolder);}

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
