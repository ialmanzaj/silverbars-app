package com.app.app.silverbarsapp.handlers;

import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by isaacalmanza on 03/14/17.
 */

public class WorkoutHandler implements CountDownController.CountDownEvents {

    private static final String TAG = WorkoutHandler.class.getSimpleName();

    private Utilities utilities = new Utilities();

    private CountDownController mCountDownController;
    private WorkoutEvents listener;

    private int mTotalSets;
    private ArrayList<ExerciseRep> exercises;

    private int mCurrentExercisePosition = 0;
    private int mCurrentSet = 1;

    //FLAGS for the events in workout
    private boolean isWorkoutPaused = false,isWorkoutRest = false;

    //this var for the countdown;
    private int mCurrentRest = 0;
    private int mRestByExercise,mRestBySet;


    public WorkoutHandler(WorkoutEvents listener,ArrayList<ExerciseRep> exercises,int sets,int restbyexercise,int restbyset){
        this.listener = listener;
        this.exercises = exercises;
        mCountDownController = new CountDownController(this);
        this.mTotalSets = sets;
        mRestByExercise = restbyexercise;
        mRestBySet = restbyset;
    }

    public ArrayList<ExerciseRep> getExercises(){
        return exercises;
    }

    @Override
    public void onTickMainCounter(String second) {
        listener.onCountDownWorking(second);
    }

    @Override
    public void onTickRestCounter() {
        mCurrentRest--;
        listener.onRestWorking(mCurrentRest);
    }

    public boolean isWorkoutPaused(){
        return isWorkoutPaused;
    }

    private void playCountDown(){
        if (!mCountDownController.isMainCountDownTimerAvailable()){
            mCountDownController.createMainCountDownTimer(exercises.get(mCurrentExercisePosition).getSeconds());
        }else
            mCountDownController.resumeMainCountDown();
    }

    public void playWorkout(){
        if (!utilities.checkIfRep(exercises.get(mCurrentExercisePosition))){

            playCountDown();
            listener.onWorkoutPlayed(exercises.get(mCurrentExercisePosition));
            return;
        }


        listener.onWorkoutPlayed(exercises.get(mCurrentExercisePosition));
    }

    public void pauseWorkout(){
        if (!utilities.checkIfRep(exercises.get(mCurrentExercisePosition))){

            mCountDownController.pauseMainCountDown();
            listener.onWorkoutPaused(exercises.get(mCurrentExercisePosition));
            return;
        }

        listener.onWorkoutPaused(exercises.get(mCurrentExercisePosition));
    }

    public void nextExercise(){
        mCurrentExercisePosition++;
        checkOnNextExerciseOrSet(mCurrentExercisePosition);
    }

    public void saveTimePerExercise(long seconds){
        String total_time;
        if (exercises.get(mCurrentExercisePosition).getTimes_per_set() != null) {
            total_time = exercises.get(mCurrentExercisePosition).getTimes_per_set() + "_" + cleanDate(utilities.formatHMS(seconds));
        }else {
            total_time = cleanDate(utilities.formatHMS(seconds));
        }

        exercises.get(mCurrentExercisePosition).setTimes_per_set(total_time);
    }


    private void checkOnNextExerciseOrSet(int currentExercisePosition){
        if (currentExercisePosition<exercises.size())
            onChangeToNextExercise(currentExercisePosition);
        else
            onChangeToNextSet();
    }

    private String cleanDate(String date){
        String[] date_splited = date.split(":");
        String date_new = "";
        for (String aDate_splited : date_splited) {
            if (!Objects.equals(aDate_splited, "00")) {
                date_new = !date_new.equals("") ? date_new + ":"+ aDate_splited : aDate_splited;
            }
        }
        return date_new.isEmpty() ? "0" : date_new;
    }

    private void onChangeToNextSet(){

        if (mCurrentSet+1 <= mTotalSets){

            //set the exercise in the first position
            mCurrentExercisePosition = 0;

            //plus one more set
            mCurrentSet++;

            //update UI
            listener.onChangeToNextSet(mCurrentSet);

        } else {

            listener.onWorkoutFinished();
        }
    }

    private void onChangeToNextExercise(int currentExercisePosition){
        listener.onChangeToNextExercise(currentExercisePosition);
    }

    /**
     *
     *
     *    Events when timer has finished
     *<p>
     *
     *
     *
     *
     */
    public void setListenerOverlayView(int  second){
        if (second == 0){
            restFinished();
        }
    }

    public void startRest(){
        //Log.d(TAG,"startRest");

        //set rest flag active
        isWorkoutRest = true;

        //starting rest CountDownTimer
        if (mCurrentExercisePosition+1 < exercises.size()){
            //Log.d(TAG,"rest by exercise");
            startRestCountDown(mRestByExercise);

        }else {
            //Log.d(TAG,"rest by set");
            startRestCountDown(mRestBySet);
        }

        //update UI
        listener.onRestStarted();
    }

    private void startRestCountDown(int rest){
        //set the actual rest var
        mCurrentRest = rest;
        int total_secs = (rest +1);
        //create timer
        mCountDownController.createRestTimer(total_secs);
    }

    public void restFinished(){
        //Log.d(TAG,"restFinished");
        mCountDownController.destroyRestCountDownTimer();

        //flag rest off
        isWorkoutRest = false;

        //rest finished event
        listener.onRestFinished(exercises.get(mCurrentExercisePosition).getExercise());

        //move to the nextMusic exercise or set after rest
        mCurrentExercisePosition++;
        checkOnNextExerciseOrSet(mCurrentExercisePosition);
    }

    public void destroy() {
        mCountDownController.destroyMainCountDownTimer();
        mCountDownController.destroyRestCountDownTimer();
    }

    public interface WorkoutEvents{
        void onCountDownWorking(String second);

        void onWorkoutPlayed(ExerciseRep exercise);
        void onWorkoutPaused(ExerciseRep exercise);
        void onWorkoutFinished();

        void onRestStarted();
        void onRestWorking(int rest);
        void onRestFinished(Exercise exercise);

        void onChangeToNextExercise(int exercise_position);
        void onChangeToNextSet(int current_set);
    }

}
