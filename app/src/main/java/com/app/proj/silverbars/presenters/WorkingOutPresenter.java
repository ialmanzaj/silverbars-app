package com.app.proj.silverbars.presenters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.proj.silverbars.MusicCallback;
import com.app.proj.silverbars.MusicPlayer;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.SpotifyPlayerImpl;
import com.app.proj.silverbars.activities.WorkingOutActivity;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.WorkingOutView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkingOutPresenter extends BasePresenter  implements MusicCallback {

    private static final String TAG = WorkingOutPresenter.class.getSimpleName();


    private Utilities utilities = new Utilities();
    private Context context;

    private WorkingOutView view;
    private SpotifyPlayerImpl mSpotifyPlayer;
    private MusicPlayer mLocalMusicPlayer;


    private MediaPlayer mExerciseAudioPlayer;
    
    private final float VOLUME_FULL = (float)(Math.log(100)/Math.log(100));;
    private final float VOLUME_HALF = (float)(Math.log(100-90)/Math.log(100));


    private CountDownTimer mMainTimer, mRestTimer, mStartTimer;

    private int TotalSets = 0, mCurrentSet = 0, Time_aux = 0,actualRest = 0,actual_start_time = 0,tempo = 0;

    private int Format_Time,Format_Time_Rest;

    private int actualReps;

    private int position = 0, exercise_position=0, execises_total = 0, ActualTimeMain=0, ActualTimeRest=0;
    private int totalTime;


    public WorkingOutPresenter(Context context,WorkingOutView view){
        this.context = context;
        this.view = view;
    }


    public void createSpotifyPlayer(String spotify_token, String spotify_playlist){
        if (mSpotifyPlayer == null){
            mSpotifyPlayer = new SpotifyPlayerImpl(context,spotify_token,spotify_playlist,this);
            mSpotifyPlayer.setup();
        }
    }


    public void createLocalMusicPlayer(String[] song_names,ArrayList<File> songs_files){
        if (mLocalMusicPlayer == null){
            mLocalMusicPlayer = new MusicPlayer(context,song_names,songs_files,this);
            mLocalMusicPlayer.setup(0,true);
        }
    }


    private void setupExercisePlayer(String exercise_audio_file){
            mExerciseAudioPlayer = new MediaPlayer();

            if (mExerciseAudioPlayer.isPlaying()) {
                mExerciseAudioPlayer.stop();
                mExerciseAudioPlayer.release();

                mExerciseAudioPlayer = new MediaPlayer();
            }


            try {

                String[] mp3dir = exercise_audio_file.split("/SilverbarsMp3/");
                mExerciseAudioPlayer = MediaPlayer.create(context, Uri.parse(context.getFilesDir()+"/SilverbarsMp3/"+mp3dir[1]));


            } catch (NullPointerException e) {
                Log.e(TAG,"Exception",e);
            }

    }

    public void playExerciseAudio(String exercise_audio_file){
        Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

        setupExercisePlayer(exercise_audio_file);


        mExerciseAudioPlayer.setOnPreparedListener(player -> {
            Log.e("mExerciseAudioPlayer","ready!");

          
            if (mLocalMusicPlayer != null && mLocalMusicPlayer.isPlaying())
                mLocalMusicPlayer.setVolume(0.04f,0.04f);


            if (mSpotifyPlayer != null){
                mSpotifyPlayer.onPauseSpotifyPlayer();
            }

            mExerciseAudioPlayer.start();
        });


        mExerciseAudioPlayer.setOnCompletionListener(mediaPlayer -> {

            if (mLocalMusicPlayer !=null && mLocalMusicPlayer.isPlaying()) {
                mLocalMusicPlayer.setVolume(VOLUME_FULL, VOLUME_FULL);
                mediaPlayer.release();
            }

            if (mSpotifyPlayer != null){
                mSpotifyPlayer.onPlayPlayer();
            }

        });
        
    }

    private void onSwipeMusicPreview(){


    }

    private void onSwipeMusicNext(){

    }

    @Override
    public void onSongName(String song_name) {
        view.updateSongName(song_name);
    }

    @Override
    public void onArtistName(String artist_name) {
        view.updateArtistName(artist_name);
    }



    public void startInicialTimer(int seconds){
        long  sec = (seconds+5) * 1000;
        int sec_interval = 1000;

        mStartTimer = new CountDownTimer(sec, sec_interval) {
            @Override
            public void onTick(long millisUntilFinished) {

                actual_start_time--;

                view.onMainCounterStarted(String.valueOf(actual_start_time));
            }

            public void onFinish() {}
        }.start();
    }



    private void startRestTimer(int actualrest){
        int totalsecs = (actualrest + 5) * 1000;
        int sec_interval = 1000;
        Format_Time_Rest = 0;

        mRestTimer = new CountDownTimer(totalsecs, sec_interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Format_Time_Rest =  Math.round(millisUntilFinished * 0.001f);

                actualRest--;

                //UI changes
                view.onRestCounterStarted(String.valueOf(actualRest));

            }
            public void onFinish() {}
        }.start();
    }


    public void showRestModal(int rest_time){
        //Log.v(TAG,"Descanso: Empezo");
        rest = true;

        mMainTimer.cancel();

        actualRest = rest_time;
        startRestTimer(rest_time);
    }



    // CONTADOR DE REPETICIONES
    private void performTick(long millisUntilFinished) {
        Format_Time = Math.round(millisUntilFinished * 0.001f);

        if (Time_aux-tempo == Format_Time){
            Time_aux = Time_aux - tempo;
            actualReps--;// restar repeticiones

            view.onRepetitionCountdown(String.valueOf(actualReps));//restar reps de texto mostrado
        }
    }

    //asign time for each repetition
    private void asignTotalTime(int position){
        totalTime = (exercises.get(position).getRepetition() * tempo) + 5;
    }


    private void startMainCountDown(int seconds,int interval, int time_aux){
        if (!BUTTON_PAUSE && !rest){
            //Log.d(TAG,"Main CountDown: activado");

            int totalsecs = seconds * 1000;
            int sec_interval= interval * 1000 ;
            Time_aux = time_aux;
            Format_Time = 0;

            mMainTimer = new CountDownTimer(totalsecs, sec_interval) {
                public void onTick(long millisUntilFinished) {
                    performTick(millisUntilFinished);
                }
                public void onFinish() {}
            }.start();

        }
    }


    private void PauseCountDown(){
        //Log.v(TAG,"PauseCountDown: activado");
        //Log.v(TAG,"rest flag: "+rest);
        pause = true;

        if (MAIN_TIMER && main_timer!=null){

            if (!rest){
                ActualTimeMain = Format_Time;
                main_timer.cancel();

            }else {

                //Log.v(TAG,"Pausar rest: activado");
                ActualTimeRest = Format_Time_Rest;
                restTimer.cancel();
            }
        }else if (INITIAL_TIMER) {

            startTimer.cancel();

        }

    }// PauseCountDown

    private void ResumeCountDown(){
        //Log.v(TAG,"ResumeCountDown: activado");
        //Log.v(TAG,"rest flag: "+rest);

        pause = false;


        if (rest){
            Log.v(TAG,"ActualTimeRest: "+ActualTimeRest);
            startRestTimer(ActualTimeRest);

        }else {

            if (MAIN_TIMER){
                startMainCountDown(ActualTimeMain,1,Time_aux);
            }
            if (INITIAL_TIMER){
                startInicialTimer(actual_start_time);
            }
        }
    }


    public void repetitionOperationLogic(CharSequence charSequence){

        actualReps = Integer.valueOf(charSequence.toString());

        if (actualReps == 0){

            if (exercise_position + 1 < execises_total){
                exercise_position++;

                if (exercises_size > 1){

                    //set preview exercise button on
                    view.onPreviewExercise();

                    if((exercise_position+1) == execises_total){

                        //set next exercise button on
                       view.onNextExercise();
                    }

                    
                }

                //when the repetition has finished
                view.onRepsFinished(exercise_position);

            } else {

                // contar sets
                if (mCurrentSet + 1 <= TotalSets){

                    //plus one more set
                    mCurrentSet++;

                    //set the exercise in the first position
                    exercise_position = 0;


                    view.onSetFinished(mCurrentSet);



                    //finish = true;


                } else {

                    //workout finished
                    mMainTimer.cancel();


                    view.onWorkoutFinished();
                }
            }
        }

    }

    public void restOperationLogic(CharSequence charSequence){
        if (actual_start_time == 0 && INITIAL_TIMER){

            INITIAL_TIMER = false;
            startTimer.cancel();

            mNextExercisebutton.setEnabled(true);
            mPreviewExerciseButton.setEnabled(true);

            MAIN_TIMER = true;
            ScreenOn();
            asignTotalTime(y);

            startMainCountDown(totalTime,1,totalTime);

            if (Songs_from_Phone && MAIN_TIMER){startLocalMusic();}

            if (spotify_playlist != null && mSpotifyToken != null){
                startPlaySpotify(spotify_playlist);
                onPlayMusicPlayerUI();
            }

            mModalOverlayView.setVisibility(View.GONE);

        }else if (actualRest == 0 && MAIN_TIMER){
            //Log.v(TAG,"Descanso: Terminado");

            mNextExercisebutton.setEnabled(true);
            mPreviewExerciseButton.setEnabled(true);

            restTimer.cancel();
            rest = false;

            if (play_exercise_audio){
                playExerciseAudio(exercises.get(y).getExercise().getExercise_audio());
            }


            asignTotalTime(y);
            startMainCountDown(totalTime,1,totalTime);
            mRepetitionTimerText.setText(String.valueOf(exercises.get(y).getRepetition()));


            mPositiveText.setText(String.valueOf(exercises.get(y).getTempo_positive()));
            mIsometricText.setText(String.valueOf(exercises.get(y).getTempo_isometric()));
            mNegativeText.setText(String.valueOf(exercises.get(y).getTempo_negative()));


            mModalOverlayView.setVisibility(View.GONE);
        }
    }


    public void playWorkout(){
        if (pause){
            BUTTON_PAUSE = false;



            ResumeCountDown();
            startLocalMusic();

        }
    }


    public void pauseWorkout(){
        if (!pause){

            BUTTON_PAUSE = true;
            PauseCountDown();

            pauseLocalMusic();
        }
    }


    public void dialogPreviewExercise(){

        PauseCountDown();

        new MaterialDialog.Builder(WorkingOutActivity.this)
                .title("Desea regresar al ejercicio anterior?")
                .content("Esta seguro que quiere regresar al ejercicio anterior?")
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .positiveText(getResources().getString(R.string.positive_dialog)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                ResumeCountDown();


                if (MAIN_TIMER){

                    list.smoothScrollToPosition(y-1);


                    y--;
                    mCurrentExerciseText.setText(String.valueOf(y+1));
                    main_timer.cancel();
                    mRepetitionTimerText.setText(String.valueOf(exercises.get(y).getRepetition()));

                    mPositiveText.setText(String.valueOf(exercises.get(y).getTempo_positive()));
                    mIsometricText.setText(String.valueOf(exercises.get(y).getTempo_isometric()));
                    mNegativeText.setText(String.valueOf(exercises.get(y).getTempo_negative()));

                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);

                    if (y == 0){

                        mPreviewExerciseButton.setVisibility(View.GONE);

                    }
                    else{
                        if(y < elements-1 && exercises_size > 1){

                            mNextExercisebutton.setVisibility(View.VISIBLE);

                        }
                    }
                    if (pause){
                        PauseCountDown();
                        pauseLocalMusic();

                    }

                }
            }
        }).negativeText(getResources().getString(R.string.negative_dialog)).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();

                ResumeCountDown();

            }
        }).show();


    }


    public void finishWorkout(){

        PauseCountDown();
        pauseLocalMusic();

        new MaterialDialog.Builder(context)
                .title(getResources().getString(R.string.title_dialog))
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content(getResources().getString(R.string.content_dialog))
                .positiveText(getResources().getString(R.string.positive_dialog)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Log.v(TAG,"dialog finish workout: SI");

                dialog.dismiss();
                ScreenOff();

                startTimer.cancel();

                if (main_timer != null){
                    main_timer.cancel();
                }

                cancelLocalMusic();

                if (media!=null){
                    media.release();
                }

                launchResultsActivity();

            }
        }).negativeText(getResources().getString(R.string.negative_dialog)).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Log.v(TAG,"dialog finish workout: NO");
                dialog.dismiss();

                if (!BUTTON_PAUSE){

                    ResumeCountDown();
                    startLocalMusic();
                }

            }
        }).show();

    }


    public void dialogNextExercise(){

        PauseCountDown();

        new MaterialDialog.Builder(context)
                .title("Desea pasar al siguiente ejercicio?")
                .content("Esta seguro que quiere pasar al siguiente ejercicio?")
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .positiveText(getResources().getString(R.string.positive_dialog)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();


                if (!BUTTON_PAUSE){
                    ResumeCountDown();
                }


                if (MAIN_TIMER){

                    list.smoothScrollToPosition(y+1);
                    y++;
                    mCurrentExerciseText.setText(String.valueOf(y+1));
                    main_timer.cancel();

                    mRepetitionTimerText.setText(String.valueOf(exercises.get(y).getRepetition()));

                    mPositiveText.setText(String.valueOf(exercises.get(y).getTempo_positive()));
                    mIsometricText.setText(String.valueOf(exercises.get(y).getTempo_isometric()));
                    mNegativeText.setText(String.valueOf(exercises.get(y).getTempo_negative()));


                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);

                    if (y == elements-1){
                        mNextExercisebutton.setVisibility(View.GONE);
                    } else{
                        if (y > 0 && exercises_size > 1){
                            mPreviewExerciseButton.setVisibility(View.VISIBLE);
                        }
                    }

                    if (pause){
                        PauseCountDown();
                        pauseLocalMusic();
                    }


                }
            }
        }).negativeText(getResources().getString(R.string.negative_dialog)).onNegative((dialog, which) -> {

            dialog.dismiss();
            ResumeCountDown();


        }).show();
    }


    public void onStart() {
        Log.d(TAG,"onStart");


        if (onPause){
            ResumeCountDown();
        }


    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");

        if (mSpotifyPlayer != null){
            mSpotifyPlayer.onResume();
        }


        if (onPause){
            ResumeCountDown();
        }

    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");

        if (mSpotifyPlayer != null){
            mSpotifyPlayer.onPause();
        }


        PauseCountDown();
        onPause = true;
    }


    @Override
    public void onStop() {

    }


    @Override
    public void onDestroy() {

        if (mSpotifyPlayer != null){
            mSpotifyPlayer.onDestroy();
        }


        if (MAIN_TIMER && mMainTimer != null){mMainTimer.cancel();}

        if (mExerciseAudioPlayer!=null){mExerciseAudioPlayer.release();}

    }


   

}
