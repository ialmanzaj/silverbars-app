package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.app.silverbarsapp.OnItemSelectedListener;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.components.DaggerProgressionComponent;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.OnClick;


public class MyProgressFragment extends BaseFragment implements ProgressionView {

    private static final String TAG = MyProgressFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.start) Button startbutton;

    @BindView(R.id.toggle_progress) ToggleSwitch mToggleProgressView;

    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.muscles_content) LinearLayout mMusclesContentView;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout mSwipeRefreshLayout;


    private Utilities mUtilities = new Utilities();

    List<MuscleProgression> mProgressions = new ArrayList<>();
    List<MuscleProgression> mTodayProgressions = new ArrayList<>();
    List<MuscleProgression> mTotalProgressions = new ArrayList<>();

    String mMuscleParts = " ";

    private int mProgressionSelection = 0;
    private int mLastMusleId;


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_my_progress;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
         DaggerProgressionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .progressionModule(new ProgressionModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.isAdded()){
            setupWebview();
        }

        mProgressionPresenter.getProgression();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mProgressionPresenter.getProgression();
            mProgressions.clear();
            mTodayProgressions.clear();
            mTotalProgressions.clear();
        });

        mToggleProgressView.setOnToggleSwitchChangeListener((position, isChecked) -> {
            mProgressionSelection = position;
            updateUi(position);
        });
    }

    private void setProgress(){
        for (MuscleProgression progression: mProgressions){
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTime dateTime = formatter.parseDateTime(progression.getDate());

            Interval today = new Interval(DateTime.now(), Days.ONE);

            if (today.contains(dateTime)){
                mTodayProgressions.add(progression);
            }else {
                mTotalProgressions.add(progression);
            }
        }

        //init the UI
        updateUi(mProgressionSelection);
    }

    @OnClick(R.id.start)
    public void startWorkout(){
        try{
            //redirect to the other fragment(my matches with the argument offer).
            ((OnItemSelectedListener)getActivity()).onChangeNavigation(1);
        }catch (ClassCastException cce){
            Log.e(TAG,"ClassCastException",cce);
        }
    }

    private void updateUi(int position){
        if (position == 0){
            Log.d(TAG,"mTodayProgressions: "+mTodayProgressions.size());

            if (mTodayProgressions.size() > 0) {
                onEmptyViewOff();

                cleanMuscleContentView();
                changeProgressUI(mTodayProgressions);
            }else {
                onEmptyViewOn();
            }

        }else if (position == 1){
            Log.d(TAG,"mTotalProgressions: "+mTotalProgressions.size());

            if (mTotalProgressions.size() > 0){
                onEmptyViewOff();

                cleanMuscleContentView();
                changeProgressUI(mTotalProgressions);
            }else {
                onEmptyViewOn();
            }

        }
    }


    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        mUtilities.loadUrlOfMuscleBody(getActivity(),webView);
    }


    @Override
    public void emptyProgress() {
        onEmptyViewOn();
    }

    @Override
    public void displayNetworkError() {
        Log.d(TAG,"displayNetworkError");
    }

    @Override
    public void displayServerError() {
        Log.d(TAG,"displayServerError");
    }

    @Override
    public void displayProgressions(List<MuscleProgression> progressions) {
        Collections.reverse(progressions);
        mProgressions.addAll(progressions);
        getMusclesFromProgression();
    }

    @Override
    public void displayMuscle(Muscle muscle) {
        //Log.d(TAG,"displayMuscle: "+muscle);
        insertMuscleNamesInProgression(muscle);

        if (muscle.getId() == mLastMusleId){
            mSwipeRefreshLayout.setRefreshing(false);
            onLoadingViewOff();
            setProgress();
        }
    }


    private void insertMuscleNamesInProgression(Muscle muscle){
        for (MuscleProgression progression: mProgressions){
            if (progression.getMuscle_id() == muscle.getId()){
                progression.setMuscle(muscle);
            }
        }
    }

    private void changeProgressUI(List<MuscleProgression> progression){
        for (MuscleProgression muscleProgression: progression){
            createProgressionView(muscleProgression);
            insertMuscleToWebview(muscleProgression.getMuscle().getMuscle_name());
        }
    }


    private void createProgressionView(MuscleProgression muscleProgression){
        RelativeLayout relativeLayout = mUtilities.createProgressionView(CONTEXT, muscleProgression.getMuscle().getMuscle_name(),String.valueOf(muscleProgression.getLevel()), muscleProgression.getMuscle_activation_progress());
        mMusclesContentView.addView(relativeLayout);
    }

    private void insertMuscleToWebview(String muscle_name){
        //add muscle names to the webview
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.injectJS(webView,mMuscleParts);
    }


    private void getMusclesFromProgression(){
        List<String> looking_muscle = new ArrayList<>();
        for (MuscleProgression muscleProgression: mProgressions){

            String muscle_id = String.valueOf(muscleProgression.getMuscle_id());

            if (!looking_muscle.contains(muscle_id)){
                looking_muscle.add(muscle_id);

                //get  this muscle to the api
                mProgressionPresenter.getMuscle(muscleProgression.getMuscle_id());
            }
        }

        mLastMusleId = Integer.parseInt(looking_muscle.get(looking_muscle.size()-1));
    }

    private void onEmptyViewOn(){mEmptyView.setVisibility(View.VISIBLE);}

    private void onEmptyViewOff(){mEmptyView.setVisibility(View.GONE);}

    private void cleanMuscleContentView(){
        mMuscleParts = " ";
        mMusclesContentView.removeAllViewsInLayout();
    }

    private void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

}
