package com.app.proj.silverbars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.app.proj.silverbars.Utilities.SongArtist;
import static com.app.proj.silverbars.Utilities.SongName;
import static com.app.proj.silverbars.Utilities.getExerciseAudioName;
import static com.app.proj.silverbars.Utilities.quitarMp3;

public class WorkingOutActivity extends AppCompatActivity implements View.OnClickListener, Player.NotificationCallback, ConnectionStateCallback {

    private static final String TAG ="WorkingOut ACTIVITY";
    private static final String CLIENT_ID = "20823679749441aeacf4e601f7d12270";


    static MediaPlayer mp;
    ArrayList<File> mySongs;
    ArrayList<File> playlist;
    private ImageButton btPlay;
    private ImageButton btPause;
    private Uri u;
    private int x = 0, y=0, elements = 0, ActualTimeMain=0, tempo = 0, actualReps, Format_Time,Format_Time_Rest,ActualTimeRest=0;
    private int totalTime;
    private TextView Rep_timer_text;
    private TextView song_name, artist_name;
    private TextView CurrentSet;
    private TextView CurrentExercise;
    private TextView RestCounter_text;
    private TextView headerText;

    private CountDownTimer main_timer, restTimer, startTimer;

    private LinearLayout ModalLayout;

    ImageButton pause_workout_button;
    ImageButton finish_workout_button;
    ImageButton play_workout_button;

    private boolean Songs_from_Phone = false, finish = false, INITIAL_TIMER = false,main = false,MAIN_TIMER = false;
    private RecyclerView recycler;
    private int TotalSets = 0, ActualSets = 0, Time_aux = 0,actualRest = 0,actual_start_time = 0;

    private MediaPlayer media;

    private boolean VibrationPerSet = false,VibrationPerRep = false, pause=false,rest = false;
    private static final String FORMAT = "%2d";

    private int exercises_size = 0;
    private int RestByExercise = 0,RestBySet = 0;


    TextView positive,negative,isometric;

    String spotify_playlist;

    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;
    private BroadcastReceiver mNetworkStateReceiver;
    private Metadata mMetadata;
    String Token;

    Boolean Music_Spotify = false,play_exercise_audio = false,BUTTON_PAUSE = false,onPause = false;

    ArrayList<ExerciseRep> exercisesforRecycler = new ArrayList<>();

