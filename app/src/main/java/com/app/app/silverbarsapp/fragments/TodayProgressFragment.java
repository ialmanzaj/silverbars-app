package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.ProgressionAdapter;
import com.app.app.silverbarsapp.components.DaggerTodayProgressionComponent;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.OnItemSelectedListener;
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

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayProgressFragment extends BaseFragment implements ProgressionView {

    private static final String TAG = TodayProgressFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;


    List<MuscleProgression> mProgressions = new ArrayList<>();
    List<MuscleProgression> mTodayProgressions = new ArrayList<>();

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;


    @BindView(R.id.start) Button startbutton;

    //@BindView(R.id.toggle_progress) ToggleSwitch mToggleProgressView;

    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.muscles_content) ListView mMusclesContentView;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;


    String mMuscleParts = " ";

    ProgressionAdapter adapter;

    private Utilities mUtilities = new Utilities();


    private int mLastMusleId;


    @Override
    protected int getFragmentLayout() {
        return R.layout.today_progress;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
    }


    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerTodayProgressionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .progressionModule(new ProgressionModule(this))
                .build().inject(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.isAdded()) {
            // Sets the data behind this ListView
            //adapter = new ProgressionAdapter(getActivity());
            //mMusclesContentView.setAdapter(adapter);
        }

        setupWebview();

       mProgressionPresenter.getProgression();
    }


    private void setProgress(){
        for (MuscleProgression progression: mProgressions){
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTime dateTime = formatter.parseDateTime(progression.getDate());

            Interval today = new Interval(DateTime.now(), Days.ONE);

            if (today.contains(dateTime)){
                mTodayProgressions.add(progression);
            }

        }

        //init the UI
        updateUi();
    }

    private void updateUi(){
        Log.d(TAG,"updateUi");

        if (mTodayProgressions.size() > 0) {
            onEmptyViewOff();

            changeProgressUI(mTodayProgressions);

        }else {
            onEmptyViewOn("You haven't train today:(");
        }
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

    private void insertMuscleToWebview(String muscle_name){
        //add muscle names to the webview
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.injectJS(webView,mMuscleParts);
    }


    private void changeProgressUI(List<MuscleProgression> progression){
        for (MuscleProgression muscleProgression: progression){
            createProgressionView(muscleProgression);

            Log.d(TAG,"muscleProgression with muscle id: "+muscleProgression.getMuscle_id());
            insertMuscleToWebview(muscleProgression.getMuscle().getMuscle_name());
        }
    }


    private void createProgressionView(MuscleProgression muscleProgression){
        Log.d(TAG,"createProgressionView");
        adapter.add(muscleProgression);
    }



    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        mUtilities.loadUrlOfMuscleBody(getActivity(),webView);
    }


    private void onComplete(){
        //refresh complete
        mSwipeRefreshLayout.setRefreshing(false);

        onLoadingViewOff();
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


        }else {

            Log.d(TAG,"same progressions");
            //cancel refreshing
            //mSwipeRefreshLayout.setRefreshing(false);
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


}
