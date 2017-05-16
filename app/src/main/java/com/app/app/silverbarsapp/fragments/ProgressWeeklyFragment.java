package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailWeeklyActivity;
import com.app.app.silverbarsapp.components.DaggerMonthlyProgressionComponent;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.handlers.MusclesWebviewHandler;
import com.app.app.silverbarsapp.handlers.ProgressionAlgoritm;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import im.delight.android.webview.AdvancedWebView;


public class ProgressWeeklyFragment extends BaseFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressWeeklyFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;

    @BindView(R.id.webview) AdvancedWebView webView;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    @BindView(R.id.modal_overlay) LinearLayout mModal;
    @BindView(R.id.info) ImageView mInfo;


    private Utilities mUtilities = new Utilities();
    MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();
    ProgressionAlgoritm mProgressionAlgoritm = new ProgressionAlgoritm();
    private Filter filter = new Filter();


    private List<ExerciseProgression> mMonthProgressions = new ArrayList<>();

    String mMuscleParts = " ";
    private int mCurrentWeek = 0;

    LocalDate monthBegin = new LocalDate().withDayOfMonth(1);
    LocalDate monthEnd  = new LocalDate().plusMonths(1).withDayOfMonth(1).minusDays(1);

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
        if (this.isAdded()) {

            mProgressionPresenter.getExerciseProgression();
            setupWebview();

            mSeekbarWithIntervals.setIntervals(getWeeksAbreb());
            mSeekbarWithIntervals.setInitialProgress(whichWeekIs(new DateTime()));
            mSeekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mSeekbarWithIntervals.changeSeekBarTextViewColor(progress);
                    mCurrentWeek = progress;
                    updateMainUi(getProgressionByWeek(progress));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
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


    private List<String> getWeeksAbreb() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.weeks_abreb));
    }

    private List<String> getTitlesWeek() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.weeks));
    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(CONTEXT,this), "Android");
        mUtilities.loadBodyFromUrl(CONTEXT,webView);
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mProgressionPresenter.getMuscleProgressions();
    }

    @Override
    public void onMuscleSelected(String muscle) {

        ArrayList<ExerciseProgression> current_week = mProgressionAlgoritm.getListOfAllBestProgression(
                filter.getProgressionFilteredByMuscle(getProgressionByWeek(mCurrentWeek),muscle));

        ArrayList<ExerciseProgression> last_week =  mProgressionAlgoritm.getListOfAllBestProgression(
                filter.getProgressionFilteredByMuscle(getProgressionByWeek(mCurrentWeek-1),muscle));

        Intent intent = new Intent(CONTEXT,ExerciseDetailWeeklyActivity.class);
        intent.putExtra("title", getTitlesWeek().get(mCurrentWeek));
        intent.putExtra("subtitle",muscle);
        intent.putExtra("type_date",1);
        intent.putExtra("exercises", mProgressionAlgoritm.compareWithOldProgressions(current_week,last_week));
        intent.putExtra("muscle_activation",mProgressionAlgoritm.getMuscleActivationProgression(muscle,current_week,last_week));
        startActivity(intent);
    }

    @Override
    public void emptyProgress() {
        onLoadingViewOff();
        initUI();
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
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
        //Log.d(TAG,"mMonthProgressions "+mMonthProgressions.size());

        initUI();
    }

    private void initUI(){
        //filter the ui
        int week = whichWeekIs(new DateTime());
        //Log.d(TAG,"week "+week);
        mSeekbarWithIntervals.setProgress(week);
    }

    private ArrayList<ExerciseProgression> getProgressionByWeek(int week) {
        switch (week){
            case -1:
                //Log.d(TAG, "last_week of the past month");
                return filter.getProgressionFiltered(mMonthProgressions,getWeeks().get(0));
            case 0:
                //Log.d(TAG, "week_one");
                return filter.getProgressionFiltered(mMonthProgressions,getWeeks().get(1));
            case 1:
                //Log.d(TAG, "week_two");
                return filter.getProgressionFiltered(mMonthProgressions,getWeeks().get(2));
            case 2:
                //Log.d(TAG, "week_three");
                return filter.getProgressionFiltered(mMonthProgressions,getWeeks().get(3));
            case 3:
                //Log.d(TAG, "week_four");
                return filter.getProgressionFiltered(mMonthProgressions,getWeeks().get(4));
            default:
                return null;
        }
    }

    private int whichWeekIs(DateTime today){
        if (filter.filterByDate(today,getWeeks().get(1))){
           //Log.d(TAG,"week_one: ");
            return 0;
        }else if (filter.filterByDate(today,getWeeks().get(2))){
            //Log.d(TAG,"week_two: ");
            return 1;
        }else if (filter.filterByDate(today,getWeeks().get(3))){
            //Log.d(TAG,"week_three: "+getWeeks().get(3));
            return 2;
        } else if (filter.filterByDate(today,getWeeks().get(4))){
            //Log.d(TAG,"week_four: "+getWeeks().get(4));
            return 3;
        }
        return -1;
    }

    private List<Interval> getWeeks() {
        return new ArrayList<Interval>() {{
            add(new Interval(monthBegin.minusWeeks(1).toDateTimeAtStartOfDay(), Weeks.ONE));
            add(new Interval(monthBegin.toDateTimeAtStartOfDay(), Weeks.ONE));
            add(new Interval(monthBegin.plusWeeks(1).toDateTimeAtStartOfDay(), Weeks.ONE));
            add(new Interval(monthBegin.plusWeeks(2).toDateTimeAtStartOfDay(), Weeks.ONE));
            add(new Interval(monthBegin.plusWeeks(3).toDateTimeAtStartOfDay(), monthEnd.toDateTimeAtStartOfDay()));
        }};
    }

    private void updateMainUi(List<ExerciseProgression> progressions){
        ///Log.d(TAG,"progressions "+progressions.size());
        if (progressions.size() > 0) {
            onEmptyViewOff();
            clearWebview();
            updateBodyMuscleWebView(progressions);
        }else {
            onEmptyViewOn(CONTEXT.getString(R.string.fragment_progress_weekly_empty));
        }
    }

    private void updateBodyMuscleWebView(List<ExerciseProgression> exerciseProgressions){
        for (ExerciseProgression exercise_progress: exerciseProgressions){
            for (MuscleExercise muscle: exercise_progress.getExercise().getMuscles()) {
                insertMuscleToWebview(muscle.getMuscle());
            }
        }
    }

    private void insertMuscleToWebview(String muscle_name){
        mMuscleParts += mMusclesWebviewHandler.getMuscleReadyForWebview(muscle_name);
        mMusclesWebviewHandler.addWebviewClientOnClickPaint(webView,mMuscleParts);
    }

    private void clearWebview(){
        mMuscleParts = " ";
        webView.reload();
    }

    private void onEmptyViewOn(String text){mEmptyView.setVisibility(View.VISIBLE);mEmptyText.setText(text);mInfo.setVisibility(View.GONE);}

    private void onEmptyViewOff(){mEmptyView.setVisibility(View.GONE);mInfo.setVisibility(View.VISIBLE);}

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
