package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.Filter;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
import com.app.app.silverbarsapp.components.DaggerTotalProgressionComponent;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.utils.WebAppInterface;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class ProgressTotalFragment extends BaseFragment implements ProgressionView,MuscleListener {


    private static final String TAG = ProgressTotalFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.webview) WebView webView;

    private Utilities mUtilities = new Utilities();
    Filter filter = new Filter();

    private String mMuscleParts = " ";

    ArrayList<ExerciseProgression> mProgressions = new ArrayList<>();

    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_progress_total;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerTotalProgressionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .progressionModule(new ProgressionModule(this))
                .build().inject(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupWebview();
        //mProgressionPresenter.getMuscleProgressions();
        mProgressionPresenter.getExerciseProgression();

    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface( new WebAppInterface(CONTEXT,this), "Android");
        mUtilities.loadUrlOfMuscleBody(CONTEXT,webView);
    }

    @Override
    public void onMuscleSelected(String muscle) {
        Intent intent = new Intent(CONTEXT, ExerciseDetailActivity.class);
        intent.putExtra("title", "Total");
        intent.putExtra("subtitle",muscle);
        intent.putExtra("exercises",filter.getProgressionFilteredByMuscle(mProgressions,muscle));
        startActivity(intent);
    }

    @Override
    public void emptyProgress() {
        onLoadingViewOff();
        onEmptyViewOn("You haven't train :(");
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
    public void displayProgressions(List<ExerciseProgression> progressions) {
        onLoadingViewOff();
        Collections.reverse(progressions);
        init(progressions);
        mProgressions.addAll(progressions);
    }

    private void init(List<ExerciseProgression> progressions){
        Log.d(TAG,"init");

        //filter the progressions
        updateUi(progressions);
    }

    private void updateUi(List<ExerciseProgression> progressions_filtered){
        Log.d(TAG,"updateUi: "+progressions_filtered.size());

        if (progressions_filtered.size() > 0) {
            Log.d(TAG,"YES update");
            onEmptyViewOff();
            clearWebview();
            updateBodyMuscleWebView(progressions_filtered);
        }else {
            Log.d(TAG,"empty");
            onEmptyViewOn("You haven't train :(");
        }
    }


    private void updateBodyMuscleWebView(List<ExerciseProgression> exerciseProgressions){
        for (ExerciseProgression exercise_progress: exerciseProgressions){
            for (String muscle: mUtilities.deleteCopiesofMuscles(exercise_progress.getExercise().getMuscles())) {
                insertMuscleToWebview(muscle);
            }
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.onWebviewClickReady(webView,mMuscleParts);
    }

    private void clearWebview(){
        webView.reload();
        mMuscleParts = " ";
    }


    private void onEmptyViewOn(String text){mEmptyView.setVisibility(View.VISIBLE);mEmptyText.setText(text);}

    private void onEmptyViewOff(){mEmptyView.setVisibility(View.GONE);}

    private void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    private void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);
    }


}