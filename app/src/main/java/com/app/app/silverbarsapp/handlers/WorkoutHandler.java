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

    private ArrayList<ExerciseRep> exercises;

    private int mCurrentExercisePosition = 0;
    private int mCurrentSet = 0;
    private int mCurrentRest = 0;

    private int mRestByExercise,mRestBySet;

    private Utilities.HandlerMover mExerciseMover;
    private Utilities.HandlerMover mSetMover;

    public WorkoutHandler(WorkoutEvents listener,ArrayList<ExerciseRep> exercises,int sets,int rest_by_exercise,int rest_by_set){
        this.listener = listener;
        this.exercises = exercises;
        mRestByExercise = rest_by_exercise;
        mRestBySet = rest_by_set;

        mCountDownController = new CountDownController(this);
        mExerciseMover = new Utilities.HandlerMover(exercises.size());
        mSetMover = new Utilities.HandlerMover(sets);
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




    /**
     *
     *
     *
     *
     *   Workout controls
     *
     *
     *
     *
     */
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
        changeNextExerciseOrSet();
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



    private void playCountDown(){
        if (!mCountDownController.isMainCountDownTimerAvailable()){
            mCountDownController.createMainCountDownTimer(exercises.get(mCurrentExercisePosition).getSeconds());
        }else
            mCountDownController.resumeMainCountDown();
    }

    /**
     *
     *
     *
     *   Workout utilities
     *
     *
     *
     *
     */




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





    /**
     *
     *
     *
     *  Rest controls
     *
     *
     *
     *
     */
    public void setRestListener(int  second){
        if (second == 0){
            restFinished();
        }
    }

    public void startRest(){

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

    private void startRestCountDown(int rest_seconds){
        //set the actual rest var
        mCurrentRest = rest_seconds;
        int total_secs = (rest_seconds +1);


        //create timer
        mCountDownController.createRestTimer(total_secs);
    }


    public void restFinished(){
        //Log.d(TAG,"restFinished");
        destroyRestCountDown();

        //rest finished event
        listener.onRestFinished(exercises.get(mCurrentExercisePosition).getExercise());

        changeNextExerciseOrSet();
    }


    /**
     *
     *
     *
     *   Controls to move next exercise or next set
     *
     *
     *
     *
     */

    private void changeNextExerciseOrSet(){
        int position = mExerciseMover.moveNext();

        if (mExerciseMover.allowToMove(position)) {
            mCurrentExercisePosition++;

            listener.onChangeToNextExercise(mCurrentExercisePosition);

        }else {
            changeToNextSet();
        }

    }


    private void changeToNextSet(){
        int position = mSetMover.moveNext();

        if (mSetMover.allowToMove(position)){
            mCurrentSet++;

            //set the exercise in the first position
            mCurrentExercisePosition = 0;

            //restart
            mExerciseMover.setPosition(0);

            //update UI
            listener.onChangeToNextSet(mCurrentSet);
        } else {
            listener.onWorkoutFinished();
        }
    }

    /**
     *
     *
     *
     *  to clean
     *
     *
     */

    private void destroyRestCountDown() {
        mCountDownController.destroyRestCountDownTimer();
    }

    public void destroy() {
        mCountDownController.destroyMainCountDownTimer();
        destroyRestCountDown();
    }


    /**
     *
     *
     *
     *  Main workout events
     *
     *
     *
     */
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
