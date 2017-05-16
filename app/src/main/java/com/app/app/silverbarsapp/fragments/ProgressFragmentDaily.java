package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailDailyActivity;
import com.app.app.silverbarsapp.components.DaggerTodayProgressionComponent;
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
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
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

public class ProgressFragmentDaily extends BaseFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressFragmentDaily.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.webview) AdvancedWebView webView;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    @BindView(R.id.modal_overlay) LinearLayout mModal;
    @BindView(R.id.info) ImageView mInfo;


    private Utilities mUtilities = new Utilities();
    private Filter filter = new Filter();
    MusclesWebviewHandler mMusclesWebviewHandler = new MusclesWebviewHandler();
    ProgressionAlgoritm mProgressionAlgoritm = new ProgressionAlgoritm();


    private List<ExerciseProgression> progressions;
    private List<ExerciseProgression> mWeekProgressions = new ArrayList<>();

    private int mCurrentDay;
    private String mMuscleParts = " ";


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_progress_daily;
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

            setupWebview();
            mProgressionPresenter.getExerciseProgression();

            List<String> days = getDaysOfWeekAbreb();
            mCurrentDay = new DateTime().getDayOfWeek() - 1;

            //set the current day
            days.set(mCurrentDay, CONTEXT.getString(R.string.fragment_progress_daily_today));


            //setup seekbar
            mSeekbarWithIntervals.setIntervals(days);
            mSeekbarWithIntervals.setInitialProgress(mCurrentDay);
            mSeekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int day, boolean fromUser) {
                    mSeekbarWithIntervals.changeSeekBarTextViewColor(day);
                    mCurrentDay = day;
                    updateMainUi(getProgressionByDay(day));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(CONTEXT,this), "Android");
        mUtilities.loadBodyFromUrl(CONTEXT,webView);
    }

    private List<LocalDate> getLocalDates() {
        return new ArrayList<LocalDate>() {{
            add(new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.TUESDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.WEDNESDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.THURSDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.FRIDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.SATURDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.SUNDAY));
        }};
    }

    private List<Interval> getDays() {
        return new ArrayList<Interval>() {{
            add(new Interval(getLocalDates().get(0).toDateTimeAtStartOfDay(), Days.ONE));
            add(new Interval(getLocalDates().get(1).toDateTimeAtStartOfDay(), Days.ONE));
            add(new Interval(getLocalDates().get(2).toDateTimeAtStartOfDay(), Days.ONE));
            add(new Interval(getLocalDates().get(3).toDateTimeAtStartOfDay(), Days.ONE));
            add(new Interval(getLocalDates().get(4).toDateTimeAtStartOfDay(), Days.ONE));
            add(new Interval(getLocalDates().get(5).toDateTimeAtStartOfDay(), Days.ONE));
            add(new Interval(getLocalDates().get(6).toDateTimeAtStartOfDay(), Days.ONE));
        }};
    }

    private ArrayList<ExerciseProgression> getProgressionByDay(int day){
        switch (day) {
            case 0:
                //Log.d(TAG, "Monday");
                return filter.getProgressionFiltered(mWeekProgressions, getDays().get(0));
            case 1:
                //Log.d(TAG, "Tuesday");
                return (filter.getProgressionFiltered(mWeekProgressions, getDays().get(1)));
            case 2:
                //Log.d(TAG, "Wednesday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(2)));
            case 3:
                //Log.d(TAG, "Thursday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(3)));
            case 4:
                //Log.d(TAG, "Friday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(4)));
            case 5:
                //Log.d(TAG, "Saturday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(5)));
            case 6:
                //Log.d(TAG, "Sunday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(6) ));
            default:
                return null;
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
        mProgressionPresenter.getExerciseProgression();
    }

    @Override
    public void onMuscleSelected(String muscle) {
        ArrayList<ExerciseProgression> current_exercises =
                filter.getProgressionFilteredByMuscle(getProgressionByDay(mCurrentDay),muscle);

        ArrayList<ExerciseProgression> last_progression = filter.getLastProgressions(current_exercises,progressions);

        Intent intent = new Intent(CONTEXT, ExerciseDetailDailyActivity.class);
        intent.putExtra("title", getDaysOfWeek().get(mCurrentDay));
        intent.putExtra("subtitle", muscle);
        intent.putExtra("type_date", 0);
        intent.putExtra("exercises", mProgressionAlgoritm.compareWithOldProgressions(current_exercises,last_progression));
        intent.putExtra("muscle_activation",mProgressionAlgoritm.getMuscleActivationProgression(muscle,current_exercises,last_progression));
        startActivity(intent);
    }

    @Override
    public void emptyProgress() {
        onLoadingViewOff();
        initUI();
    }

    @Override
    public void displayProgressions(List<ExerciseProgression> progressions) {
        this.progressions = progressions;
        onLoadingViewOff();
        Collections.reverse(progressions);
        init(progressions);
    }

    private void init(List<ExerciseProgression> progressions){
        //filter the progressions
        Interval this_week = new Interval(getLocalDates().get(0).toDateTimeAtStartOfDay().minusDays(1), Weeks.ONE);
        mWeekProgressions = filter.getProgressionFiltered(progressions,this_week);
        initUI();
    }

    private void initUI(){
        //filter the ui
        mSeekbarWithIntervals.setProgress(new DateTime().getDayOfWeek() - 1);
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
    }

    private List<String> getDaysOfWeekAbreb() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.days_abreb));
    }

    private List<String> getDaysOfWeek() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.days));
    }

    private void updateMainUi(List<ExerciseProgression> progressions_filtered){
        if (progressions_filtered.size() > 0) {
            onEmptyViewOff();
            clearWebview();
            updateBodyMuscleWebView(progressions_filtered);
        }else {
            onEmptyViewOn(CONTEXT.getString(R.string.fragment_progress_daily_empty));
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
        webView.reload();
        mMuscleParts = " ";
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