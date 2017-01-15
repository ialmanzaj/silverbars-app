package com.app.proj.silverbars.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.proj.silverbars.utils.OnSwipeTouchListener;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.RecyclerViewTouch;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.adapters.ExerciseWorkingOutAdapter;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.viewsets.WorkingOutView;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;


public class WorkingOutActivity extends AppCompatActivity implements View.OnClickListener,WorkingOutView{

    private static final String TAG = WorkingOutActivity.class.getSimpleName();


    @BindView(R.id.PlayerLayout) LinearLayout mPlayerLayout;

    @BindView(R.id.play_music) ImageButton mPlaybutton;
    @BindView(R.id.pause_music) ImageButton mPausebutton;

    @BindView(R.id.repetition_timer) TextView Rep_timer_text;
    
    @BindView(R.id.song_name) TextView mSongName;
    @BindView(R.id.artist_name) TextView mArtistName;
    
    @BindView(R.id.current_set) TextView CurrentSet;
    @BindView(R.id.current_exercise) TextView CurrentExercise;
    
    @BindView(R.id.RestCounter_text) TextView RestCounter_text;
    @BindView(R.id.headerText) TextView headerText;

    @BindView(R.id.modal_overlay) LinearLayout mModalOverlayView;

    @BindView(R.id.pause_workout) ImageButton mPauseWorkoutButton;
    @BindView(R.id.finish_workout) ImageButton mFinishWorkoutButton;
    @BindView(R.id.play_workout) ImageButton mPlayWorkoutButton;

    @BindView(R.id.positive) TextView mPositiveText;
    @BindView(R.id.negative) TextView mNegativeText;
    @BindView(R.id.isometric) TextView mIsometricText;

    @BindView(R.id.prvExercise) ImageButton prvLayout;
    @BindView(R.id.nxtExercise) ImageButton nxtLayout;

    @BindView(R.id.list) RecyclerView list;
    
    @BindView(R.id.total_exercises) TextView mTotalExercises;
    

    ArrayList<File> mySongsList;
    ArrayList<File> playlist;
    
    private Uri url_music;
    private int position = 0, y=0, elements = 0, ActualTimeMain=0, tempo = 0, actualReps, Format_Time,Format_Time_Rest,ActualTimeRest=0;
    private int totalTime;

    private CountDownTimer main_timer, restTimer, startTimer;

    private boolean Songs_from_Phone = false, finish = false, INITIAL_TIMER = false,main = false,MAIN_TIMER = false;
    
    private int TotalSets = 0, ActualSets = 0, Time_aux = 0,actualRest = 0,actual_start_time = 0;
    
    private boolean VibrationPerSet = false,VibrationPerRep = false, pause=false,rest = false;

    private int exercises_size = 0;
    
    private int RestByExercise = 0,RestBySet = 0;
    
    String spotify_playlist;

    private String mSpotifyToken;

    Boolean Music_Spotify = false,play_exercise_audio = false,BUTTON_PAUSE = false,onPause = false;

    ArrayList<ExerciseRep> exercises = new ArrayList<>();
    
    private Utilities utilities;

    ExerciseWorkingOutAdapter adapter;


    private void getExtras(Bundle extras){
        exercises = extras.getParcelableArrayList("exercises");
        RestByExercise =  extras.getInt("RestByExercise");
        RestBySet = extras.getInt("RestBySet");
        VibrationPerRep = extras.getBoolean("VibrationPerRep");
        VibrationPerSet =  extras.getBoolean("VibrationPerSet");
        play_exercise_audio = extras.getBoolean("play_exercise_audio");
        TotalSets = extras.getInt("Sets");
        mySongsList = (ArrayList) extras.getParcelableArrayList("songlist");
        spotify_playlist = extras.getString("playlist_spotify");
        mSpotifyToken = extras.getString("token");
        String[] song_names = extras.getStringArray("songs");
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);


        Bundle extras = getIntent().getExtras();


        getExtras(extras);





        // inicialize tempo
        tempo = exercises.get(y).getTempo_positive() 
                + exercises.get(y).getTempo_isometric() 
                + exercises.get(y).getTempo_negative();

        
        
        
        // spotify inicialization
        if (spotify_playlist != null && mSpotifyToken != null){
            configPlayerSpotify(mSpotifyToken);
        }


