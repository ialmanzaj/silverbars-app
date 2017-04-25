package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.app.app.silverbarsapp.Filter;
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

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.content) LinearLayout mMainContentLayout;

    @BindView(R.id.content_empty) LinearLayout mEmptyView;
    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.readd) Button mReAddButton;

    @BindView(R.id.exercises_selected) RecyclerView mExercisesSelectedList;

    /*@BindView(R.id.skills)RecyclerView mSkillsList;
    @BindView(R.id.content_empty_types) LinearLayout mEmptyTypesView;

    private SkillAdapter skill_adapter;*/

    private ExercisesSelectedAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    private String mMusclesStringJs = " ";

    private Utilities utilities = new Utilities();
    private Filter filter = new Filter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);
        ButterKnife.bind(this);

        initUI();
    }

    private void initUI(){
        setupToolbar();
        setupTabs();
        setupExercisesAdapter();
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

        TabHost.TabSpec rutina = tabHost2.newTabSpec(getResources().getString(R.string.tab_workout));
        rutina.setIndicator(getResources().getString(R.string.tab_workout));
        rutina.setContent(R.id.workout);

        TabHost.TabSpec muscles = tabHost2.newTabSpec(getResources().getString(R.string.tab_muscles));
        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

      /*  TabHost.TabSpec skills = tabHost2.newTabSpec("Focus");
        skills.setIndicator("Focus");
        skills.setContent(R.id.types);
*/
        tabHost2.addTab(rutina);
        tabHost2.addTab(muscles);
        //tabHost2.addTab(skills);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIST_EXERCISES_SELECTION) {
            if (resultCode == RESULT_OK){
                if (data.hasExtra("exercises_selected")) {

                    ArrayList<Exercise>  exercises = data.getParcelableArrayListExtra("exercises_selected");
                    setExercisesAdapter(exercises);
                }
            }
        } else if (requestCode == FINAL_CREATE_WORKOUT){
            if (resultCode == RESULT_OK ){
                setResult(RESULT_OK, new Intent());
                finish();
            }
        }
    }

   /* private void setupAdapterSkills(List<Exercise> exercises){
        onEmptyTypesViewOff();

        //list settings
        mSkillsList.setLayoutManager(new LinearLayoutManager(this));
        mSkillsList.setNestedScrollingEnabled(false);
        mSkillsList.setHasFixedSize(false);
        skill_adapter = new SkillAdapter(utilities.getTypesByExercise(exercises));
        mSkillsList.setAdapter(skill_adapter);
    }*/

    private void setupWebview(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        utilities.loadUrlOfMuscleBody(this,webView);
    }

    private void setupExercisesAdapter(){
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
        intent.putParcelableArrayListExtra("exercises_selected", adapter.getSelectedExercises());
        startActivityForResult(intent,FINAL_CREATE_WORKOUT);
    }

    @OnClick(R.id.add_exercises)
    public void addExerciseButton(){
        Intent intent = new Intent(this,MuscleSelectionActivity.class);
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }

    @OnClick(R.id.readd)
    public void readAddExerciseButton(){
        Intent intent = new Intent(this,MuscleSelectionActivity.class);
        intent.putExtra("exercises",filter.getExercisesIds(adapter.getSelectedExercises()));
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }

    private void setExercisesAdapter(List<Exercise> exercises){
        //empty view off
        onEmptyViewOff();

        //set exercises in adapter
        //setupAdapterSkills(exercises);
        adapter.setExercises(exercises);

        //updated muscles view
        updateMuscleView(filter.getMusclesFromExercises(exercises));
        utilities.injectJS(webView,mMusclesStringJs);
    }

    @Override
    public void onUpdateMuscleView() {
        updateMusclesView();
        //updateTypes();
    }

    private void updateMusclesView(){
        updateMuscleView(filter.getMusclesFromExercises(adapter.getSelectedExercises()));
        reloadWebview();
    }

   /* private void updateTypes(){
        skill_adapter.set(utilities.getTypesByExercise(adapter.getSelectedExercises()));
    }*/

    @Override
    public void onExerciseDeleted(Exercise exercise,int position) {
        //show snackbar of exercise deleted
        Snackbar.make(mMainContentLayout, R.string.activity_create_workout_deleted, Snackbar.LENGTH_LONG).setAction
                (R.string.activity_create_workout_undo, view -> {

                    //re add element to the adapter
                    adapter.insert(position,exercise);
                    //update muscle view
                    updateMusclesView();

                    //show again the snackbar of restored
                    Snackbar snackbar_restored = Snackbar.make(mMainContentLayout, R.string.activity_create_workout_restored,
                            Snackbar.LENGTH_SHORT);
                    snackbar_restored.show();
        }).show();
    }

    private void updateMuscleView(List<String> musculos){
        //Log.d(TAG,"muscle size "+musculos.size());
        if (musculos.size() < 1){
            mMusclesStringJs = " ";
        }else {
            mMusclesStringJs = utilities.getMusclesReadyForWebview(utilities.deleteCopiesofList(musculos));
        }
    }

    private void reloadWebview(){
        webView.reload();
        utilities.loadUrlOfMuscleBody(this,webView);
        utilities.onWebviewReady(webView,mMusclesStringJs);
    }

    private void onEmptyViewOff(){
        mEmptyView.setVisibility(View.GONE);
        mReAddButton.setVisibility(View.VISIBLE);
        mExercisesSelectedList.setVisibility(View.VISIBLE);
    }

    /*private void onEmptyTypesViewOn(){
        mEmptyTypesView.setVisibility(View.VISIBLE);
    }

    private void onEmptyTypesViewOff(){
        mEmptyTypesView.setVisibility(View.GONE);
    }*/

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {mItemTouchHelper.startDrag(viewHolder);}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
