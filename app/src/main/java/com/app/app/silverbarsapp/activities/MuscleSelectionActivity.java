package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.utils.WebAppInterface;
import com.app.app.silverbarsapp.components.DaggerMuscleSelectionComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.modules.MuscleSelectionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.MuscleSelectionPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.MuscleSelectionView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MuscleSelectionActivity extends BaseActivity implements MuscleSelectionView,MuscleListener {

    private static final String TAG = MuscleSelectionActivity.class.getSimpleName();

    @Inject
    MuscleSelectionPresenter mMuscleSelectionPresenter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.webview) WebView myWebView;
    @BindView(R.id.look_exercises) Button mLookExercises;

    @BindView(R.id.muscles_selected)TextView mMusclesTextSelected;

    @BindView(R.id.scroll_text) HorizontalScrollView mScrollText;

    private Utilities utilities = new Utilities();

    private ArrayList<String> muscles_selected = new ArrayList<>();
    private ArrayList<Integer> exercises_selected;


    @Override
    protected int getLayout() {
        return R.layout.activity_muscle_selection;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerMuscleSelectionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .muscleSelectionModule(new MuscleSelectionModule(this))
                .build().inject(this);
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mMuscleSelectionPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {getExtras(getIntent().getExtras());}

        setupToolbar();
        setupWebview();

        mMuscleSelectionPresenter.getMuscles();
    }

    private void setupWebview(){
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface( new WebAppInterface(this,this), "Android");
        utilities.loadUrlOfMuscleBody(this,myWebView);
    }

    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_muscle_selection_toolbar);
    }

    private void getExtras(Bundle extras){
        exercises_selected = extras.getIntegerArrayList("exercises");
    }

    @OnClick(R.id.look_exercises)
    public void searchExercises(){

        if (muscles_selected.size() < 1){
            utilities.toast(this,getString(R.string.activity_muscle_selection_selection));
            return;
        }

        if (exercises_selected != null){
            startExerciseList(true);
        }else {
            startExerciseList(false);
        }

    }

    private void startExerciseList(boolean exist_exercise_selected){
        Intent intent = new Intent(this, ExerciseListActivity.class);
        if (exist_exercise_selected){
            intent.putExtra("exercises",exercises_selected);
        }

        intent.putExtra("muscles",muscles_selected);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                if (data.hasExtra("exercises") && data.hasExtra("exercises_selected")) {

                    ArrayList<Exercise> all_exercises = data.getParcelableArrayListExtra("exercises");
                    ArrayList<Integer> mExercisesSelectedListIds = data.getIntegerArrayListExtra("exercises_selected");


                    Intent return_intent = new Intent();
                    return_intent.putExtra("exercises",all_exercises);
                    return_intent.putExtra("exercises_selected",mExercisesSelectedListIds);
                    setResult(RESULT_OK, return_intent);
                    finish();
                }
            }
        }
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
    }

    @Override
    public void getMuscles(List<Muscle> muscles) {
        //Log.d(TAG,"muscles "+muscles);

        List<String> muscles_names = new ArrayList<>();

        for (Muscle muscle: muscles){
            muscles_names.add(muscle.getMuscle_name());
        }

        utilities.injectJSOnClick(myWebView,utilities.getMusclesReadyForWebview(muscles_names));
    }

    @Override
    public void onMuscleSelected(String muscle) {
        muscles_selected.add(muscle);

        runOnUiThread(() -> {
            //stuff that updates ui
            mMusclesTextSelected.setText(muscles_selected.toString().replace("[","").replace("]",""));
        });


        mScrollText.postDelayed(() -> mScrollText.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100L);
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

