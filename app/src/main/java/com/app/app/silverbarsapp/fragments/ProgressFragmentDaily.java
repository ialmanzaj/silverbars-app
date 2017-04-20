package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.AsyncTask;
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
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProgressFragmentDaily extends BaseFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressFragmentDaily.class.getSimpleName();

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


    private List<ExerciseProgression> mWeekProgressions = new ArrayList<>();

    @BindView(R.id.modal_overlay)LinearLayout mModalOverlay;


    private Utilities mUtilities = new Utilities();
    private Filter filter = new Filter();

    private int mCurrentDay;
    List<Integer> list_progress = new ArrayList<>();
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
        Log.d(TAG,"ProgressFragmentDaily");

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


    private void changeSeekBarTextViewColor(int progress){
        list_progress.add(progress);
        mSeekbarWithIntervals.changeTextColorSelected(progress);
        if (list_progress.size() >= 2){
            int penultimo_element = list_progress.size()-2;
            mSeekbarWithIntervals.changeTextColorNoSelected(list_progress.get(penultimo_element));
        }
    }

    private void setupWebview(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface( new WebAppInterface(CONTEXT,this), "Android");
        mUtilities.loadUrlOfMuscleBody(CONTEXT,webView);
    }

    private ArrayList<ExerciseProgression> getProgressionByDay(int day){
        switch (day) {
            case 0:
                Log.d(TAG, "Monday");
                return filter.getProgressionFiltered(mWeekProgressions, new Interval(Monday.toDateTimeAtStartOfDay(), Days.ONE));
            case 1:
                Log.d(TAG, "Tuesday");
                return (filter.getProgressionFiltered(mWeekProgressions, new Interval(Tuesday.toDateTimeAtStartOfDay(), Days.ONE)));
            case 2:
                Log.d(TAG, "Wednesday");
                return(filter.getProgressionFiltered(mWeekProgressions, new Interval(Wednesday.toDateTimeAtStartOfDay(), Days.ONE)));
            case 3:
                Log.d(TAG, "Thursday");
                return(filter.getProgressionFiltered(mWeekProgressions, new Interval(Thursday.toDateTimeAtStartOfDay(), Days.ONE)));
            case 4:
                Log.d(TAG, "Friday");
                return(filter.getProgressionFiltered(mWeekProgressions, new Interval(Friday.toDateTimeAtStartOfDay(), Days.ONE)));
            case 5:
                Log.d(TAG, "Saturday");
                return(filter.getProgressionFiltered(mWeekProgressions, new Interval(Saturday.toDateTimeAtStartOfDay(), Days.ONE)));
            case 6:
                Log.d(TAG, "Sunday");
                return(filter.getProgressionFiltered(mWeekProgressions, new Interval(Sunday.toDateTimeAtStartOfDay(), Days.ONE)));
            default:
                return null;
        }
    }

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mProgressionPresenter.getMuscleProgressions();
    }

    private List<String> getDaysOfWeekAbreb() {
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

    private List<String> getDaysOfWeek() {
        return new ArrayList<String>() {{
            add("Monday");
            add("Tuesday");
            add("Wednesday");
            add("Thursday");
            add("Friday");
            add("Saturday");
            add("Sunday");
        }};
    }

    private void show(){

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void result) {

                //mModalOverlay.setVisibility(View.INVISIBLE);
            }
            @Override
            protected void onPreExecute() {
                //mModalOverlay.setVisibility(View.VISIBLE);
            }
            @Override
            protected Void doInBackground(Void... params) {
                // Try to sleep for roughly 2 seconds
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;

            }

        }.execute();
    }

    @Override
    public void displayProgressions(List<ExerciseProgression> progressions) {
        onLoadingViewOff();
        Collections.reverse(progressions);
        initView(progressions);
    }

    @Override
    public void onMuscleSelected(String muscle) {
        Intent intent = new Intent(CONTEXT, ExerciseDetailActivity.class);
        intent.putExtra("title", getDaysOfWeek().get(mCurrentDay));
        intent.putExtra("subtitle",muscle);
        intent.putExtra("exercises",filter.getProgressionFilteredByMuscle(getProgressionByDay(mCurrentDay),muscle));
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

    private void initView(List<ExerciseProgression> progressions){
        Log.d(TAG,"init");

        //filter the progressions
        Interval this_week = new Interval(Monday.toDateTimeAtStartOfDay().minusDays(1), Weeks.ONE);
        mWeekProgressions = filter.getProgressionFiltered(progressions,this_week);

        Log.d(TAG,"this_week: "+this_week);
        Log.d(TAG,"mWeekProgressions: "+mWeekProgressions);

        //filter the ui
        mSeekbarWithIntervals.setProgress(new DateTime().getDayOfWeek() - 1);

        //empty view
        if (mWeekProgressions.size() <= 0) {
            onEmptyViewOn("You haven't train :(");
        }
    }

    private void updateUi(List<ExerciseProgression> progressions_filtered){
        Log.d(TAG,"updateUi: "+progressions_filtered.size());
        if (progressions_filtered.size() > 0) {
            Log.d(TAG,"YES update");
            show();
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