        elements = adapter.getItemCount();
        CurrentExercise.setText("1");

        mTotalExercises.setText(String.valueOf(elements));
        totalSet.setText(String.valueOf(TotalSets));

        totalSet.setText(String.valueOf(TotalSets));
        CurrentSet.setText("0");

        
        list.addOnItemTouchListener(new RecyclerViewTouch()); // disables scolling

        // list en linear
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(lManager);


        // Crear un nuevo adaptador
        adapter = new ExerciseWorkingOutAdapter(this,exercises);
        list.setAdapter(adapter);

        
        
        // Inicialize variables
        
        
        exercises_size = exercises.size();
        playlist = new ArrayList<>();

        // contadores de descanso y repeticiones actuales
        actualReps = exercises.get(0).getRepetition();
        actualRest = RestByExercise;
        

        //positivie,mNegativeText and mIsometricText
        mPositiveText.setText(String.valueOf(exercises.get(0).getTempo_positive()));
        mIsometricText.setText(String.valueOf(exercises.get(0).getTempo_isometric()));
        mNegativeText.setText(String.valueOf(exercises.get(0).getTempo_negative()));


        
        nxtLayout.setVisibility(View.VISIBLE);


        
        mPlaybutton.setOnClickListener(this);
        mPausebutton.setOnClickListener(this);
        
        mSongName.setSelected(true);

        //repetiton text
     

        //inicializar rep text
        Rep_timer_text.setText(String.valueOf(exercises.get(0).getRepetition()));

        Rep_timer_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                actualReps = Integer.valueOf(charSequence.toString());

                if (actualReps == 0){

                    if (y+1 < elements ){
                        y++;

                        if (exercises_size > 1){

                            prvLayout.setVisibility(View.VISIBLE);

                            if((y+1)==elements){
                                nxtLayout.setVisibility(View.GONE);
                            }
                        }

                        list.smoothScrollToPosition(y);
                        CurrentExercise.setText(String.valueOf(y+1));


                        ActivateVibrationPerSet();
                        Rep_timer_text.setEnabled(false);
                        showRestModal(RestByExercise);// descanso por ejercicio

                    } else {
                        // contar sets
                        if (ActualSets+1 <= TotalSets){
                            ActualSets++;
                            CurrentSet.setText(String.valueOf(ActualSets));
                            CurrentExercise.setText(String.valueOf("1"));
                            y = 0;
                            list.smoothScrollToPosition(y);
                            finish = true;

                            showRestModal(RestBySet);//descanso por set

                            if (exercises_size > 1){

                                nxtLayout.setVisibility(View.VISIBLE);
                                prvLayout.setVisibility(View.GONE);

                            }


                        } else {
                            main_timer.cancel();
                            ScreenOff();

                            launchResultsActivity();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        RestCounter_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (actual_start_time == 0 && INITIAL_TIMER){
                    
                    INITIAL_TIMER = false;
                    startTimer.cancel();

                    nxtLayout.setEnabled(true);
                    prvLayout.setEnabled(true);

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

                    nxtLayout.setEnabled(true);
                    prvLayout.setEnabled(true);

                    restTimer.cancel();
                    rest = false;

                    if (play_exercise_audio){
                        playExerciseAudio(exercises.get(y).getExercise().getExercise_audio());
                    }

                    
                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);
                    Rep_timer_text.setText(String.valueOf(exercises.get(y).getRepetition()));


                    mPositiveText.setText(String.valueOf(exercises.get(y).getTempo_positive()));
                    mIsometricText.setText(String.valueOf(exercises.get(y).getTempo_isometric()));
                    mNegativeText.setText(String.valueOf(exercises.get(y).getTempo_negative()));


                    mModalOverlayView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        
        
        mPlayerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {

                onMusicNext();
            }
            @Override
            public void onSwipeLeft() {

                onMusicPreview();

            }
        });
        
        
        mPauseWorkoutButton.setOnClickListener(view -> {
            Log.v(TAG,"BUTTON PAUSE: "+BUTTON_PAUSE);

            onPauseWorkout();

        });

        mPlayWorkoutButton.setOnClickListener(view -> onPlayWorkout());
        
        
        mFinishWorkoutButton.setOnClickListener(v -> DialogFinalize());

        
        if (adapter.getItemCount() <= 1){
            prvLayout.setVisibility(View.GONE);
            nxtLayout.setVisibility(View.GONE);
        }
        
        prvLayout.setOnClickListener(view -> {

            dialogPreviewExercise();

        });

        nxtLayout.setOnClickListener(view -> {

            dialogNextExercise();

        });


        if (mySongsList != null && mySongsList.size() > 0 && song_names != null){

            Songs_from_Phone = true;

            
            playlist = getPlaylist(song_names,mySongsList);
            

            String songName = utilities.getSongName(this,playlist.get(position));
            mSongName.setText(utilities.removeLastMp3(songName));

            String artist = utilities.SongArtist(this,playlist.get(position));

            mArtistName.setText(artist);

            Log.v(TAG, (String) mSongName.getText());


            mPlaybutton.setVisibility(View.GONE);
            mPausebutton.setVisibility(View.VISIBLE);


            url_music = Uri.parse(playlist.get(position).toString());

            mLocalMusicPlayer = MediaPlayer.create(getApplicationContext(),url_music);
            mLocalMusicPlayer.setOnCompletionListener(mediaPlayer -> setupMusicPlayer(playlist.size()));

            
            
        

        } else {

            mPlayerLayout.setVisibility(View.GONE);
            mSongName.setText(getResources().getString(R.string.no_song));
            mPlaybutton.setEnabled(false);
            mPlaybutton.setClickable(false);
            mPausebutton.setEnabled(false);
            mPausebutton.setClickable(false);
        }

        actual_start_time = 5+1;
        startInicialTimer(actual_start_time);
    }
    
    
    private void onPlayWorkout(){
        if (pause){
            BUTTON_PAUSE = false;

            mPauseWorkoutButton.setVisibility(View.VISIBLE);
            mPlayWorkoutButton.setVisibility(View.GONE);
            mFinishWorkoutButton.setVisibility(View.GONE);

            ScreenOn();
            ResumeCountDown();
            startLocalMusic();

        }
    }


