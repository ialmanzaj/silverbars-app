package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
import com.app.app.silverbarsapp.components.DaggerMonthlyProgressionComponent;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.handlers.ProgressionAlgoritm;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
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


public class ProgressWeeklyFragment extends BaseFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressWeeklyFragment.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;


    @BindView(R.id.loading) LinearLayout mLoadingView;

    @BindView(R.id.error_view) LinearLayout mErrorView;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;


    private Filter filter = new Filter();
    private ProgressionAlgoritm mProgressionAlgoritm;


    private List<ExerciseProgression> mMonthProgressions = new ArrayList<>();

    private int mCurrentWeek = 0;

    LocalDate monthBegin = new LocalDate().withDayOfMonth(1);
    LocalDate monthEnd  = new LocalDate().plusMonths(1).withDayOfMonth(1).minusDays(1);


    private PENDING_ACTIONS mPendingAction = PENDING_ACTIONS.NONE;

    private enum PENDING_ACTIONS {
        NONE,
        CHANGE_TO_EMPTY,
        CHANGED_BODY
    }


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

            mProgressionAlgoritm = new ProgressionAlgoritm(CONTEXT);

            mProgressionPresenter.getExerciseProgression();

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


    private List<String> getWeeksAbreb() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.weeks_abreb));
    }

    private List<String> getTitlesWeek() {
        return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.weeks));
    }



    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mProgressionPresenter.getMuscleProgressions();
    }

    @Override
    public void onMuscleSelected(String muscle) {

        ArrayList<ExerciseProgression> last_week = filter.getProgressionFilteredByMuscle(getProgressionByWeek(mCurrentWeek-1),muscle);
        ArrayList<ExerciseProgression> current_week = filter.getProgressionFilteredByMuscle(getProgressionByWeek(mCurrentWeek),muscle);

        Intent intent = new Intent(CONTEXT,ExerciseDetailActivity.class);
        intent.putExtra("title", getTitlesWeek().get(mCurrentWeek));
        intent.putExtra("subtitle",muscle);
        intent.putExtra("exercises", mProgressionAlgoritm.getProgressionComparedWeekly(last_week,current_week));
        intent.putExtra("muscle_activation",mProgressionAlgoritm.getMuscleActivationProgression(muscle,last_week,current_week));
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

    private void updateMainUi(ArrayList<ExerciseProgression> progressions_filtered){
        Bundle extras = new Bundle();

        if (progressions_filtered.size() > 0){

            extras.putParcelableArrayList("progressions", progressions_filtered);
            BodyFragment currentFragment = new BodyFragment();
            currentFragment.setArguments(extras);
            currentFragment.addListener(this);

            changeFragment(currentFragment);

            mPendingAction = PENDING_ACTIONS.CHANGED_BODY;

        }else {

            if (mPendingAction != PENDING_ACTIONS.CHANGE_TO_EMPTY) {

                EmptyViewFragment currentFragment = new EmptyViewFragment();
                changeFragment(currentFragment);


                //FLAG
                mPendingAction = PENDING_ACTIONS.CHANGE_TO_EMPTY;
            }
        }


    }

    private void changeFragment(Fragment currentFragment){
        if (this.isAdded()) {
            new Handler().post(() -> {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, currentFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            });
        }
    }




    /**
     *
     *
     *
     *
     *     UI events
     *<p>
     *
     *
     *
     *
     *
     */



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
