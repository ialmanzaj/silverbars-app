package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.MuscleDetailActivity;
import com.app.app.silverbarsapp.components.DaggerTodayProgressionComponent;
import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.SeekbarWithIntervals;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
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
public class WeeklyProgressFragment extends BaseFragment implements ProgressionView {

    private static final String TAG = WeeklyProgressFragment.class.getSimpleName();

    LocalDate Monday =  new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY);
    LocalDate Tuesday =  new LocalDate().withDayOfWeek(DateTimeConstants.TUESDAY);
    LocalDate Wednesday =  new LocalDate().withDayOfWeek(DateTimeConstants.WEDNESDAY);
    LocalDate Thursday =  new LocalDate().withDayOfWeek(DateTimeConstants.THURSDAY);
    LocalDate Friday =  new LocalDate().withDayOfWeek(DateTimeConstants.FRIDAY);
    LocalDate Saturday =  new LocalDate().withDayOfWeek(DateTimeConstants.SATURDAY);
    LocalDate Sunday =  new LocalDate().withDayOfWeek(DateTimeConstants.SUNDAY);


    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    @BindView(R.id.see_more) Button mSeeMoreButton;

    private List<MuscleProgression> mProgressions = new ArrayList<>();
    private List<MuscleProgression> mWeekProgressions = new ArrayList<>();

    private String mMuscleParts = " ";
    private Utilities mUtilities = new Utilities();
    private int mLastMusleId;

    @Override
    protected int getFragmentLayout() {
        return R.layout.weekly_progress_fragment;
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

        setupWebview();
        mProgressionPresenter.getProgression();


        mSeekbarWithIntervals.setIntervals(getDaysOfWeek());

        mSeekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG,"progress "+progress);
                switch (progress){
                    case 0:
                        updateUi(getProgressionFiltered(mWeekProgressions,new Interval(Monday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                    case 1:
                        updateUi(getProgressionFiltered(mWeekProgressions,new Interval(Tuesday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                    case 2:
                        updateUi(getProgressionFiltered(mWeekProgressions, new Interval(Wednesday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                    case 3:
                        updateUi(getProgressionFiltered(mWeekProgressions,new Interval(Thursday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                    case 4:
                        updateUi(getProgressionFiltered(mWeekProgressions,new Interval(Friday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                    case 5:
                        updateUi(getProgressionFiltered(mWeekProgressions,new Interval(Saturday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                    case 6:
                        updateUi(getProgressionFiltered(mWeekProgressions,new Interval(Sunday.toDateTimeAtStartOfDay(), Days.ONE)));
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        mUtilities.loadUrlOfMuscleBody(CONTEXT,webView);
    }

    @OnClick(R.id.see_more)
    public void seeMoreButton(){
        startActivity(new Intent(CONTEXT, MuscleDetailActivity.class));
    }

    private List<String> getDaysOfWeek() {
        return new ArrayList<String>() {{
            add("Mon");
            add("Tue");
            add("Wed");
            add("Thurs");
            add("Fri");
            add("Sat");
            add("Sun");
        }};
    }

    @Override
    public void displayProgressions(List<MuscleProgression> progressions) {
        Collections.reverse(progressions);
        mProgressions.addAll(progressions);
        getMusclesFromProgression();
    }

    @Override
    public void emptyProgress() {
        onLoadingViewOff();
        onErrorViewOn();
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
    public void displayMuscle(Muscle muscle) {
        insertMuscleNamesInProgression(muscle);
        if (muscle.getId() == mLastMusleId){
            onLoadingViewOff();

            //filter the progressions
            Interval this_week = new Interval(Monday.toDateTimeAtStartOfDay(), Weeks.ONE);
            mWeekProgressions = getProgressionFiltered(mProgressions,this_week);

            //filter the ui
            Interval monday = new Interval(Monday.toDateTimeAtStartOfDay(), Days.ONE);
            updateUi(getProgressionFiltered(mWeekProgressions,monday));
        }
    }

    private void insertMuscleNamesInProgression(Muscle muscle){
        for (MuscleProgression progression: mProgressions){
            if (progression.getMuscle_id() == muscle.getId()){
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


    private List<MuscleProgression> getProgressionFiltered(List<MuscleProgression> progressions,Interval day){
        List<MuscleProgression> progressions_filted = new ArrayList<>();
        for (MuscleProgression progress: progressions){
            if (filterByDate(progress,day)){
                progressions_filted.add(progress);
            }
        }
        return progressions_filted;
    }



    private boolean filterByDate(MuscleProgression progress,Interval day){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTime progress_date = formatter.parseDateTime(progress.getDate());
        return day.contains(progress_date);
    }

    private void updateUi(List<MuscleProgression> progressions){
        Log.d(TAG,"updateUi: "+progressions.size());

        if (progressions.size() > 0) {
            Log.d(TAG,"YES update");
            onEmptyViewOff();
            clearWebview();
            updateMuscle(progressions);
        }else {
            Log.d(TAG,"empty");
            onEmptyViewOn("You haven't train :(");
        }
    }

    private void updateMuscle(List<MuscleProgression> progression){
        for (MuscleProgression muscleProgression: progression){
            insertMuscleToWebview(muscleProgression.getMuscle().getMuscle_name());
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.onWebviewReady(webView,mMuscleParts);
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