    private void onPauseWorkout(){

        if (!pause){

            BUTTON_PAUSE = true;
            PauseCountDown();

            mPauseWorkoutButton.setVisibility(View.GONE);
            mPlayWorkoutButton.setVisibility(View.VISIBLE);
            mFinishWorkoutButton.setVisibility(View.VISIBLE);


            pauseLocalMusic();
            ScreenOff();
        }

    }



   


    private void dialogNextExercise(){

        PauseCountDown();

        new MaterialDialog.Builder(WorkingOutActivity.this)
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
                    CurrentExercise.setText(String.valueOf(y+1));
                    main_timer.cancel();

                    Rep_timer_text.setText(String.valueOf(exercises.get(y).getRepetition()));

                    mPositiveText.setText(String.valueOf(exercises.get(y).getTempo_positive()));
                    mIsometricText.setText(String.valueOf(exercises.get(y).getTempo_isometric()));
                    mNegativeText.setText(String.valueOf(exercises.get(y).getTempo_negative()));


                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);

                    if (y == elements-1){
                        nxtLayout.setVisibility(View.GONE);
                    } else{
                        if (y > 0 && exercises_size > 1){
                            prvLayout.setVisibility(View.VISIBLE);
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

    private void dialogPreviewExercise(){

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
                    CurrentExercise.setText(String.valueOf(y+1));
                    main_timer.cancel();
                    Rep_timer_text.setText(String.valueOf(exercises.get(y).getRepetition()));

                    mPositiveText.setText(String.valueOf(exercises.get(y).getTempo_positive()));
                    mIsometricText.setText(String.valueOf(exercises.get(y).getTempo_isometric()));
                    mNegativeText.setText(String.valueOf(exercises.get(y).getTempo_negative()));

                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);

                    if (y == 0){

                        prvLayout.setVisibility(View.GONE);

                    }
                    else{
                        if(y < elements-1 && exercises_size > 1){

                            nxtLayout.setVisibility(View.VISIBLE);

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


    private void startInicialTimer(int seconds){
        nxtLayout.setEnabled(false);
        prvLayout.setEnabled(false);



        long  sec = (seconds+5) * 1000;
        int sec_interval = 1000;

        startTimer = new CountDownTimer(sec, sec_interval) {
            @Override
            public void onTick(long millisUntilFinished) {

                actual_start_time--;
                RestCounter_text.setText(String.valueOf(actual_start_time));
            }

            public void onFinish() {}
        }.start();
        INITIAL_TIMER = true;
    }


    private void showRestModal(int rest_time){
        //Log.v(TAG,"Descanso: Empezo");

        nxtLayout.setEnabled(false);
        prvLayout.setEnabled(false);

        main_timer.cancel();
        rest = true;

        actualRest = rest_time;
        mModalOverlayView.setVisibility(View.VISIBLE);
        headerText.setText(getResources().getString(R.string.rest_text));

        startRestTimer(rest_time);
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


    //asign time for each repetition
    private void asignTotalTime(int position){
        totalTime = (exercises.get(position).getRepetition() * tempo) + 5;
    }


    private void DialogFinalize(){

        PauseCountDown();
        pauseLocalMusic();


        new MaterialDialog.Builder(this)
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


    private void launchResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putParcelableArrayListExtra("exercises", exercises);
        startActivity(intent);
        finish();
    }

    private void ScreenOn(){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void ScreenOff(){getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}


    private void startRestTimer(int actualrest){
        //Log.d(TAG, "Timer descanso: activado: "+actualrest);
        RestCounter_text.setText(String.valueOf(actualRest));

        int totalsecs = (actualrest + 5) * 1000;
        int sec_interval = 1000;
        Format_Time_Rest = 0;

        restTimer = new CountDownTimer(totalsecs, sec_interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Format_Time_Rest =  Math.round(millisUntilFinished * 0.001f);

                actualRest--;
                RestCounter_text.setText(String.valueOf(actualRest));
            }
            public void onFinish() {}
        }.start();
    }


    private void startMainCountDown(int seconds,int interval, int time_aux){
        if (!BUTTON_PAUSE && !rest){
            //Log.d(TAG,"Main CountDown: activado");

            int totalsecs= seconds * 1000;
            int sec_interval= interval * 1000 ;
            Time_aux = time_aux;
            Format_Time = 0;

            main_timer = new CountDownTimer(totalsecs, sec_interval) {
                public void onTick(long millisUntilFinished) {
                    performTick(millisUntilFinished);
                }
                public void onFinish() {}
            }.start();

        }
    }

    // CONTADOR DE REPETICIONES
    public void performTick(long millisUntilFinished) {
        Format_Time = Math.round(millisUntilFinished * 0.001f);

        if (Time_aux-tempo == Format_Time){
            Time_aux = Time_aux - tempo;
            actualReps--;// restar repeticiones

            Rep_timer_text.setText(String.valueOf(actualReps));//restar reps de texto mostrado
            ActivateVibrationPerRep();//activar vibracion por reps
        }
    }

    private void ActivateVibrationPerRep(){
        if (VibrationPerRep) {
            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (!pause){
                vb.vibrate(250);
            }else {
                vb.cancel();
            }
        }
    }

    private void ActivateVibrationPerSet(){
        if (VibrationPerSet){
            Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
            if (!pause){
                vb.vibrate(1000);
            }else {
                vb.cancel();
            }
        }
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.play_music:
              
                break;
            case R.id.pause_music:
                break;
        }
    }

  
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG,"onBackPressed");
        DialogFinalize();
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    private void onPauseMusicPlayerUI(){
        mPlaybutton.setVisibility(View.VISIBLE);
        mPausebutton.setVisibility(View.GONE);
    }

    private void onPlayMusicPlayerUI(){
        mPlaybutton.setVisibility(View.GONE);
        mPausebutton.setVisibility(View.VISIBLE);
    }



    @Override
    public void updateSongName(String song_name) {
        
    }

    @Override
    public void updateArtistName(String artist_name) {

    }

    @Override
    public void onPauseMusic() {

    }

    @Override
    public void onPlayMusic() {

    }


}