    ImageButton prvLayout,nxtLayout;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            logStatus("OK!");
        }

        @Override
        public void onError(Error error) {
            logStatus("ERROR:" + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);
        Intent i = getIntent();
        Bundle b = i.getExtras();

        exercisesforRecycler = b.getParcelableArrayList("exercises");
        RestByExercise =  b.getInt("RestByExercise");
        RestBySet = b.getInt("RestBySet");
        VibrationPerRep = b.getBoolean("VibrationPerRep");
        VibrationPerSet =  b.getBoolean("VibrationPerSet");
        play_exercise_audio = b.getBoolean("play_exercise_audio");
        TotalSets = b.getInt("Sets");
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        spotify_playlist = b.getString("playlist_spotify");
        Token = b.getString("token");

        // inicialize tempo
        tempo = exercisesforRecycler.get(y).getTempo_positive() + exercisesforRecycler.get(y).getTempo_isometric() + exercisesforRecycler.get(y).getTempo_negative();

        // spotify inicialization
        if (spotify_playlist != null && Token != null){
            configPlayerSpotify(Token);
        }

        // Inicialize variables
        String[] song_names = b.getStringArray("songs");
        exercises_size = exercisesforRecycler.size();
        playlist = new ArrayList<>();

        // contadores de descanso y repeticiones actuales
        actualReps = exercisesforRecycler.get(0).getRepetition();
        actualRest = RestByExercise;


        artist_name = (TextView) findViewById(R.id.artist_name);
        CurrentSet = (TextView) findViewById(R.id.CurrentSet);
        TextView totalSet = (TextView) findViewById(R.id.TotalSet);
        CurrentExercise = (TextView) findViewById(R.id.CurrentExercise);

        //tempo text
        positive = (TextView) findViewById(R.id.positive);
        isometric = (TextView) findViewById(R.id.isometric);
        negative = (TextView) findViewById(R.id.negative);

        //positivie,negative and isometric
        positive.setText(String.valueOf(exercisesforRecycler.get(0).getTempo_positive()));
        isometric.setText(String.valueOf(exercisesforRecycler.get(0).getTempo_isometric()));
        negative.setText(String.valueOf(exercisesforRecycler.get(0).getTempo_negative()));

        // controls UI
        prvLayout = (ImageButton) findViewById(R.id.prvExercise);
        nxtLayout = (ImageButton) findViewById(R.id.nxtExercise);
        nxtLayout.setVisibility(View.VISIBLE);


        // buttons pause and finish workout
        pause_workout_button = (ImageButton) findViewById(R.id.PauseButton);
        finish_workout_button = (ImageButton) findViewById(R.id.Endbutton);
        play_workout_button = (ImageButton) findViewById(R.id.PlayButton);


        LinearLayout playerLayout = (LinearLayout) findViewById(R.id.PlayerLayout);

        // Rest modal
        ModalLayout = (LinearLayout) findViewById(R.id.ModalLayout);


        headerText = (TextView) findViewById(R.id.headerText);
        RestCounter_text = (TextView) findViewById(R.id.RestCounter_text);

        final TextView totalExercise = (TextView) findViewById(R.id.TotalExercise);

        song_name = (TextView) findViewById(R.id.song_name);
        song_name.setSelected(true);

        //repetiton text
        Rep_timer_text = (TextView) findViewById(R.id.Rep_timer_text);

        //inicializar rep text
        Rep_timer_text.setText(String.valueOf(exercisesforRecycler.get(0).getRepetition()));

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

                        recycler.smoothScrollToPosition(y);
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
                            recycler.smoothScrollToPosition(y);
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
                    Log.v(TAG,"Main TIMER: INICIADO");

                    nxtLayout.setEnabled(true);
                    prvLayout.setEnabled(true);

                    MAIN_TIMER = true;
                    ScreenOn();
                    asignTotalTime(y);

                    startMainCountDown(totalTime,1,totalTime);

                    if (Songs_from_Phone && MAIN_TIMER){StartMusic();}

                    if (spotify_playlist != null && Token != null){
                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }

                    ModalLayout.setVisibility(View.GONE);

                }else if (actualRest == 0 && MAIN_TIMER){
                    Log.v(TAG,"Descanso: Terminado");

                    nxtLayout.setEnabled(true);
                    prvLayout.setEnabled(true);

                    restTimer.cancel();
                    rest = false;

                    playExerciseAudio(exercisesforRecycler.get(y).getExercise().getExercise_audio());
                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);
                    Rep_timer_text.setText(String.valueOf(exercisesforRecycler.get(y).getRepetition()));


                    positive.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_positive()));
                    isometric.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_isometric()));
                    negative.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_negative()));


                    ModalLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        playerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {

                if (Music_Spotify){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.skipToPrevious(mOperationCallback);

                    }else {

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }


                } else if (Songs_from_Phone){
                    
                    int playlist_size = playlist.size();
                    if (playlist_size > 1 && (x-1) >= 0 ){
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        
                        CancelMusic();
                        
                        x = (x-1)%playlist.size();
                        u = Uri.parse(playlist.get(x).toString());

                        String songName = SongName(WorkingOutActivity.this,playlist.get(x));
                        song_name.setText(quitarMp3(songName));
                        String artist = SongArtist(WorkingOutActivity.this,playlist.get(x));

                        artist_name.setText(artist);
                        mp = MediaPlayer.create(getApplicationContext(),u);
                        
                        StartMusic();
                    } else {
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        CancelMusic();
                        u = Uri.parse(playlist.get(x).toString());
                        mp = MediaPlayer.create(getApplicationContext(),u);
                        StartMusic();
                    }
                }
            }

            public void onSwipeLeft() {
                if (Music_Spotify){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.skipToNext(mOperationCallback);

                    }else {

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }

                } else if (Songs_from_Phone) {
                    int playlist_size = playlist.size();
                    if (playlist_size > 1 && x + 1 < playlist_size) {
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        mp.release();
                        x = (x + 1) % playlist.size();
                        u = Uri.parse(playlist.get(x).toString());

                        String songName = SongName(WorkingOutActivity.this,playlist.get(x));

                        song_name.setText(songName);

                        String artist = SongArtist(WorkingOutActivity.this,playlist.get(x));

                        artist_name.setText(artist);

                        mp = MediaPlayer.create(getApplicationContext(), u);
                        mp.start();
                    } else {
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        mp.release();
                        u = Uri.parse(playlist.get(x).toString());
                        mp = MediaPlayer.create(getApplicationContext(), u);
                        mp.start();
                    }
                }
            }
        });

        pause_workout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!pause){
                    
                    BUTTON_PAUSE = true;
                    PauseCountDown();

                    pause_workout_button.setVisibility(View.GONE);
                    play_workout_button.setVisibility(View.VISIBLE);
                    finish_workout_button.setVisibility(View.VISIBLE);


                    PauseMusic();
                    ScreenOff();
                }

                Log.v(TAG,"BUTTON PAUSE: "+BUTTON_PAUSE);
            }
        });

        play_workout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pause){
                    BUTTON_PAUSE = false;

                    pause_workout_button.setVisibility(View.VISIBLE);
                    play_workout_button.setVisibility(View.GONE);
                    finish_workout_button.setVisibility(View.GONE);

                    ScreenOn();
                    ResumeCountDown();
                    StartMusic();

                }

            }
        });



        finish_workout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFinalize();
            }
        });


        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        RecyclerView.OnItemTouchListener disable = new RecyclerViewTouch();
        recycler.addOnItemTouchListener(disable); // disables scolling

        // recycler en linear
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(lManager);


        // Crear un nuevo adaptador
        RecyclerView.Adapter adapter = new ExerciseWorkingOutAdapter(this,exercisesforRecycler);
        recycler.setAdapter(adapter);

        if (adapter.getItemCount() <= 1){
            prvLayout.setVisibility(View.GONE);
            nxtLayout.setVisibility(View.GONE);
        }


        elements = adapter.getItemCount();
        CurrentExercise.setText("1");


        totalExercise.setText(String.valueOf(elements));
        totalSet.setText(String.valueOf(TotalSets));


        totalSet.setText(String.valueOf(TotalSets));
        CurrentSet.setText("0");

        prvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                                recycler.smoothScrollToPosition(y-1);
                                y--;
                                CurrentExercise.setText(String.valueOf(y+1));
                                main_timer.cancel();
                                Rep_timer_text.setText(String.valueOf(exercisesforRecycler.get(y).getRepetition()));

                                positive.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_positive()));
                                isometric.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_isometric()));
                                negative.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_negative()));

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
                                    PauseMusic();
                                    
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
        });



        nxtLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                            recycler.smoothScrollToPosition(y+1);
                            y++;
                            CurrentExercise.setText(String.valueOf(y+1));
                            main_timer.cancel();

                            Rep_timer_text.setText(String.valueOf(exercisesforRecycler.get(y).getRepetition()));

                            positive.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_positive()));
                            isometric.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_isometric()));
                            negative.setText(String.valueOf(exercisesforRecycler.get(y).getTempo_negative()));


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
                                PauseMusic();
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
        });

        // music controls
        btPlay = (ImageButton)findViewById(R.id.btnPlay);
        btPause = (ImageButton)findViewById(R.id.btnPause);
        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);

        if (mySongs != null && mySongs.size() > 0 && song_names != null){

            Songs_from_Phone = true;
            for(int j = 0; j < mySongs.size(); j++){
                for(int z = 0; z < song_names.length; z++)
                    if (Objects.equals(song_names[z], SongName(this,mySongs.get(j)))){
                        z++;
                        playlist.add(mySongs.get(j));
                    }
            }


            u = Uri.parse(playlist.get(x).toString());

            String songName = SongName(this,playlist.get(x));
            song_name.setText(quitarMp3(songName));

            String artist = SongArtist(WorkingOutActivity.this,playlist.get(x));
            artist_name.setText(artist);

            Log.v(TAG, (String) song_name.getText());

            mp = MediaPlayer.create(getApplicationContext(),u);
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            final int playlist_size = playlist.size();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playMusic(playlist_size);
                }
            });

        } else if (spotify_playlist != null && Token != null){
            Log.v(TAG,"musica spotify");
            Music_Spotify = true;

        } else {

            playerLayout.setVisibility(View.GONE);
            song_name.setText(getResources().getString(R.string.no_song));
            btPlay.setEnabled(false);
            btPlay.setClickable(false);
            btPause.setEnabled(false);
            btPause.setClickable(false);
        }

        actual_start_time = 5+1;
        startInicialTimer(actual_start_time);
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
        nxtLayout.setEnabled(false);
        prvLayout.setEnabled(false);

        Log.v(TAG,"Descanso: Empezo");
        main_timer.cancel();
        rest = true;

        actualRest = rest_time;
        ModalLayout.setVisibility(View.VISIBLE);
        headerText.setText(getResources().getString(R.string.rest_text));

        startRestTimer(rest_time);
    }

    
    private void startRestTimer(int actualrest){
        Log.v(TAG, "Timer descanso: activado: "+actualrest);
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
            Log.v(TAG,"Main CountDown: activado");

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

    private void PlayerPause(){
        btPlay.setVisibility(View.VISIBLE);
        btPause.setVisibility(View.GONE);

    }
    private void PlayerPlay(){
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);


    }

    private void PauseMusic(){if (Songs_from_Phone) {mp.pause();}}

    private void CancelMusic(){if (Songs_from_Phone) {mp.release();}}

    private void StartMusic(){if (Songs_from_Phone) {mp.start();}}

    
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnPlay:
                if (Music_Spotify){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isActiveDevice){
                        mPlayer.resume(mOperationCallback);
                        PlayerPlay();

                    }else {

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }


                }else {
                    StartMusic();
                    PlayerPlay();
                }
                break;
            case R.id.btnPause:

                if (Music_Spotify){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.pause(mOperationCallback);
                        PlayerPause();

                    }

                }else {
                    PauseMusic();
                    PlayerPause();
                }
                break;
        }
    }

    private void PauseCountDown(){
        Log.v(TAG,"PauseCountDown: activado");
        Log.v(TAG,"rest flag: "+rest);
        pause = true;

        if (MAIN_TIMER && main_timer!=null){

            if (!rest){
                ActualTimeMain = Format_Time;
                main_timer.cancel();

            }else {

                Log.v(TAG,"Pausar rest: activado");
                ActualTimeRest = Format_Time_Rest;
                restTimer.cancel();
            }
        }else if (INITIAL_TIMER) {

            startTimer.cancel();

        }

    }// PauseCountDown


    private void ResumeCountDown(){
        pause = false;
        Log.v(TAG,"ResumeCountDown: activado");
        Log.v(TAG,"rest flag: "+rest);

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
    private void asignTotalTime(int position){
        totalTime = (exercisesforRecycler.get(position).getRepetition() * tempo) + 5;

      /*  Log.v(TAG,"totalTime: "+totalTime);
        Log.v(TAG,"Exercises_reps: "+Exercises_reps[song_names]);
        Log.v(TAG,"tempo: "+tempo);*/
    }



    private void playMusic(final int playlist_size){
        if (playlist_size>1){
            x = (x+1)%playlist.size();
            u = Uri.parse(playlist.get(x).toString());
            String songName = SongName(this,playlist.get(x));
            song_name.setText(quitarMp3(songName));
            String artist = SongArtist(this,playlist.get(x));
            artist_name.setText(artist);

            mp = MediaPlayer.create(getApplicationContext(),u);
            mp.start();

        }else{
            u = Uri.parse(playlist.get(x).toString());
            mp = MediaPlayer.create(getApplicationContext(),u);
            mp.start();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playMusic(playlist_size);
            }
        });
    }


    private void playExerciseAudio(String exercise_audio_file){
        if (play_exercise_audio){
            Log.v(TAG,"playExerciseAudio: "+exercise_audio_file);

            media = new MediaPlayer();
            final int maxVolume = 100;
            final float volumeFull = (float)(Math.log(maxVolume)/Math.log(maxVolume));;
            final float volumeHalf = (float)(Math.log(maxVolume-90)/Math.log(maxVolume));

            if (media.isPlaying()) {
                media.stop();
                media.release();
                media = new MediaPlayer();
            }


            try {

                String[] audioDir = exercisesforRecycler.get(y).getExercise().getExercise_audio().split("exercises");

                if (audioDir.length == 2){

                    media = MediaPlayer.create(this,Uri.parse(getFilesDir()+"/SilverbarsMp3/"+getExerciseAudioName(exercisesforRecycler.get(y).getExercise().getExercise_audio())));

                }else {

                    String[] mp3dir = exercise_audio_file.split("/SilverbarsMp3/");
                    media = MediaPlayer.create(this, Uri.parse(getFilesDir()+"/SilverbarsMp3/"+mp3dir[1]));
                }

            } catch (Exception e) {
                Log.e(TAG,"Exception",e);
            }

            media.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                public void onPrepared(MediaPlayer arg0) {
                    Log.e("ready!","ready!");
                    if (mp!=null){
                        if (mp.isPlaying())
                            mp.setVolume(0.04f,0.04f);
                    }
                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.pause(mOperationCallback);
                    }
                    media.start();
                }} );

            media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mp!=null && mp.isPlaying()) {
                        mp.setVolume(volumeFull, volumeFull);
                        mediaPlayer.release();
                    }
                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isActiveDevice){
                        mediaPlayer.release();
                        mPlayer.resume(mOperationCallback);
                    }
                }
            });

        }

    }


    private void DialogFinalize(){

        PauseCountDown();
        PauseMusic();


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

                    CancelMusic();

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
                        StartMusic();
                    }

            }
        }).show();


    }


    private void launchResultsActivity(){
        Intent intent = new Intent(WorkingOutActivity.this, ResultsActivity.class);
        intent.putParcelableArrayListExtra("exercises", exercisesforRecycler);
        startActivity(intent);
        finish();
    }

    private void ScreenOn(){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void ScreenOff(){getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}

    private void configPlayerSpotify(String token){

        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), token, CLIENT_ID);

            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    Log.v(TAG,"-- Player initialized --");
                    mPlayer.setConnectivityStatus(mOperationCallback,getNetworkConnectivity(WorkingOutActivity.this));
                    mPlayer.addConnectionStateCallback(WorkingOutActivity.this);
                    mPlayer.addNotificationCallback(WorkingOutActivity.this);
                }
                @Override
                public void onError(Throwable error) {
                    Log.e(TAG,"Error in initialization: " + error.getMessage());
                }
            });
        } else {
            mPlayer.login(Token);
        }
    }

    public void startPlaySpotify(String uri_song) {
        logStatus("Starting playback for " + uri_song);
        mPlayer.playUri(mOperationCallback,uri_song,0,0);
    }

    private void updateView() {
        Log.v(TAG,"mCurrentPlaybackState: "+mCurrentPlaybackState);
        Log.v(TAG,"isLoggedIn: "+isLoggedIn());

        if (mMetadata != null && mMetadata.currentTrack != null) {
            song_name.setText(mMetadata.currentTrack.name);
            artist_name.setText(mMetadata.currentTrack.artistName);
            //final String durationStr = String.format(" (%dms)", mMetadata.currentTrack.durationMs);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");

        if (onPause){
            ResumeCountDown();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");


        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    Log.v(TAG,"Network state changed: " + connectivity.toString());
                    mPlayer.setConnectivityStatus(mOperationCallback,connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(this);
            mPlayer.addConnectionStateCallback(this);
        }

        if (onPause){
            ResumeCountDown();
        }

    }
    @Override
    protected void onPause() {
        Log.v(TAG,"onPause");
        
        PauseCountDown();
        onPause = true;
        
        unregisterReceiver(mNetworkStateReceiver);
        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(this);
            mPlayer.removeConnectionStateCallback(this);
        }

        super.onPause();
    }

    @Override
    public void onBackPressed(){
        Log.v(TAG,"BUTTON BACK PRESSED: CLICK");
        DialogFinalize();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG,"onDestroy");
        ScreenOff();

        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isActiveDevice){
            Spotify.destroyPlayer(this);
        }

        if (MAIN_TIMER && main_timer != null){
            main_timer.cancel();
        }

        if (media!=null){
            media.release();
        }

        super.onDestroy();
    }


    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }

    private void logStatus(String status) {
        Log.i("SpotifySdkDemo", status);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent event) {

        logStatus("Player event: " + event);
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        mMetadata = mPlayer.getMetadata();
        logStatus("Player state: " + mCurrentPlaybackState);
        logStatus("Metadata: " + mMetadata);

        updateView();
    }

    @Override
    public void onPlaybackError(Error error) {
        logStatus("Player error: " + error);
    }
    @Override
    public void onLoggedIn() {
        Log.d(TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d(TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "Temporary error occurred");
    }
    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);

    }

}