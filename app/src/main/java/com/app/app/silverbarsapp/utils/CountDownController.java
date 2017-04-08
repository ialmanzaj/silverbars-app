package com.app.app.silverbarsapp.utils;

import android.os.CountDownTimer;

/**
 * Created by isaacalmanza on 02/19/17.
 */

public class CountDownController {

    private static final String TAG = CountDownController.class.getSimpleName();

    //the main countdowns to use
    private CountDownTimer sMainCountDownTimer, mRestTimer, mStartedInitialTimer;

    private CountDownEvents listener;

    //save the the seconds to finish the coutdown
    private int mCurrentSecondsMainTimer,mCurrentSecondsRestTimer;

    private Utilities utilities = new Utilities();

    public CountDownController(CountDownEvents listener){
        this.listener = listener;
    }

    public void createMainCountDownTimer(int seconds){
        //set the timer to 0
        mCurrentSecondsMainTimer = 0;

        int seconds_total =  (seconds+1) * 1000;

        sMainCountDownTimer = new CountDownTimer(seconds_total, 1000) {
            public void onTick(long milisecond) {

                long millis = milisecond - 1000;
                //save current second to resume-pause
                mCurrentSecondsMainTimer  = Math.round(millis * 0.001f);

                String text = utilities.formatHMS(millis);

                listener.onTickMainCounter(text);
            }
            public void onFinish() {}
        }.start();
    }

    public boolean isMainCountDownTimerAvailable(){
        return sMainCountDownTimer != null;
    }


    public void pauseMainCountDown(){
        if (isMainCountDownTimerAvailable()){
            sMainCountDownTimer.cancel();
        }
    }

    public void resumeMainCountDown(){
        createMainCountDownTimer(mCurrentSecondsMainTimer);
    }

    public void createRestTimer(int seconds){
        //init mCurrentSecondsRestTimer
        mCurrentSecondsRestTimer = 0;

        //Log.i(TAG,"createRestTimer: "+seconds);

        long seconds_total = seconds * 1000;

        mRestTimer = new CountDownTimer(seconds_total, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i(TAG,"mRestTimer "+millisUntilFinished);

                mCurrentSecondsRestTimer  = Math.round(millisUntilFinished * 0.001f);
                //Log.i(TAG,"mCurrentSecondsRestTimer "+mCurrentSecondsRestTimer);

                listener.onTickRestCounter();
            }
            public void onFinish() {}
        }.start();
    }

    public boolean isRestCountDownTimerAvailable(){
        return mRestTimer != null;
    }

    public void pauseRestCountDownTimer(){
        mRestTimer.cancel();
    }

    public void resumeRestCountDownTimer(){
        createRestTimer(mCurrentSecondsRestTimer);
    }

    public void destroyMainCountDownTimer(){
        if (isMainCountDownTimerAvailable()){sMainCountDownTimer.cancel();sMainCountDownTimer = null;}
    }

    public void destroyRestCountDownTimer(){
        if (isRestCountDownTimerAvailable()){mRestTimer.cancel();mRestTimer = null;}
    }

    public interface CountDownEvents{
        void onTickMainCounter(String total);
        void onTickRestCounter();
    }

}
