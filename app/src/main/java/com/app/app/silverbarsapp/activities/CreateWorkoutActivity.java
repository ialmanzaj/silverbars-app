package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.adapters.ExercisesSelectedAdapter;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.handlers.MusclesWebviewHandler;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.utils.OnStartDragListener;
import com.app.app.silverbarsapp.utils.SimpleItemTouchHelperCallback;
import com.app.app.silverbarsapp.utils.Utilities;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;


public class CreateWorkoutActivity extends BaseActivity implements OnStartDragListener, ExercisesSelectedAdapter.OnExerciseListener {

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

    private Utilities utilities = new Utilities();
    private Filter filter = new Filter();
    private MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();

    private ExercisesSelectedAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected int getLayout() {
        return R.layout.activity_create_workout;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

        //mix panel events
        eventCreateWorkout();
    }

    private void initUI(){
        setupToolbar();
        setupTabs();
        setupExercisesAdapter();
        setupWebview();
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


    /**
     *
     *
     *    Click listeners
     *
     *
     *
     */

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
        Intent intent = new Intent(this,ExerciseListActivity.class);
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }

    @OnClick(R.id.readd)
    public void readAddExerciseButton(){
        Intent intent = new Intent(this,ExerciseListActivity.class);
        intent.putExtra("exercises",filter.getExercisesIds(adapter.getSelectedExercises()));
        startActivityForResult(intent,LIST_EXERCISES_SELECTION);
    }


    /**
     *
     *
     *
     *
     * Muscle events
     *
     */

    @Override
    public void onUpdateMuscleView() {
        updateMusclesView();
    }


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

    private String getMusclesReady(List<String> musculos){
        //Log.d(TAG,"muscle size "+musculos.size());
        String muscles_js;
        if (musculos.size() < 1){
            muscles_js = " ";
        }else {
            muscles_js = mMusclesWebviewHandler.getMusclesReadyForWebview(utilities.deleteCopiesofList(musculos));
        }
        return muscles_js;
    }

    /**
     *
     *
     *
     *     UI events
     *
     *
     *
     *
     */

    public void setupToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.activity_create_workout_create_button));
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


    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        utilities.loadBodyFromLocal(this,webView);
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

    private void setExercisesAdapter(List<Exercise> exercises){
        //empty view off
        onEmptyViewOff();

        //set exercises in adapter
        adapter.setExercises(exercises);

        //update  body muscle of webview
        mMusclesWebviewHandler.paint(getMusclesReady(filter.getMusclesFromExercises(exercises)));
        mMusclesWebviewHandler.execute(webView);
    }

    private void updateMusclesView(){
        updateWebviewReady(getMusclesReady(filter.getMusclesFromExercises(adapter.getSelectedExercises())));
    }


    private void updateWebviewReady(String muscles){
        webView.reload();
        utilities.loadBodyFromLocal(this,webView);
        mMusclesWebviewHandler.addWebviewClientPaint(webView,muscles);
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
            //Log.d(TAG, "action bar clicked");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     *
     *
     *
     *    Mix panel events
     *
     *
     */

    private void eventCreateWorkout(){
        MixpanelAPI mMixpanel = MixpanelAPI.getInstance(this, MIX_PANEL_TOKEN);
        mMixpanel.track("Create Workout 1", utilities.getUserData(this));
    }

}
