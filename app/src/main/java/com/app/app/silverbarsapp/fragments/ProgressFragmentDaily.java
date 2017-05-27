package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.SeekBar;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
import com.app.app.silverbarsapp.components.DaggerTodayProgressionComponent;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.handlers.ProgressionAlgoritm;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;
import com.app.app.silverbarsapp.widgets.SeekbarWithIntervals;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

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

import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;

public class ProgressFragmentDaily extends BaseProgressionFragment implements ProgressionView, MuscleListener {

    private static final String TAG = ProgressFragmentDaily.class.getSimpleName();

    @Inject
    ProgressionPresenter mProgressionPresenter;

    @BindView(R.id.seekbarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervals;

    private ProgressionAlgoritm mProgressionAlgoritm;
    private Filter filter = new Filter();
    private Utilities utilities = new Utilities();

    private List<ExerciseProgression> progressions;
    private List<ExerciseProgression> mWeekProgressions = new ArrayList<>();

    private int mCurrentDay;

    private PENDING_ACTIONS mPendingAction = PENDING_ACTIONS.NONE;


    private MixpanelAPI mMixpanel;

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

        mMixpanel = MixpanelAPI.getInstance(CONTEXT, MIX_PANEL_TOKEN);

        mProgressionAlgoritm = new ProgressionAlgoritm(CONTEXT);
        mProgressionPresenter.getExerciseProgression();


        initSeekbar();
    }


    /**
     *
     *
     *    Click listeners
     *
     *
     *
     */

    @OnClick(R.id.reload)
    public void reload(){
        onErrorViewOff();
        onLoadingViewOn();
        mProgressionPresenter.getExerciseProgression();
    }



    /**
     *
     *
     *   Muscle events
     *
     *
     *
     */
    @Override
    public void onMuscleSelected(String muscle) {

        ArrayList<ExerciseProgression> current_day_progressions =
                filter.getProgressionFilteredByMuscle(getProgressionByDay(mCurrentDay),muscle);

        ArrayList<ExerciseProgression> last_progression = filter.getLastProgressions(current_day_progressions,progressions);

        Intent intent = new Intent(CONTEXT, ExerciseDetailActivity.class);
        intent.putExtra("title", getDaysOfWeek().get(mCurrentDay));
        intent.putExtra("subtitle", muscle);
        intent.putExtra("exercises", mProgressionAlgoritm.getProgressionComparedDaily(last_progression,current_day_progressions));
        intent.putExtra("muscle_activation",mProgressionAlgoritm.getMuscleActivationProgression(muscle,last_progression,current_day_progressions));
        startActivity(intent);
    }


    /**
     *
     *
     *
     *   API events
     *
     *
     *
     */

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

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
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

    private void initSeekbar(){
        List<String> days = getDaysOfWeekAbreb();
        mCurrentDay = new DateTime().getDayOfWeek() - 1;

        //set the current day
        days.set(mCurrentDay, CONTEXT.getString(R.string.fragment_progress_daily_today));
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

    private void initUI(){
        //filter the ui
        mSeekbarWithIntervals.setProgress(new DateTime().getDayOfWeek() - 1);
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

                EmptyFragment currentFragment = new EmptyFragment();
                changeFragment(currentFragment);


                //FLAG
                mPendingAction = PENDING_ACTIONS.CHANGE_TO_EMPTY;
            }
        }
    }





    /**
     *
     *
     *
     *   Date functions
     *
     *
     */

    private List<String> getDaysOfWeekAbreb() {return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.days_abreb));}

    private List<String> getDaysOfWeek() {return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.days));}

    private List<LocalDate> getLocalDates() {return new ArrayList<LocalDate>() {{
            add(new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.TUESDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.WEDNESDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.THURSDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.FRIDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.SATURDAY));
            add(new LocalDate().withDayOfWeek(DateTimeConstants.SUNDAY));
        }};}

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


    /**
     *
     *
     *
     *
     *    Mix panel events
     *
     *
     */

    private void mixPanelEventProgressionDaily(){
        mMixpanel.track("on Progression daily", utilities.getUserData(CONTEXT));
    }


}