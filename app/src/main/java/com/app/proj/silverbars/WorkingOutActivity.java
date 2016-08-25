package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.app.proj.silverbars.Utilities.SongArtist;
import static com.app.proj.silverbars.Utilities.SongName;
import static com.app.proj.silverbars.Utilities.getFileReady;
import static com.app.proj.silverbars.Utilities.getUrlReady;
import static com.app.proj.silverbars.Utilities.quitarMp3;

public class WorkingOutActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="WorkingOut ACTIVITY";
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
    private FrameLayout ModalLayout;
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
        TotalSets = b.getInt("Sets");

        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        Log.v(TAG,"mysongs"+mySongs);


        String[] position = b.getStringArray("pos");
        Positive = b.getIntArray("Array_Positive_Exercises");
        Isometric =  b.getIntArray("Array_Isometric_Exercises");
        Negative =  b.getIntArray("Array_Negative_Exercises");

        Exercises = WorkoutActivity.ParsedExercises;

        exercises_size = Exercises.length;
        // tempo config
        tempo = Positive[y] + Isometric[y] + Negative[y];

        //Log.v("Songs", Arrays.toString(position));
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
        
        
        // Rest modal
        ModalLayout = (FrameLayout) findViewById(R.id.ModalLayout);
        headerText = (TextView) findViewById(R.id.headerText);
        RestCounter_text = (TextView) findViewById(R.id.RestCounter_text);

        final TextView totalExercise = (TextView) findViewById(R.id.TotalExercise);

        // next and preview button for exercise
        ImageButton prvExercise = (ImageButton) findViewById(R.id.prvExercise);
        ImageButton nxtExercise = (ImageButton) findViewById(R.id.nxtExercise);

        prvLayout = (FrameLayout) findViewById(R.id.prvLayout);
        nxtLayout = (FrameLayout) findViewById(R.id.nxtLayout);
        nxtLayout.setVisibility(View.VISIBLE);

        // pause and finish workout controls
        PauseButton = (Button) findViewById(R.id.PauseButton);
        Button endbutton = (Button) findViewById(R.id.Endbutton);

        RelativeLayout playerLayout = (RelativeLayout) findViewById(R.id.PlayerLayout);

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
                if (SelectedSongs){
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
                if (SelectedSongs) {
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

                            recycler.smoothScrollToPosition(y-1);
                            y--;
                            CurrentExercise.setText(String.valueOf(y+1));
                            main_timer.cancel();
                            Rep_timer_text.setText(String.valueOf(Exercises_reps[y]));
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
                            // String state = PauseButton.getText().toString();
                            recycler.smoothScrollToPosition(y+1);
                            y++;
                            CurrentExercise.setText(String.valueOf(y+1));
                            main_timer.cancel();
                            Rep_timer_text.setText(String.valueOf(Exercises_reps[y]));
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

        if (mySongs != null && mySongs.size() > 0){
            SelectedSongs = true;
            for(int j = 0; j < mySongs.size(); j++){
                for(int z = 0; z < position.length; z++)
                    if (Objects.equals(position[z], SongName(this,mySongs.get(j)))){
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
        }
        else{

            song_name.setText(getResources().getString(R.string.no_song));
            btPlay.setEnabled(false);
            btPlay.setClickable(false);
            btPause.setEnabled(false);
            btPause.setClickable(false);
            Log.v(TAG, (String) song_name.getText());
        }


        actual_start_time = 5;
        startInicialTimer(actual_start_time);
    }


    private void startInicialTimer(int seconds){

        long  sec = seconds * 1000;

        startTimer = new CountDownTimer(sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Format_Time_Rest =  Math.round(millisUntilFinished * 0.001f);

                actual_start_time--;
                RestCounter_text.setText(String.valueOf(actual_start_time));
            }

            public void onFinish() {
            }
        }.start();
        START_TIMER = true;
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
        Log.v(TAG,"Format_Time: "+Format_Time);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnPlay:
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
                mp.start();
                break;
            case R.id.btnPause:
                btPlay.setVisibility(View.VISIBLE);
                btPause.setVisibility(View.GONE);
                mp.pause();
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
                startInicialTimer(actual_start_time+5);
            }

        }


    }
    private void asignTotalTime(int position){
        totalTime = (Exercises_reps[position] * tempo) + 5;

        Log.v(TAG,"totalTime: "+totalTime);
        Log.v(TAG,"Exercises_reps[position]: "+Exercises_reps[position]);
        Log.v(TAG,"tempo: "+tempo);
    }



    private void playExerciseAudio(String file){
        Log.v(TAG,"playExerciseAudio: "+file);
        media = new MediaPlayer();
        int maxVolume = 100;
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
                media.start();
            }} );

        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mp!=null && mp.isPlaying()) {
                    mp.setVolume(volumeFull, volumeFull);
                    mediaPlayer.release();
                }
            }
        });
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

                //Log.v(TAG,"millisUntilFinished:"+millisUntilFinished);
                Format_Time_Rest =  Math.round(millisUntilFinished * 0.001f);
                //Log.v(TAG,"Format_Time_Rest:"+Format_Time_Rest);

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


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");

        if (pause){
            ResumeCountDown();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
        PauseCountDown();
    }

    @Override
    public void onBackPressed(){
        Log.v(TAG,"BUTTON BACK PRESSED: CLICK");
        DialogFinalize();
    }

    @Override
    protected void onDestroy() {
        ScreenOff();
        if (MAIN_TIMER && main_timer != null){
            main_timer.cancel();
        }

        if (media!=null){
            media.release();
        }

        super.onDestroy();
    }



}