package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerMuscleSelectionComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.modules.MuscleSelectionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.MuscleSelectionPresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.MusclesWebviewHandler;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.utils.WebAppInterface;
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
    @BindView(R.id.search_exercises) Button mLookExercises;

    @BindView(R.id.muscles_selected)TextView mMusclesTextSelected;

    @BindView(R.id.scroll_text) HorizontalScrollView mScrollText;

    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;

    @BindView(R.id.modal_overlay) LinearLayout mModal;
    @BindView(R.id.info) ImageView mInfo;


    private Utilities utilities = new Utilities();

    MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();

    private ArrayList<String> muscles_selected = new ArrayList<>();
    private ArrayList<Integer> exercises_ids_selected;

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
        setupToolbar();
        setupWebview();
        mMuscleSelectionPresenter.getMuscles();

        if (getIntent().getExtras() != null) {getExtras(getIntent().getExtras());}
    }

    private void getExtras(Bundle extras){
        exercises_ids_selected = extras.getIntegerArrayList("exercises_selected");
    }

    private void setupWebview(){
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this,this), "Android");
        utilities.loadUrlOfMuscleBody(this,myWebView);
    }

    public void setupToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_muscle_selection_toolbar);
        }
    }

    @OnClick(R.id.info)
    public void infoButton(){
        mModal.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.okay)
    public void okayButton(){
        mModal.setVisibility(View.GONE);
    }


    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mMuscleSelectionPresenter.getMuscles();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                if (data.hasExtra("exercises_selected")) {

                    ArrayList<Exercise>  exercises = data.getParcelableArrayListExtra("exercises_selected");

                    Intent return_intent = new Intent();
                    return_intent.putExtra("exercises_selected",exercises);
                    setResult(RESULT_OK, return_intent);
                    finish();
                }
            }
        }
    }


    @OnClick(R.id.search_exercises)
    public void searchExercisesButton(){

        if (muscles_selected.size() < 1){
            utilities.toast(this,getString(R.string.activity_muscle_selection_selection));
            return;
        }

        if (exercises_ids_selected != null){
            startExerciseList(true);
        }else {
            startExerciseList(false);
        }
    }

    private void startExerciseList(boolean exist_exercise_selected){
        Intent intent = new Intent(this, ExerciseListActivity.class);
        if (exist_exercise_selected){
            intent.putExtra("exercises",exercises_ids_selected);
        }
        intent.putExtra("muscles",muscles_selected);
        startActivityForResult(intent,1);
    }

    @Override
    public void displayNetworkError() {
        Log.e(TAG,"displayNetworkError");
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        Log.e(TAG,"displayServerError");
        onErrorViewOn();
    }

    @Override
    public void getMuscles(List<Muscle> muscles) {
        //Log.d(TAG,"muscles "+muscles);
        onLoadingViewOff();

        List<String> muscles_names = new ArrayList<>();

        for (Muscle muscle: muscles){
            muscles_names.add(muscle.getMuscle_name());
        }

        String muscles_for_js = mMusclesWebviewHandler.getMusclesReadyForWebview(muscles_names);

        myWebView.post(() -> {
            mMusclesWebviewHandler.paintOnClick(muscles_for_js);
            mMusclesWebviewHandler.execute(myWebView);
        });

    }

    @Override
    public void onMuscleSelected(String muscle) {
        if (!muscles_selected.contains(muscle)){

            //add muscle
            muscles_selected.add(muscle);

            runOnUiThread(() -> {
                //stuff that updates ui
                mMusclesTextSelected.setText(muscles_selected.toString().replace("[","").replace("]",""));
            });

            mScrollText.postDelayed(() -> mScrollText.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100L);


        }else {
            //Log.d(TAG,"muscle selected: "+muscle);

            String muscles_for_js = mMusclesWebviewHandler.getMuscleReadyForWebview(muscle);

            myWebView.post(() -> {
                mMusclesWebviewHandler.removePaint(muscles_for_js);
                mMusclesWebviewHandler.execute(myWebView);
            });

            //remove muscle
            muscles_selected.remove(muscle);


            runOnUiThread(() -> {
                //stuff that updates ui
                mMusclesTextSelected.setText(muscles_selected.toString().replace("[","").replace("]",""));
            });

            mScrollText.postDelayed(() -> mScrollText.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100L);
        }
    }

    private void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){mErrorView.setVisibility(View.VISIBLE);}

    private void onErrorViewOff(){mErrorView.setVisibility(View.GONE);}

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

