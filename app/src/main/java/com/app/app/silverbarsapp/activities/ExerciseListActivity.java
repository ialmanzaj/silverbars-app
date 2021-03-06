package com.app.app.silverbarsapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ExercisesAllAdapter;
import com.app.app.silverbarsapp.adapters.FilterAdapter;
import com.app.app.silverbarsapp.components.DaggerExerciseListComponent;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.modules.ExerciseListModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ExerciseListPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ExerciseListView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseListActivity extends BaseActivityExtended implements ExerciseListView,FilterAdapter.onMuscleSelected {

    private static final String TAG = ExerciseListActivity.class.getSimpleName();

    @Inject
    ExerciseListPresenter mExerciseListPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.exercises_list) RecyclerView mExercisesList;
    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.filters) RecyclerView mMuscleFilters;

    private ExercisesAllAdapter adapter;

    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<Integer> mExercisesSelectedIds;

    private Filter filter = new Filter();

    FilterAdapter filterAdapter;
    MixpanelAPI mixpanel;

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
        setupToolbar();

        Bundle extras = getIntent().getExtras();
        if (extras!= null) getExtras(extras);

        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExerciseListPresenter.getExercises();

        setFilterAdapter();

        mixpanel = MixpanelAPI.getInstance(this, MIX_PANEL_TOKEN);

        mixpanelExerciseList();
    }

    private void getExtras(Bundle extras){
        if (extras.getIntegerArrayList("exercises") != null ){
            mExercisesSelectedIds = extras.getIntegerArrayList("exercises");
        }
    }

    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_exercises_title));
    }

    /**
     *
     *
     *    Click listeners
     *
     *
     *
     */



    @OnClick(R.id.reload)
    public void reloadButton(){
        onErrorViewOff();
        onLoadingViewOn();
        mExerciseListPresenter.getExercises();
    }

    @OnClick(R.id.add_exercises)
    public void addExercisesButton(){

        ArrayList<Integer> exercise_selected_ids = new ArrayList<>();

        for(int i = 0; i < adapter.getExercisesSelected().size(); i++) {
            Object exercise_is_selected = adapter.getExercisesSelected().valueAt(i);

            if ((Boolean)exercise_is_selected){
                int exercise_id = adapter.getExercisesSelected().keyAt(i);
                exercise_selected_ids.add(exercise_id);
            }
        }

        if (exercise_selected_ids.size() > 0) {
            mixpanelExercisesSelected();


            Intent return_intent = new Intent();
            return_intent.putExtra("exercises_selected",filter.getExercisesById(mExercises, exercise_selected_ids));
            setResult(RESULT_OK, return_intent);
            finish();

        }else {
            new Utilities().toast(this,getString(R.string.activity_exercise_list_no_selection));
        }

    }


    /**
     *
     *
     *
     *    Events listening
     *
     *
     *
     *
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                if (data.hasExtra("muscles")) {

                    ArrayList<String> muscles_selected = data.getStringArrayListExtra("muscles");

                    for (String muscle: muscles_selected) {
                        if (!filterAdapter.getMuscles().contains(muscle)) {

                            //add muscles to filter
                            filterAdapter.add(muscle);
                        }
                    }

                    //move the recycler view to the last element
                    mMuscleFilters.postDelayed(() -> mMuscleFilters.smoothScrollToPosition(filterAdapter.getItemCount()-1),100L);

                    //filter exercises in list
                    setExercisesView(filter.filterExerciseByMuscle(filterAdapter.getMuscles(),mExercises));
                }
            }
        }
    }


    /**
     *
     *
     *
     *   API events
     *
     *
     *
     */

    @Override
    public void displayExercises(List<Exercise> exercises) {
        onLoadingViewOff();
        mExercises.addAll(exercises);
        if (mExercisesSelectedIds != null){
            setExercisesView(filter.getExercisesNoSelected(mExercisesSelectedIds,mExercises));
        }else {
            setExercisesView(mExercises);
        }
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
    }


    /**
     *
     *
     *   Filter events
     *
     *
     *
     */
    @Override
    public void onDeleteMuscle(int position) {
        if (filterAdapter.getItemCount() > 1) {
            //remove from list
            filterAdapter.delete(position);

            //move the recycler view to the last element
            mMuscleFilters.postDelayed(() -> mMuscleFilters.smoothScrollToPosition(filterAdapter.getItemCount() - 1), 100L);

            //filter exercises in list
            ArrayList<Exercise> exercises = filter.filterExerciseByMuscle(filterAdapter.getMuscles(), mExercises);
            setExercisesView(exercises.isEmpty() ? mExercises : exercises);
        }
    }

    /**
     *
     *
     *
     *     UI events
     *
     *
     *
     */

    private void setFilterAdapter(){
        filterAdapter = new FilterAdapter();
        filterAdapter.addListener(this);
        mMuscleFilters.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mMuscleFilters.setAdapter(filterAdapter);

    }

    private void setExercisesView(ArrayList<Exercise> exercises){
        if (exercises.size() > 0) {
            adapter = new ExercisesAllAdapter(exercises);
            mExercisesList.setAdapter(adapter);
        }else {
            onEmptyViewOn();
        }
    }


    private void onEmptyViewOn(){mEmptyView.setVisibility(View.VISIBLE);}

    private void onEmptyViewOff(){mEmptyView.setVisibility(View.GONE);}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.filter:
                mixpanelFilter();

                Intent intent = new Intent(this,MuscleSelectionActivity.class);
                startActivityForResult(intent,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     *
     *
     *    Mix panel events
     *
     *
     *
     */

    private void mixpanelExerciseList(){
        mixpanel.track("on Exercise list", new Utilities().getUserData(this));
    }

    private void mixpanelExercisesSelected(){
        mixpanel.track("on Exercises Selected", new Utilities().getUserData(this));
    }

    private void mixpanelFilter(){
        mixpanel.track("on Filter", new Utilities().getUserData(this));
    }

}