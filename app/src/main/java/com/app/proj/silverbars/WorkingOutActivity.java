package com.app.proj.silverbars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.app.proj.silverbars.Utilities.SongArtist;
import static com.app.proj.silverbars.Utilities.SongName;
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

    private FrameLayout prvLayout;
    private FrameLayout nxtLayout;
    private RelativeLayout ModalLayout;
    private Button PauseButton;
    private boolean SelectedSongs = false, finish = false, START_TIMER = false,main = false,MAIN_TIMER = false;
    private RecyclerView recycler;
    private int TotalSets = 0, ActualSets = 0, Time_aux = 0,actualRest = 0,actual_start_time = 0;
    AlertDialog alertDialog;
    private MediaPlayer media;
    private JsonExercise[] Exercises;
    private int[] Exercises_reps;
    private boolean VibrationPerSet = false,VibrationPerRep = false, pause=false,rest = false;
    private static final String FORMAT = "%2d";

    private int exercises_size = 0;
    private int RestByExercise = 0,RestBySet = 0;
    private int [] Positive,Negative,Isometric;

    TextView positive,negative,isometric;

    String spotify_playlist;

    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;
    private BroadcastReceiver mNetworkStateReceiver;
    private Metadata mMetadata;
    String Token;

    Boolean Spotify_ = false,DownloadAudioExercise = false;

    // Inicializar Workouts
    List<WorkoutInfo> exercisesforRecycler = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        RestByExercise =  b.getInt("RestByExercise");
        RestBySet = b.getInt("RestBySet");
        Exercises_reps = b.getIntArray("ExercisesReps");
        VibrationPerRep = b.getBoolean("VibrationPerRep");
        VibrationPerSet =  b.getBoolean("VibrationPerSet");
        DownloadAudioExercise = b.getBoolean("audio_exercise");

        TotalSets = b.getInt("Sets");

        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        Log.v(TAG,"mysongs"+mySongs);

        spotify_playlist = b.getString("playlist_spotify");
        Token = b.getString("token");

        Log.v(TAG,"spotify_playlist: "+spotify_playlist);
        Log.v(TAG,"Token: "+Token);

        if (spotify_playlist != null && Token != null){
            configPlayerSpotify(Token);

        }

        String[] song_names = b.getStringArray("pos");

        Positive = b.getIntArray("Array_Positive_Exercises");
        Isometric =  b.getIntArray("Array_Isometric_Exercises");
        Negative =  b.getIntArray("Array_Negative_Exercises");

        Exercises = WorkoutActivity.ParsedExercises;

        exercises_size = Exercises.length;


        // tempo config
        tempo = Positive[y] + Isometric[y] + Negative[y];

        //Log.v("Songs", Arrays.toString(song_names));
        playlist = new ArrayList<>();

        // contadores de descanso y repeticiones actuales
        actualReps = Exercises_reps[0];
        actualRest = RestByExercise;

        artist_name = (TextView) findViewById(R.id.artist_name);
        CurrentSet = (TextView) findViewById(R.id.CurrentSet);
        TextView totalSet = (TextView) findViewById(R.id.TotalSet);
        CurrentExercise = (TextView) findViewById(R.id.CurrentExercise);


        //tempo text
        positive = (TextView) findViewById(R.id.positive);
        isometric = (TextView) findViewById(R.id.isometric);
        negative = (TextView) findViewById(R.id.negative);

        positive.setText(String.valueOf(Positive[0]));
        isometric.setText(String.valueOf(Isometric[0]));
        negative.setText(String.valueOf(Negative[0]));

        // next and preview button for exercise
        ImageButton prvExercise = (ImageButton) findViewById(R.id.prvExercise);
        ImageButton nxtExercise = (ImageButton) findViewById(R.id.nxtExercise);

        prvLayout = (FrameLayout) findViewById(R.id.prvLayout);
        nxtLayout = (FrameLayout) findViewById(R.id.nxtLayout);
        nxtLayout.setVisibility(View.VISIBLE);

        // pause and finish workout controls
        PauseButton = (Button) findViewById(R.id.PauseButton);
        Button endbutton = (Button) findViewById(R.id.Endbutton);


        LinearLayout playerLayout = (LinearLayout) findViewById(R.id.PlayerLayout);



        // Rest modal
        ModalLayout = (RelativeLayout) findViewById(R.id.ModalLayout);


        headerText = (TextView) findViewById(R.id.headerText);
        RestCounter_text = (TextView) findViewById(R.id.RestCounter_text);

        final TextView totalExercise = (TextView) findViewById(R.id.TotalExercise);




        song_name = (TextView) findViewById(R.id.song_name);
        song_name.setSelected(true);

        //repetiton text
        Rep_timer_text = (TextView) findViewById(R.id.Rep_timer_text);

        //inicializar rep text
        Rep_timer_text.setText(String.valueOf(Exercises_reps[0]));


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

                    }
                    else{
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
                        }
                        else{
                            main_timer.cancel();
                            ScreenOff();
                            Log.v(TAG,"results activity here");
                            startActivity(new Intent(WorkingOutActivity.this,ResultsActivity.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        RestCounter_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (actual_start_time == 0){
                    Log.v(TAG,"START TIMER: INICIADO");
                    ScreenOn();
                    asignTotalTime(y);

                    MAIN_TIMER = true;
                    startMainCountDown(totalTime,1,totalTime);

                    if (SelectedSongs && MAIN_TIMER){
                        mp.start();
                    }
                    if (spotify_playlist != null && Token != null){

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();

                    }

                    ModalLayout.setVisibility(View.GONE);
                }

                //cuando el descanso se acaba
                if (actualRest == 0){
                    Log.v(TAG,"Descanso: Terminado");
                    restTimer.cancel();
                    rest = false;

                    playExerciseAudio(Exercises[y].getExercise_audio());
                    asignTotalTime(y);
                    startMainCountDown(totalTime,1,totalTime);
                    Rep_timer_text.setText(String.valueOf(Exercises_reps[y]));


                    positive.setText(String.valueOf(Positive[y]));
                    isometric.setText(String.valueOf(Isometric[y]));
                    negative.setText(String.valueOf(Negative[y]));


                    ModalLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //change to next music with swipe
        playerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {

                if (Spotify_){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.skipToPrevious();

                    }else {

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }




                }else if (SelectedSongs){
                    int playlist_size = playlist.size();
                    if (playlist_size > 1 && (x-1) >= 0 ){
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        mp.release();
                        x = (x-1)%playlist.size();
                        u = Uri.parse(playlist.get(x).toString());

                        String songName = SongName(WorkingOutActivity.this,playlist.get(x));
                        song_name.setText(quitarMp3(songName));
                        String artist = SongArtist(WorkingOutActivity.this,playlist.get(x));

                        artist_name.setText(artist);
                        mp = MediaPlayer.create(getApplicationContext(),u);
                        mp.start();
                    }
                    else{
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        mp.release();
                        u = Uri.parse(playlist.get(x).toString());
                        mp = MediaPlayer.create(getApplicationContext(),u);
                        mp.start();
                    }
                }
            }

            public void onSwipeLeft() {
                if (Spotify_){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.skipToNext();

                    }else {

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }

                } else if (SelectedSongs) {
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

        PauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG,"BUTTON PAUSE: CLICK");
                if (!pause){
                    PauseCountDown();
                    PauseButton.setText(getResources().getString(R.string.resume_workout_text));

                    if (SelectedSongs){
                        mp.pause();
                    }
                    ScreenOff();

                }else{
                    ScreenOn();

                    Log.v(TAG,"BUTTON PAUSE: DESACTIVADO");
                    PauseButton.setText(getResources().getString(R.string.pause_workout_text));

                    //seguir contando
                    ResumeCountDown();
                    if (SelectedSongs) {
                        mp.start();//seguir musica
                    }

                }
            }
        });

        endbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"BUTTON END WORKOUT: CLICK");
                DialogFinalize();
            }
        });


        for (JsonExercise Exercise : Exercises) {
            exercisesforRecycler.add(new WorkoutInfo(Exercise.getExercise_name(), null,Exercise.getExercise_image()));
        }


        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        RecyclerView.OnItemTouchListener disable = new RecyclerViewTouch();
        recycler.addOnItemTouchListener(disable); // disables scolling

        // recycler en linear
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(lManager);


        // Crear un nuevo adaptador
        RecyclerView.Adapter adapter = new WorkoutsAdapter(exercisesforRecycler, getApplicationContext());
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

        prvExercise.setOnClickListener(new View.OnClickListener() {
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
                            recycler.smoothScrollToPosition(y-1);
                            y--;

                            CurrentExercise.setText(String.valueOf(y+1));
                            main_timer.cancel();
                            Rep_timer_text.setText(String.valueOf(Exercises_reps[y]));


                            positive.setText(String.valueOf(Positive[y]));
                            isometric.setText(String.valueOf(Isometric[y]));
                            negative.setText(String.valueOf(Negative[y]));

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
                                if (SelectedSongs) {
                                    mp.pause();
                                }
                            }


                    }
                })
                        .negativeText(getResources().getString(R.string.negative_dialog)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        ResumeCountDown();

                    }
                })
                        .show();

            }
        });


        //darle al siguiente ejercicio
        nxtExercise.setOnClickListener(new View.OnClickListener() {
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
                            ResumeCountDown();
                            // String state = PauseButton.getText().toString();
                            recycler.smoothScrollToPosition(y+1);
                            y++;
                            CurrentExercise.setText(String.valueOf(y+1));
                            main_timer.cancel();
                            Rep_timer_text.setText(String.valueOf(Exercises_reps[y]));

                            positive.setText(String.valueOf(Positive[y]));
                            isometric.setText(String.valueOf(Isometric[y]));
                            negative.setText(String.valueOf(Negative[y]));

                            asignTotalTime(y);
                            startMainCountDown(totalTime,1,totalTime);
                            if (y == elements-1){
                                nxtLayout.setVisibility(View.GONE);
                            }
                            else{
                                if (y > 0 && exercises_size > 1){
                                    prvLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            if (pause){
                                PauseCountDown();
                                if (SelectedSongs) {
                                    mp.pause();
                                }
                            }


                    }
                })
                        .negativeText(getResources().getString(R.string.negative_dialog)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        ResumeCountDown();
                    }
                })
                        .show();

            }
        });

        // music controls
        btPlay = (ImageButton)findViewById(R.id.btnPlay);
        btPause = (ImageButton)findViewById(R.id.btnPause);
        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);

        if (mySongs != null && mySongs.size() > 0 && song_names != null){

            SelectedSongs = true;
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
            Spotify_ = true;

        } else {

            playerLayout.setVisibility(View.GONE);
            song_name.setText(getResources().getString(R.string.no_song));
            btPlay.setEnabled(false);
            btPlay.setClickable(false);
            btPause.setEnabled(false);
            btPause.setClickable(false);
        }

        actual_start_time = 5;
        startInicialTimer(actual_start_time);
    }


    private void startInicialTimer(int seconds){
        long  sec = (seconds+5) * 1000;

        startTimer = new CountDownTimer(sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Format_Time_Rest =  Math.round(millisUntilFinished * 0.001f);

                actual_start_time--;
                RestCounter_text.setText(String.valueOf(actual_start_time));
            }

            public void onFinish() {}
        }.start();
        START_TIMER = true;
    }

    private int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
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

    private void startMainCountDown(int seconds,int interval, int time_aux){
        if (!pause && MAIN_TIMER){
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
        //Log.v("Time",String.valueOf(Time_aux-tempo)+" / "+Format_Time);

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
                Log.v(TAG, "Vibration Por Rep: cancelado");
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
                Log.v(TAG, "Vibration Por Set: cancelado");
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnPlay:
                if (Spotify_){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isActiveDevice){
                        mPlayer.resume();
                        PlayerPlay();

                    }else {

                        startPlaySpotify(spotify_playlist);
                        PlayerPlay();
                    }


                }else {
                    mp.start();
                    PlayerPlay();
                }
                break;
            case R.id.btnPause:

                if (Spotify_){

                    if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying){
                        mPlayer.pause();
                        PlayerPause();

                    }

                }else {
                    mp.pause();
                    PlayerPause();
                }
                break;
        }
    }

    private void PauseCountDown(){
        pause = true;

        Log.v(TAG,"PauseCountDown: activado");
        Log.v(TAG,"rest flag: "+rest);

        if (MAIN_TIMER && main_timer!=null){

            if (!rest){
                ActualTimeMain = Format_Time;
                main_timer.cancel();

            }else {

                Log.v(TAG,"Pausar rest: activado");
                ActualTimeRest = Format_Time_Rest;
                restTimer.cancel();
            }
        }else if (START_TIMER) {

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
            if (START_TIMER){
                startInicialTimer(actual_start_time);
            }

        }


    }
    private void asignTotalTime(int song_names){
        totalTime = (Exercises_reps[song_names] * tempo) + 5;

        Log.v(TAG,"totalTime: "+totalTime);
        Log.v(TAG,"Exercises_reps[song_names]: "+Exercises_reps[song_names]);
        Log.v(TAG,"tempo: "+tempo);
    }

    private void playExerciseAudio(String file){
        Log.v(TAG,"playExerciseAudio: "+file);

        if (DownloadAudioExercise){

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
                String[] audioDir = Exercises[y].getExercise_audio().split("exercises");

                if (audioDir.length == 2){

                    String Parsedurl = "exercises"+audioDir[1];
                    Log.v(TAG,"Parsedurl: "+Parsedurl);
                    String[] splitName = Parsedurl.split("/");
                    Log.v(TAG,"splitName: "+ Arrays.toString(splitName));
                    String mp3Name = splitName[2];
                    Log.v(TAG,"mp3Name: "+ mp3Name);

                    media = MediaPlayer.create(this,Uri.parse(getFilesDir()+"/SilverbarsMp3/"+mp3Name));

                }else {

                    String[] mp3dir = file.split("/SilverbarsMp3/");
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
                        mPlayer.pause();
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
                        mPlayer.resume();
                    }
                }
            });

        }

    }

    private void showRestModal(int rest_time){
        Log.v(TAG,"Descanso: Empezo");
        main_timer.cancel();
        rest = true;

        actualRest = rest_time;
        ModalLayout.setVisibility(View.VISIBLE);
        headerText.setText(getResources().getString(R.string.rest_text));


        startRestTimer(rest_time);
    }

    private void startRestTimer(int actualrest){
        Log.v(TAG, "startRestTimer: activado");

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

    private void PauseMusic(){if (SelectedSongs) {mp.pause();}}

    private void CancelMusic(){if (SelectedSongs) {mp.release();}}

    private void ResumeMusic(){if (SelectedSongs) {mp.start();}}


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

                    finish();

            }
        })
                .negativeText(getResources().getString(R.string.negative_dialog)).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Log.v(TAG,"dialog finish workout: NO");
                    dialog.dismiss();

                    ResumeCountDown();
                    ResumeMusic();

            }
        })
                .show();
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
                    mPlayer.setConnectivityStatus(getNetworkConnectivity(WorkingOutActivity.this));
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

        mPlayer.play(uri_song,0,0);

    }

    private void updateView() {

        Log.v(TAG,"mCurrentPlaybackState: "+mCurrentPlaybackState);
        Log.v(TAG,"isLoggedIn: "+isLoggedIn());

        if (mMetadata != null && mMetadata.currentTrack != null) {
            song_name.setText(mMetadata.currentTrack.name);
            artist_name.setText(mMetadata.currentTrack.artistName);
            final String durationStr = String.format(" (%dms)", mMetadata.currentTrack.durationMs);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");


        // Set up the broadcast receiver for network events. Note that we also unregister
        // this receiver again in onPause().
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    Log.v(TAG,"Network state changed: " + connectivity.toString());
                    mPlayer.setConnectivityStatus(connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(WorkingOutActivity.this);
            mPlayer.addConnectionStateCallback(WorkingOutActivity.this);
        }

        if (pause){
            ResumeCountDown();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
        PauseCountDown();

        unregisterReceiver(mNetworkStateReceiver);

        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(WorkingOutActivity.this);
            mPlayer.removeConnectionStateCallback(WorkingOutActivity.this);
        }

    }

    @Override
    public void onBackPressed(){
        Log.v(TAG,"BUTTON BACK PRESSED: CLICK");
        DialogFinalize();
    }

    @Override
    protected void onDestroy() {
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
        // Remember kids, always use the English locale when changing case for non-UI strings!
        // Otherwise you'll end up with mysterious errors when running in the Turkish locale.
        // See: http://java.sys-con.com/node/46241
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