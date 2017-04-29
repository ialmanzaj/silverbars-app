package com.app.app.silverbarsapp.utils;

import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseRep;

import java.util.ArrayList;

/**
 * Created by isaacalmanza on 03/14/17.
 */

public class WorkoutHandler implements CountDownController.CountDownEvents{

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
        //Log.i(TAG,"playWorkout");
        //if (isStarted){

        if (!utilities.checkIfRep(exercises.get(mCurrentExercisePosition))){
            //Log.d(TAG,"play seconds");
            playCountDown();
            listener.onWorkoutPlayed(exercises.get(mCurrentExercisePosition));
            return;
        }

        //Log.d(TAG,"play reps");
        listener.onWorkoutPlayed(exercises.get(mCurrentExercisePosition));
    }

    public void pauseWorkout(){
        //Log.i(TAG,"pauseWorkout");

        if (!utilities.checkIfRep(exercises.get(mCurrentExercisePosition))){
            //Log.d(TAG,"pause seconds");
            mCountDownController.pauseMainCountDown();
            listener.onWorkoutPaused(exercises.get(mCurrentExercisePosition));
            return;
        }

        //Log.d(TAG,"pause reps");
        listener.onWorkoutPaused(exercises.get(mCurrentExercisePosition));
    }


    public void stopWorkout(){
        //view.onExerciseFinished();
    }

    public void finishWorkout(){
        listener.onWorkoutFinished();
    }


    public void nextExercise(){
        mCurrentExercisePosition++;
        checkOnNextExerciseOrSet(mCurrentExercisePosition);
    }

    public void saveTime(long time){
        exercises.get(mCurrentExercisePosition).addTimesPerSet(mCurrentSet-1,time);
    }

    private void checkOnNextExerciseOrSet(int currentExercisePosition){
        if (currentExercisePosition<exercises.size())
            onChangeToNextExercise(currentExercisePosition);
        else
            onChangeToNextSet();
    }

    private void onChangeToNextSet(){
        //Log.d(TAG,"next set");
        // contar sets
        if (mCurrentSet+1 <= mTotalSets){

            //set the exercise in the first position
            mCurrentExercisePosition = 0;

            //plus one more set
            mCurrentSet++;

            //update UI
            listener.onChangeToNextSet(mCurrentSet);

        } else {
            //Log.d(TAG,"onWorkoutFinished");
            listener.onWorkoutFinished();
        }
    }

    private void onChangeToNextExercise(int currentExercisePosition){
        listener.onChangeToNextExercise(currentExercisePosition);
    }

    /**
     *    Events when timer has finished
     *<p>
     *
     *
     */
    public void setListenerOverlayView(int  second){
        if (second == 0){
            onRestFinished();
        }
    }

    public void startRest(){
        //Log.d(TAG,"startRest");

        //set rest flag active
        isWorkoutRest = true;

        //starting rest CountDownTimer
        if (mCurrentExercisePosition+1 < exercises.size()){
            //Log.d(TAG,"rest by exercise");
            startRestCountDownTimer(mRestByExercise);

        }else {
            //Log.d(TAG,"rest by set");
            startRestCountDownTimer(mRestBySet);
        }

        //update UI
        listener.onRestStarted();
    }

    private void startRestCountDownTimer(int rest){
        //set the actual rest var
        mCurrentRest = rest;
        int total_secs = (rest +1);
        //create timer
        mCountDownController.createRestTimer(total_secs);
    }

    private void onRestFinished(){
        //Log.d(TAG,"onRestFinished");
        mCountDownController.destroyRestCountDownTimer();

        //flag rest off
        isWorkoutRest = false;

        //rest finished event
        listener.onRestFinished(exercises.get(mCurrentExercisePosition).getExercise());

        //move to the next exercise or set after rest
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
