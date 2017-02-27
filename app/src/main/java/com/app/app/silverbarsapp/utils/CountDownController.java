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
    private int mCurrentSecondsMainTimer,mCurrentSecondsRestTimer,mCurrentSecondInitialTimer;


    public CountDownController(CountDownEvents listener){
        this.listener = listener;
    }


    public void createInicialTimer(int seconds){
        //current second default to 0
        mCurrentSecondInitialTimer = 0;

        long total_sec = (seconds +4) * 1000;

        mStartedInitialTimer = new CountDownTimer(total_sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                //save current second to resume-pause
                mCurrentSecondInitialTimer  = Math.round(millisUntilFinished * 0.001f);
                listener.onTickInitialCounter();
            }
            public void onFinish() {}
        }.start();
    }

    public boolean isInicialTimerAvailable(){
        return mStartedInitialTimer != null;
    }

    public void pauseInicialTimer(){
        mStartedInitialTimer.cancel();
    }

    public void resumeInicialTimer(){
        createInicialTimer(mCurrentSecondInitialTimer);
    }

    public void createMainCountDownTimer(int seconds,int tempo){

        //set the timer to 0
        mCurrentSecondsMainTimer = 0;

        long seconds_total = seconds * 1000;
        long interval =  tempo * 1000;

        sMainCountDownTimer = new CountDownTimer(seconds_total, interval) {
            public void onTick(long millisUntilFinished) {

                //save current second to resume-pause
                mCurrentSecondsMainTimer  = Math.round(millisUntilFinished * 0.001f);

                listener.onTickMainCounter();
            }
            public void onFinish() {}
        }.start();
    }

    public boolean isMainCountDownTimerAvailable(){
        return sMainCountDownTimer != null;
    }

    public void pauseMainCountDown(){
        sMainCountDownTimer.cancel();
    }

    public void resumeMainCountDown(int tempo){
        createMainCountDownTimer(mCurrentSecondsMainTimer,tempo);
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

    public void destroyInicialTimer(){
        ///Log.d(TAG,"destroyInicialTimer");
        if (isInicialTimerAvailable()){mStartedInitialTimer.cancel();mStartedInitialTimer = null;}
    }

    public void destroyMainCountDownTimer(){
        //Log.d(TAG,"destroyMainCountDownTimer");
        if (isMainCountDownTimerAvailable()){sMainCountDownTimer.cancel();sMainCountDownTimer = null;}
    }
    public void destroyRestCountDownTimer(){
        //Log.d(TAG,"destroyRestCountDownTimer");
        if (isRestCountDownTimerAvailable()){mRestTimer.cancel();mRestTimer = null;}
    }

    public interface CountDownEvents{
        void onTickInitialCounter();
        void onTickMainCounter();
        void onTickRestCounter();
    }


}
