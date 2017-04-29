package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.Filter;
import com.app.app.silverbarsapp.ProgressionAlgoritm;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailDailyActivity;
import com.app.app.silverbarsapp.components.DaggerTodayProgressionComponent;
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

public class ProgressFragmentDaily extends BaseFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressFragmentDaily.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text)TextView mEmptyText;

    @BindView(R.id.webview) WebView webView;

    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.reload) Button mReload;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    @BindView(R.id.modal_overlay) LinearLayout mModal;
    @BindView(R.id.info) ImageView mInfo;

    private List<ExerciseProgression> progressions;
    private List<ExerciseProgression> mWeekProgressions = new ArrayList<>();

    private Utilities mUtilities = new Utilities();
    private Filter filter = new Filter();

    private int mCurrentDay;
    List<Integer> list_progress = new ArrayList<>();
    private String mMuscleParts = " ";

    ProgressionAlgoritm mProgressionAlgoritm = new ProgressionAlgoritm();

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

        setupWebview();
        //mProgressionPresenter.getMuscleProgressions();
        mProgressionPresenter.getExerciseProgression();


        List<String> days = getDaysOfWeekAbreb();
        DateTime today = new DateTime();
        days.set(today.getDayOfWeek()-1,"Today");

        mSeekbarWithIntervals.setIntervals(days);
        mSeekbarWithIntervals.setInitialProgress(today.getDayOfWeek());
        mSeekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeSeekBarTextViewColor(progress);
                //Log.d(TAG,"progress "+progress);
                mCurrentDay = progress;
                updateUi(getProgressionByDay(progress));
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
        webView.addJavascriptInterface( new WebAppInterface(CONTEXT,this), "Android");
        mUtilities.loadUrlOfMuscleBody(CONTEXT,webView);
    }

    private void changeSeekBarTextViewColor(int progress){
        list_progress.add(progress);
        mSeekbarWithIntervals.changeTextColorSelected(progress);
        if (list_progress.size() >= 2){
            int penultimo_element = list_progress.size()-2;
            mSeekbarWithIntervals.changeTextColorNoSelected(list_progress.get(penultimo_element));
        }
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
                Log.d(TAG, "Monday");
                return filter.getProgressionFiltered(mWeekProgressions, getDays().get(0));
            case 1:
                Log.d(TAG, "Tuesday");
                return (filter.getProgressionFiltered(mWeekProgressions, getDays().get(1)));
            case 2:
                Log.d(TAG, "Wednesday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(2)));
            case 3:
                Log.d(TAG, "Thursday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(3)));
            case 4:
                Log.d(TAG, "Friday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(4)));
            case 5:
                Log.d(TAG, "Saturday");
                return(filter.getProgressionFiltered(mWeekProgressions, getDays().get(5)));
            case 6:
                Log.d(TAG, "Sunday");
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
        onEmptyViewOn("You haven't train :(");
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
        Log.d(TAG,"init");

        //filter the progressions
        Interval this_week = new Interval(getLocalDates().get(0).toDateTimeAtStartOfDay().minusDays(1), Weeks.ONE);
        mWeekProgressions = filter.getProgressionFiltered(progressions,this_week);

        Log.d(TAG,"this_week: "+this_week);
        Log.d(TAG,"mWeekProgressions: "+mWeekProgressions);


        initUI();
    }

    private void initUI(){
        //filter the ui
        mSeekbarWithIntervals.setProgress(new DateTime().getDayOfWeek() - 1);
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

    private List<String> getDaysOfWeekAbreb() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.days_abreb));
    }

    private List<String> getDaysOfWeek() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.days));
    }

    private void updateUi(List<ExerciseProgression> progressions_filtered){
        if (progressions_filtered.size() > 0) {
            onEmptyViewOff();
            clearWebview();
            updateBodyMuscleWebView(progressions_filtered);
        }else {
            onEmptyViewOn("You haven't train :(");
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
        mMuscleParts += "#" + muscle_name + ",";
        mUtilities.onWebviewClickReady(webView,mMuscleParts);
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
