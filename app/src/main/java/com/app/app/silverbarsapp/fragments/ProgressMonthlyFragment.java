package com.app.app.silverbarsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
import com.app.app.silverbarsapp.adapters.SimpleStringAdapter;
import com.app.app.silverbarsapp.components.DaggerTotalProgressionComponent;
import com.app.app.silverbarsapp.handlers.Filter;
import com.app.app.silverbarsapp.handlers.ProgressionAlgoritm;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.utils.DisableTouchRecyclerListener;
import com.app.app.silverbarsapp.utils.MuscleListener;
import com.app.app.silverbarsapp.utils.OnSwipeTouchListener;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.ProgressionView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.app.app.silverbarsapp.Constants.MIX_PANEL_TOKEN;

public class ProgressMonthlyFragment extends BaseProgressionFragment implements ProgressionView,MuscleListener {

    private static final String TAG = ProgressMonthlyFragment.class.getSimpleName();
    private static final DateTime FIRST_DAY_YEAR = new DateTime().dayOfYear().withMinimumValue();


    @Inject
    ProgressionPresenter mProgressionPresenter;


    @BindView(R.id.months)RecyclerView mMonths;


    private ArrayList<ExerciseProgression> mYearProgressions = new ArrayList<>();

    ProgressionAlgoritm mProgressionAlgoritm;
    private Utilities utilities = new Utilities();
    private Utilities.HandlerMover handlerMover;
    private Filter filter = new Filter();

    SimpleStringAdapter adapter;

    private int mCurrentMonth;

    private PENDING_ACTIONS mPendingAction = PENDING_ACTIONS.NONE;

    private MixpanelAPI mMixpanel;


    @Override
    protected BasePresenter getPresenter() {
        return mProgressionPresenter;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_progress_monthly;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        DaggerTotalProgressionComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(CONTEXT).getComponent())
                .progressionModule(new ProgressionModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mProgressionPresenter.getMuscleProgressions();

        mMixpanel = MixpanelAPI.getInstance(CONTEXT, MIX_PANEL_TOKEN);


        mProgressionAlgoritm = new ProgressionAlgoritm(CONTEXT);
        mProgressionPresenter.getExerciseProgression();
        setupAdapter();
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

    @OnClick(R.id.next)
    public void nextButton(){
        int position = handlerMover.moveNext();
        if (handlerMover.allowToMove(position)) {
            mCurrentMonth = position;
            mMonths.post(() -> mMonths.smoothScrollToPosition(mCurrentMonth));
            updateMainUi(getProgressionByMonth(mCurrentMonth));
        }
    }

    @OnClick(R.id.preview)
    public void previewButton() {
        int position = handlerMover.movePreview();
        if (handlerMover.allowToMove(position)) {
            mCurrentMonth = position;
            mMonths.post(() -> mMonths.smoothScrollToPosition(mCurrentMonth));
            updateMainUi(getProgressionByMonth(mCurrentMonth));
        }
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

        ArrayList<ExerciseProgression> last_month_progressions = filter.getProgressionFilteredByMuscle(getProgressionByMonth(mCurrentMonth-1), muscle);
        ArrayList<ExerciseProgression> current_month_progressions = filter.getProgressionFilteredByMuscle(getProgressionByMonth(mCurrentMonth), muscle);


        Intent intent = new Intent(CONTEXT, ExerciseDetailActivity.class);
        intent.putExtra("title", getMonthsNames().get(mCurrentMonth));
        intent.putExtra("subtitle",muscle);
        intent.putExtra("exercises", mProgressionAlgoritm.getProgressionComparedMonthly(last_month_progressions,current_month_progressions));
        intent.putExtra("muscle_activation",mProgressionAlgoritm.getMuscleActivationProgression(muscle,last_month_progressions,current_month_progressions));
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
        onLoadingViewOff();
        //to reverse the order of the list
        Collections.reverse(progressions);
        init(progressions);
    }

    @Override
    public void displayNetworkError() {
        onErrorViewOn();
    }

    @Override
    public void displayServerError() {
        onErrorViewOn();
    }


    private void init(List<ExerciseProgression> progressions){

        Interval year_interval =
                new Interval(FIRST_DAY_YEAR.withTimeAtStartOfDay(),
                        FIRST_DAY_YEAR.plusMonths(11).plusMonths(1).withDayOfMonth(1).minusDays(1));


        mYearProgressions = filter.getProgressionFiltered(progressions,year_interval);

        initUI();
    }

    /**
     *
     *
     *
     *     UI events
     *
     *
     *
     *
     *
     */

    private void setupAdapter(){
        mMonths.addOnItemTouchListener(new DisableTouchRecyclerListener());
        mMonths.setLayoutManager(new LinearLayoutManager(CONTEXT, LinearLayoutManager.HORIZONTAL, false));

        adapter = new SimpleStringAdapter(getMonthsNames());
        handlerMover = new Utilities.HandlerMover(getMonthsNames().size());
        mMonths.setAdapter(adapter);

        mMonths.setOnTouchListener(new OnSwipeTouchListener(CONTEXT){
            @Override
            public void onSwipeRight() {
                handlerMover.moveNext();
            }
            @Override
            public void onSwipeLeft() {
                handlerMover.movePreview();
            }
        });
    }


    private void initUI(){
        //filter the progressions to get the current month
        mCurrentMonth = whichMonthIs(new DateTime());

        //move the list to the current item
        mMonths.post(() -> mMonths.scrollToPosition(mCurrentMonth));

        //handle the nextMusic and previewMusic logic
        handlerMover.setPosition(mCurrentMonth);


        //update the ui with the current month
        updateMainUi(getProgressionByMonth(mCurrentMonth));
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
     *
     */
    private int whichMonthIs(DateTime today){
        if (filter.filterByDate(today, getMonths().get(0))){
            return 0;
        }else if (filter.filterByDate(today, getMonths().get(1))){
            return 1;
        }else if (filter.filterByDate(today, getMonths().get(2))){
            return 2;
        } else if (filter.filterByDate(today, getMonths().get(3))){
            return 3;
        } else if (filter.filterByDate(today, getMonths().get(4))){
            return 4;
        }else if (filter.filterByDate(today, getMonths().get(5))){
            return 5;
        }else if (filter.filterByDate(today, getMonths().get(6))){
            return 6;
        } else if (filter.filterByDate(today, getMonths().get(7))){
            return 7;
        } else if (filter.filterByDate(today, getMonths().get(8))){
            return 8;
        }else if (filter.filterByDate(today, getMonths().get(9))){
            return 9;
        }else if (filter.filterByDate(today, getMonths().get(10))){
            return 10;
        } else if (filter.filterByDate(today, getMonths().get(11))){
            return 11;
        }
        return -1;
    }

    private List<Interval> getMonths() {
        return new ArrayList<Interval>() {{
            add(new Interval(FIRST_DAY_YEAR.withTimeAtStartOfDay(), FIRST_DAY_YEAR.plusMonths(1).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(1).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(2).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(2).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(3).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(3).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(4).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(4).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(5).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(5).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(6).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(6).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(7).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(7).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(8).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(8).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(9).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(9).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(10).withDayOfMonth(1).minusDays(1) ));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(10).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(11).withDayOfMonth(1).minusDays(1)));
            add(new Interval(FIRST_DAY_YEAR.plusMonths(11).withDayOfMonth(1), FIRST_DAY_YEAR.plusMonths(12).withDayOfMonth(1).minusDays(1) ));
        }};
    }

