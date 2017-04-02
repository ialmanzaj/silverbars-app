package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ProgressionAdapter;
import com.app.app.silverbarsapp.components.DaggerTotalProgressionComponent;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TotalProgressFragment extends BaseFragment implements ProgressionView {

    private static final String TAG = WeeklyProgressFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;


    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.start) Button startButton;

    //@BindView(R.id.toggle_progress) ToggleSwitch mToggleProgressView;

    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.muscles_content) RecyclerView list;

    @BindView(R.id.loading) LinearLayout mLoadingView;


    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;


    String mMuscleParts = " ";
    ProgressionAdapter adapter;

    List<MuscleProgression> mTotalProgressions = new ArrayList<>();
    List<MuscleProgression> mProgressions = new ArrayList<>();

    private int mLastMusleId;

    private Utilities mUtilities = new Utilities();


    @Override
    protected int getFragmentLayout() {
        return R.layout.total_progress;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
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

        if (this.isAdded()) {

            //setupWebview();
        }
        //mProgressionPresenter.getProgression();

    }

    private void setupAdapter(){
        //list settings
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setNestedScrollingEnabled(false);

        adapter = new ProgressionAdapter(getActivity());
        list.setAdapter(adapter);
    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        mUtilities.loadUrlOfMuscleBody(getActivity(),webView);
    }

    private void setProgress(){
        for (MuscleProgression progression: mProgressions){
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

            mTotalProgressions.add(progression);
        }

        //init the UI
        updateUi();
    }

    private void changeProgressUI(List<MuscleProgression> progression){
        for (MuscleProgression muscleProgression: progression){

            //ADD each progression to a listview
            adapter.add(muscleProgression);

            Log.d(TAG,"muscleProgression with muscle id: "+muscleProgression.getMuscle_id());
            insertMuscleToWebview(muscleProgression.getMuscle().getMuscle_name());
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        //add muscle names to the webview
        mMuscleParts += "#" + muscle_name + ",";
        //mUtilities.injectJS(webView,mMuscleParts);
    }

    private void updateUi(){
        Log.d(TAG,"updateUi");

        if (mTotalProgressions.size() > 0){
              onEmptyViewOff();


            changeProgressUI(mTotalProgressions);

        }else {
            onEmptyViewOn("You don't have any training done yet");
        }
    }


    @Override
    public void emptyProgress() {
        onLoadingViewOff();
        //update the ui
        updateUi();
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
    public void displayProgressions(List<MuscleProgression> progressions) {
        Log.d(TAG,"displayProgressions");
        Collections.reverse(progressions);

        if (progressions.size() > mProgressions.size()) {
            mProgressions.addAll(progressions);
            getMusclesFromProgression();
        }
    }

    @Override
    public void displayMuscle(Muscle muscle) {
        Log.d(TAG,"displayMuscle: "+muscle.getMuscle_name());

        insertMuscleNamesInProgression(muscle);

        if (muscle.getId() == mLastMusleId){
            onLoadingViewOff();
            setProgress();
        }
    }



    private void insertMuscleNamesInProgression(Muscle muscle){
        Log.d(TAG,"insertMuscleNamesInProgression");

        for (MuscleProgression progression: mProgressions){
            if (progression.getMuscle_id() == muscle.getId()){

                Log.d(TAG,"progression with muscle id: "+progression.getMuscle_id());
                progression.setMuscle(muscle);
            }
        }
    }



    private void getMusclesFromProgression(){
        List<String> muscles_ids = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        for (MuscleProgression muscleProgression: mProgressions){
            String muscle_id = String.valueOf(muscleProgression.getMuscle_id());
            if (!muscles_ids.contains(muscle_id)){
                muscles_ids.add(muscle_id);
                ids.add(Integer.valueOf(muscle_id));
            }
        }

        mProgressionPresenter.getMuscles(ids);
        mLastMusleId = Integer.parseInt(muscles_ids.get(muscles_ids.size()-1));
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
