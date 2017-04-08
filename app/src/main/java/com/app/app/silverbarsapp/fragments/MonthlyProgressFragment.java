package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.MonthDetailActivity;
import com.app.app.silverbarsapp.components.DaggerMonthlyProgressionComponent;
import com.app.app.silverbarsapp.models.MuscleProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.SeekbarWithIntervals;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

import org.joda.time.DateTime;
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


public class MonthlyProgressFragment extends BaseFragment implements ProgressionView {

    private static final String TAG = MonthlyProgressFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    @BindView(R.id.see_more) Button mSeeMoreButton;

    List<MuscleProgression> mProgressions = new ArrayList<>();
    List<MuscleProgression> mMonthProgressions = new ArrayList<>();


    LocalDate monthBegin = new LocalDate().withDayOfMonth(1);
    LocalDate monthEnd = new LocalDate().plusMonths(1).withDayOfMonth(1).minusDays(1);

    LocalDate first_day_second_week = monthBegin.plusWeeks(1);
    LocalDate first_day_third_week = monthBegin.plusWeeks(2);
    LocalDate first_day_fourth_week = monthBegin.plusWeeks(3);

    Interval week_one = new Interval(monthBegin.toDateTimeAtStartOfDay(), Weeks.ONE);
    Interval week_two = new Interval(first_day_second_week.plusDays(1).toDateTimeAtStartOfDay(), Weeks.ONE);
    Interval week_three = new Interval(first_day_third_week.toDateTimeAtStartOfDay(), Weeks.ONE);
    Interval week_four =  new Interval(first_day_fourth_week.toDateTimeAtStartOfDay(), monthEnd.toDateTimeAtStartOfDay());

    private Utilities mUtilities = new Utilities();
    String mMuscleParts = " ";
    private int mCurrentWeek = 0;

    List<Integer> list_progress = new ArrayList<>();

    @Override
    protected int getFragmentLayout() {
        return R.layout.month_progress_fragment;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerMonthlyProgressionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .progressionModule(new ProgressionModule(this)).build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupWebview();
        mProgressionPresenter.getMuscleProgressions();

        mSeekbarWithIntervals.setIntervals(getWeeksOfMonthAbreb());
        mSeekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG,"mCurrentWeek "+progress);
                mCurrentWeek = progress;
                changeSeekBarTextViewColor(progress);
                switch (progress){
                    case 0:
                        updateUi(getProgressionFiltered(mMonthProgressions,week_one));
                        break;
                    case 1:
                        updateUi(getProgressionFiltered(mMonthProgressions,week_two));
                        break;
                    case 2:
                        updateUi(getProgressionFiltered(mMonthProgressions,week_three));
                        break;
                    case 3:
                        updateUi(getProgressionFiltered(mMonthProgressions,week_four));
                        break;
                    default:
                        onEmptyViewOn("You haven't train :(");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    private void changeSeekBarTextViewColor(int progress){
        list_progress.add(progress);
        mSeekbarWithIntervals.changeTextColorSelected(progress);
        if (list_progress.size() >= 2){
            int penultimo_element = list_progress.size()-2;
            mSeekbarWithIntervals.changeTextColorNoSelected(list_progress.get(penultimo_element));
        }
    }

    private List<String> getWeeksOfMonthAbreb() {
        return new ArrayList<String>() {{
            add("Week 1");
            add("Week 2");
            add("Week 3");
            add("Week 4");
        }};
    }

    private List<String> getNumbersWeek() {
        return new ArrayList<String>() {{
            add("First week");
            add("Second week");
            add("Third week");
            add("Fourth week");
        }};
    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        mUtilities.loadUrlOfMuscleBody(CONTEXT,webView);
    }

    @OnClick(R.id.see_more)
    public void seeMore(){
        Intent intent = new Intent(CONTEXT,MonthDetailActivity.class);
        intent.putExtra("title", getNumbersWeek().get(mCurrentWeek));
        startActivity(intent);
    }


    @Override
    public void emptyProgress() {
        onLoadingViewOff();
        onEmptyViewOn("");
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
        //Log.d(TAG,"displayProgressions");
        onLoadingViewOff();

        Collections.reverse(progressions);
        mProgressions.addAll(progressions);

        init();
    }


    private void init(){
        //filter the progressions
        Interval this_month = new Interval(monthBegin.toDateTimeAtStartOfDay(), monthEnd.toDateTimeAtStartOfDay());
        mMonthProgressions = getProgressionFiltered(mProgressions,this_month);
        //Log.d(TAG,"mMonthProgressions: "+mMonthProgressions.size());

        //filter the ui

        mSeekbarWithIntervals.setProgress(0);
    }

    private int whichWeekIs(DateTime today){
        if (filterByDate(today,week_one)){
            return 0;
        }else if (filterByDate(today,week_two)){
            return 1;
        }else if (filterByDate(today,week_three)){
            return 2;
        } else if (filterByDate(today,week_four)){
            return 3;
        }
        return -1;
    }


    private void updateUi(List<MuscleProgression> progressions){
        if (progressions.size() > 0) {
            //Log.d(TAG,"YES update" + progressions.size());
            onEmptyViewOff();


            clearWebview();

            updateBodyMuscleWebView(progressions);

        }else {
            //Log.d(TAG,"empty");
            onEmptyViewOn("You haven't train :(");
        }
    }
    private void updateBodyMuscleWebView(List<MuscleProgression> muscleProgressions){
        for (MuscleProgression progression: muscleProgressions){
            insertMuscleToWebview(progression.getMuscle().getMuscle_name());
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        //Log.d(TAG,"muscle_name: "+muscle_name);
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.onWebviewReady(webView,mMuscleParts);
    }

    private List<MuscleProgression> getProgressionFiltered(List<MuscleProgression> progressions,Interval date_interval){
        //Log.d(TAG,"filter progressions "+progressions.size());
        //Log.d(TAG,"filter date_interval "+date_interval);

        List<MuscleProgression> progressions_filted = new ArrayList<>();
        for (MuscleProgression progress: progressions){

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

            if (filterByDate(formatter.parseDateTime(progress.getDate()),date_interval)){
                progressions_filted.add(progress);
            }

        }
        return progressions_filted;
    }

    private boolean filterByDate(DateTime day,Interval date_interval){
        return date_interval.contains(day);
    }

    private void clearWebview(){
        Log.d(TAG,"clearWebview");
        mMuscleParts = " ";
        webView.reload();
    }

    private void onEmptyViewOn(String text){mEmptyView.setVisibility(View.VISIBLE);mEmptyText.setText(text);mSeeMoreButton.setVisibility(View.GONE);}

    private void onEmptyViewOff(){mEmptyView.setVisibility(View.GONE);mSeeMoreButton.setVisibility(View.VISIBLE);}

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