    private List<String> getMonthsNames() {return Arrays.asList(CONTEXT.getResources().getStringArray(R.array.months));}

    private ArrayList<ExerciseProgression> getProgressionByMonth(int month) {
        switch (month){
            case 0:
                //Log.d(TAG,"January: "+getMonths().get(0));
                return filter.getProgressionFiltered(mYearProgressions, getMonths().get(0));
            case 1:
                //Log.d(TAG,"February: "+getMonths().get(1));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(1));
            case 2:
                //Log.d(TAG,"March: "+getMonths().get(2));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(2));
            case 3:
                //Log.d(TAG,"April: "+getMonths().get(3));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(3));
            case 4:
                //Log.d(TAG,"May: "+getMonths().get(4));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(4));
            case 5:
                //Log.d(TAG,"June: "+getMonths().get(5));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(5));
            case 6:
                //Log.d(TAG,"July: "+getMonths().get(6));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(6));
            case 7:
                //Log.d(TAG,"August: "+getMonths().get(7));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(7));
            case 8:
                //Log.d(TAG,"September: "+getMonths().get(8));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(8));
            case 9:
                //Log.d(TAG,"October: "+getMonths().get(9));
                return filter.getProgressionFiltered(mYearProgressions,getMonths().get(9));
            case 10:
                //Log.d(TAG,"November: "+getMonths().get(10));
                return(filter.getProgressionFiltered(mYearProgressions,getMonths().get(10)));
            case 11:
                //Log.d(TAG,"December: "+getMonths().get(11));
                return(filter.getProgressionFiltered(mYearProgressions,getMonths().get(11)));
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
     *
     *
     */

    private void mixPanelEventProgressionMonthly(){
        mMixpanel.track("on Progression monthly", utilities.getUserData(CONTEXT));
    }





}