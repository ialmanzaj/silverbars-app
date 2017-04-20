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

import com.app.app.silverbarsapp.Filter;
import com.app.app.silverbarsapp.ProgressionAlgoritm;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
import com.app.app.silverbarsapp.components.DaggerMonthlyProgressionComponent;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.utils.WebAppInterface;
import com.app.app.silverbarsapp.viewsets.ProgressionView;
import com.app.app.silverbarsapp.widgets.SeekbarWithIntervals;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class ProgressWeeklyFragment extends BaseFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressWeeklyFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    @BindView(R.id.modal_overlay) LinearLayout mModal;


    List<ExerciseProgression> mMonthProgressions = new ArrayList<>();


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
    private Filter filter = new Filter();

    String mMuscleParts = " ";
    private int mCurrentWeek = 0;

    List<Integer> list_progress = new ArrayList<>();

    ProgressionAlgoritm mProgressionAlgoritm = new ProgressionAlgoritm();

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_progress_weekly;
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
        //mProgressionPresenter.getMuscleProgressions();
        mProgressionPresenter.getExerciseProgression();

        mSeekbarWithIntervals.setIntervals(getWeeksOfMonthAbreb());
        mSeekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurrentWeek = progress;
                changeSeekBarTextViewColor(progress);
                updateUi(getProgressionByWeek(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @OnClick(R.id.info)
    public void infoButton(){
        mModal.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.okay)
    public void okayButton(){
        mModal.setVisibility(View.GONE);
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

    private List<String> getTitlesWeek() {
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
        webView.addJavascriptInterface( new WebAppInterface(CONTEXT,this), "Android");
        mUtilities.loadUrlOfMuscleBody(CONTEXT,webView);
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mProgressionPresenter.getMuscleProgressions();
    }

    @Override
    public void onMuscleSelected(String muscle) {
        Intent intent = new Intent(CONTEXT,ExerciseDetailActivity.class);

        intent.putExtra("title", getTitlesWeek().get(mCurrentWeek));
        intent.putExtra("subtitle",muscle);
        intent.putExtra("exercises",

                compareWithOldProgressions(
                        filter.getProgressionFilteredByMuscle(getProgressionByWeek(mCurrentWeek),muscle),
                        filter.getProgressionFilteredByMuscle(getProgressionByWeek(mCurrentWeek-1),muscle))
        );

        startActivity(intent);
    }

    @Override
    public void emptyProgress() {
        Log.d(TAG,"emptyProgress");
        onLoadingViewOff();
        onEmptyViewOn("You haven't train ever:(");
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
        //Log.d(TAG,"displayProgressions");
        onLoadingViewOff();
        Collections.reverse(progressions);
        init(progressions);
    }

    private void init(List<ExerciseProgression> progressions){
        //filter the progressions
        Interval this_month = new Interval(monthBegin.toDateTimeAtStartOfDay(), monthEnd.toDateTimeAtStartOfDay());
        //Log.d(TAG,"this_month: "+this_month);

        mMonthProgressions = filter.getProgressionFiltered(progressions,this_month);

        //filter the ui
        mSeekbarWithIntervals.setProgress(whichWeekIs(new DateTime()));
    }

    private ArrayList<ExerciseProgression> getProgressionByWeek(int week) {
        switch (week){
            case -1:
                //Log.d(TAG, "last_week of the past month");
                return filter.getProgressionFiltered(mMonthProgressions,week_one);
            case 0:
                //Log.d(TAG, "week_one");
                return (filter.getProgressionFiltered(mMonthProgressions,week_one));
            case 1:
                //Log.d(TAG, "week_two");
                return (filter.getProgressionFiltered(mMonthProgressions,week_two));
            case 2:
                //Log.d(TAG, "week_three");
                return(filter.getProgressionFiltered(mMonthProgressions,week_three));
            case 3:
                //Log.d(TAG, "week_four");
                return(filter.getProgressionFiltered(mMonthProgressions,week_four));
            default:
                return null;
        }
    }

    private int whichWeekIs(DateTime today){
        if (filter.filterByDate(today,week_one)){
           // Log.d(TAG,"week_one: "+week_one);
            return 0;
        }else if (filter.filterByDate(today,week_two)){
            //Log.d(TAG,"week_two: "+week_two);
            return 1;
        }else if (filter.filterByDate(today,week_three)){
            //Log.d(TAG,"week_three: "+week_three);
            return 2;
        } else if (filter.filterByDate(today,week_four)){
            //Log.d(TAG,"week_four: "+week_four);
            return 3;
        }
        return -1;
    }

    private void updateUi(List<ExerciseProgression> progressions){
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


    private ArrayList<ExerciseProgression> compareWithOldProgressions(
            ArrayList<ExerciseProgression> current_week_progressions,ArrayList<ExerciseProgression> last_week_progressions){

        Log.d(TAG,"current_week_progressions "+current_week_progressions.size());
        Log.d(TAG,"last_week_progressions "+last_week_progressions.size());


        ArrayList<ExerciseProgression> progressions_results = new ArrayList<>();

        for (ExerciseProgression current_exercise: current_week_progressions){

            List<ExerciseProgression> last_old_progressions =
                    filter.filterProgressionByExercise(current_exercise.getExercise().getId(),last_week_progressions);

            if (last_old_progressions.size() > 0) {

                for (ExerciseProgression old_progression: last_old_progressions) {
                    progressions_results.add(mProgressionAlgoritm.getComparationReady(old_progression, current_exercise));
                }

            }else {
                progressions_results.add(mProgressionAlgoritm.checkImprovementsWithNoOldProgression(current_exercise));
            }

        }

      return progressions_results;
    }

    private void updateBodyMuscleWebView(List<ExerciseProgression> exerciseProgressions){
        for (ExerciseProgression exercise_progress: exerciseProgressions){
            for (MuscleExercise muscle: exercise_progress.getExercise().getMuscles()) {
                insertMuscleToWebview(muscle.getMuscle());
            }
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        //Log.d(TAG,"muscle_name: "+muscle_name);
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.onWebviewClickReady(webView,mMuscleParts);
    }

    private void clearWebview(){
        mMuscleParts = " ";
        webView.reload();
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
