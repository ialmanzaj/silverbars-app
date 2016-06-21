package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.like.LikeButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class WorkingOutActivity extends AppCompatActivity implements View.OnClickListener {

     static MediaPlayer mp;
    ArrayList<File> mySongs, playlist;
    private ImageButton btPlay;
    private ImageButton btPause;
    private Uri u;
    private String[] position;
    private int x = 0, y=0, elements = 0, time=0, tempo = 0, actualReps;
    private int totalTime;
    private TextView timer;
    private TextView song_name, artist_name;
    private TextView CurrentSet;
    private TextView TotalSet;
    private TextView CurrentExercise;
    private CountDownTimer timer2;
    private FrameLayout prvLayout;
    private FrameLayout nxtLayout;
    private Button PauseButton;
    private boolean SelectedSongs = false, finish = false;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private int TotalSets = 0, ActualSets = 0, Time_aux = 0;
    AlertDialog alertDialog;
    private MediaPlayer media;
    private JsonExercise[] Exercises;
    private int[] Exercises_reps;
    private boolean VibrationPerSet = false,VibrationPerRep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);


        Intent i = getIntent();
        Bundle b = i.getExtras();


        Exercises_reps = b.getIntArray("ExercisesReps");
        tempo = b.getInt("tempo");
        VibrationPerRep = b.getBoolean("VibrationPerRep");
        VibrationPerSet =  b.getBoolean("VibrationPerSet");
        TotalSets = b.getInt("Sets");
        Exercises = WorkoutActivity.ParsedExercises;
        RepsTime(y);
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getStringArray("pos");
        Log.v("Songs", Arrays.toString(position));
        playlist = new ArrayList<>();
        actualReps = Exercises_reps[0];
        artist_name = (TextView) findViewById(R.id.artist_name);

        CurrentSet = (TextView) findViewById(R.id.CurrentSet);
        TotalSet = (TextView) findViewById(R.id.TotalSet);
        CurrentExercise = (TextView) findViewById(R.id.CurrentExercise);
        final TextView totalExercise = (TextView) findViewById(R.id.TotalExercise);

        ImageButton prvExercise = (ImageButton) findViewById(R.id.prvExercise);
        ImageButton nxtExercise = (ImageButton) findViewById(R.id.nxtExercise);
        prvLayout = (FrameLayout) findViewById(R.id.prvLayout);
        nxtLayout = (FrameLayout) findViewById(R.id.nxtLayout);
        RelativeLayout playerLayout = (RelativeLayout) findViewById(R.id.PlayerLayout);
        PauseButton = (Button) findViewById(R.id.PauseButton);
        timer = (TextView) findViewById(R.id.timer);


        timer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                toast("Cambio"+charSequence);
                actualReps = Integer.valueOf(charSequence.toString());
                if (actualReps == 0){
                    if (y+1 < elements){
                        y++;
                        PlayAudio("frontlever");
                        prvLayout.setVisibility(View.VISIBLE);
                        if((y+1)==elements){
                            nxtLayout.setVisibility(View.GONE);
                        }
                        recycler.smoothScrollToPosition(y);
                        CurrentExercise.setText(String.valueOf(y+1));
                        ActivateVibrationPerSet();
                        timer.setText(String.valueOf(Exercises_reps[y]));
                        timer2.cancel();
                        RepsTime(y);
                        Timer(totalTime,1);
                    }
                    else{
                        if (ActualSets+1 <= 4){
                            ActualSets++;
                            CurrentSet.setText(String.valueOf(ActualSets));
                            CurrentExercise.setText(String.valueOf("1"));
                            y = 0;
                            recycler.smoothScrollToPosition(y);
                            finish = true;
                            timer.setText(String.valueOf(Exercises_reps[y]));
                            timer2.cancel();
                            RepsTime(y);
                            Timer(totalTime,1);
                            nxtLayout.setVisibility(View.VISIBLE);
                            prvLayout.setVisibility(View.GONE);
                        }
                        else{timer2.cancel();}
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

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
                        String SongName = SongName(playlist.get(x)).replace(".mp3","");
                        song_name.setText(SongName);
                        String SongArtist = SongArtist(playlist.get(x));
                        artist_name.setText(SongArtist);
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
                        String SongName = SongName(playlist.get(x)).replace(".mp3","");
                        song_name.setText(SongName);
                        String SongArtist = SongArtist(playlist.get(x));
                        artist_name.setText(SongArtist);
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
                String state = PauseButton.getText().toString();
                switch (state){
                    case "PAUSE":
                        PauseButton.setText("RESUME");
                        onTimerPause();
                        if (SelectedSongs){
                            mp.pause();
                        }
                        break;
                    case "RESUME":
                        PauseButton.setText("PAUSE");
                        onTimerResume();
                        if (SelectedSongs) {
                            mp.start();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        song_name = (TextView) findViewById(R.id.song_name);
        song_name.setSelected(true);

        // Inicializar Workouts
        List<WorkoutInfo> items = new ArrayList<>();

        for (int a = 0; a < Exercises.length; a++){
            items.add(new WorkoutInfo(Exercises[a].getExercise_name(),null));
        }
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);

        RecyclerView.OnItemTouchListener disable = new RecyclerViewTouch();

        recycler.addOnItemTouchListener(disable);        // disables scolling


        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);

        recycler.setLayoutManager(lManager);



        // Crear un nuevo adaptador
        adapter = new WorkoutsAdapter(items,getApplicationContext());
        recycler.setAdapter(adapter);

        if (adapter.getItemCount() == 1){
            prvLayout.setVisibility(View.GONE);
            nxtLayout.setVisibility(View.GONE);
        }


        elements = adapter.getItemCount();
        CurrentExercise.setText("1");
        totalExercise.setText(String.valueOf(elements));
        TotalSet.setText(String.valueOf(TotalSets));




        prvExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = PauseButton.getText().toString();
                recycler.smoothScrollToPosition(y-1);
                y--;
                CurrentExercise.setText(String.valueOf(y+1));
                timer2.cancel();
                timer.setText(String.valueOf(Exercises_reps[y]));
                RepsTime(y);
                Timer(totalTime,1);
                if (y == 0){
                    prvLayout.setVisibility(View.GONE);
                }
                else{
                    if(y < elements-1){
                        nxtLayout.setVisibility(View.VISIBLE);
                    }
                }
                switch (state){
                    case "PAUSE":
//                        if (SelectedSongs){
//                            mp.start();
//                        }
                        break;
                    case "RESUME":
                        onTimerPause();
                        if (SelectedSongs) {
                            mp.pause();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        nxtExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = PauseButton.getText().toString();
                recycler.smoothScrollToPosition(y+1);
                y++;
                CurrentExercise.setText(String.valueOf(y+1));
                timer2.cancel();
                timer.setText(String.valueOf(Exercises_reps[y]));
                RepsTime(y);
                Timer(totalTime,1);
                if (y == elements-1){
                    nxtLayout.setVisibility(View.GONE);
                }
                else{
                    if (y > 0){
                        prvLayout.setVisibility(View.VISIBLE);
                    }
                }
                switch (state){
                    case "PAUSE":
//                        PauseButton.setText("RESUME");
//                        onTimerResume();
//                        if (SelectedSongs){
//                            mp.start();
//                        }
                        break;
                    case "RESUME":
//                        PauseButton.setText("PAUSE");
                        onTimerPause();
                        if (SelectedSongs) {
                            mp.pause();
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        btPlay = (ImageButton)findViewById(R.id.btnPlay);
        btPause = (ImageButton)findViewById(R.id.btnPause);

        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);

        if (mySongs != null && mySongs.size() > 0){
            SelectedSongs = true;
            for(int j = 0; j < mySongs.size(); j++){
                for(int z = 0; z < position.length; z++)
                    if (Objects.equals(position[z], SongName(mySongs.get(j)))){
                        z++;
                        playlist.add(mySongs.get(j));
                    }
            }
            u = Uri.parse(playlist.get(x).toString());
            String SongName = SongName(playlist.get(x)).replace(".mp3","");
            song_name.setText(SongName);
            String SongArtist = SongArtist(playlist.get(x));
            artist_name.setText(SongArtist);
            Log.d("WorkingOutActivity", (String) song_name.getText());

            mp = MediaPlayer.create(getApplicationContext(),u);
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            final int playlist_size = playlist.size();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MusicPlayList(playlist_size);
                }
            });
        }
        else{
            song_name.setText("No Songs");
            btPlay.setEnabled(false);
            btPlay.setClickable(false);
            btPause.setEnabled(false);
            btPause.setClickable(false);
            Log.d("WorkingOutActivity", (String) song_name.getText());
        }

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Get Ready!");
        alertDialog.setMessage("4");
        alertDialog.show();   //

        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage(""+ (millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
//                info.setVisibility(View.GONE);
                timer.setText(String.valueOf(Exercises_reps[0]));
                TotalSet.setText(String.valueOf(TotalSets));
                CurrentSet.setText("0");
                Timer(totalTime,1);
                if (SelectedSongs){
                    mp.start();
                }
                alertDialog.dismiss();
            }
        }.start();
    }

    @Override
    public void onBackPressed(){
        if (SelectedSongs) {
            mp.pause();
        }
        onTimerPause();
        new MaterialDialog.Builder(this)
                .title("End Working Out?")
                .titleColor(getResources().getColor(R.color.colorPrimaryText))
                .contentColor(getResources().getColor(R.color.colorPrimaryText))
                .positiveColor(getResources().getColor(R.color.colorPrimaryText))
                .negativeColor(getResources().getColor(R.color.colorPrimaryText))
                .backgroundColor(Color.WHITE)
                .content("Are you sure you want to exit?")
                .positiveText("Yes").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                if (media!=null){
                    media.release();
                }
                if (SelectedSongs) {
                    mp.release();
                }
                finish();
            }
        })
                .negativeText("No").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                if (SelectedSongs) {
                    mp.start();
                }
                onTimerResume();
            }
        })
                .show();
    }

    public void MusicPlayList(final int playlist_size){
        if (playlist_size>1){
            x = (x+1)%playlist.size();
            u = Uri.parse(playlist.get(x).toString());
            String SongName = SongName(playlist.get(x)).replace(".mp3","");
            song_name.setText(SongName);
            String SongArtist = SongArtist(playlist.get(x));
            artist_name.setText(SongArtist);
            Log.d("WorkingOutActivity", (String) song_name.getText());
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
                MusicPlayList(playlist_size);
            }
        });
    }

    public void Timer(int seconds,int interval){
        int totalsecs= seconds * 1000;
        int sec_interval= interval * 1000 ;
        Time_aux = totalTime;

        timer2 = new CountDownTimer(totalsecs, sec_interval) {
            public void onTick(long millisUntilFinished) {performTick(millisUntilFinished);}
            public void onFinish() {}
        }.start();
    }

    // CONTADOR DE REPETICIONES
    void performTick(long millisUntilFinished) {

        int Format_Time = Math.round(millisUntilFinished * 0.001f);
        if (Time_aux-tempo == Format_Time){
            Time_aux = Time_aux - tempo;
            actualReps--;
            timer.setText(String.valueOf(actualReps));
            if (VibrationPerRep){
                ActivateVibrationPerRep();
            }
        }
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void ActivateVibrationPerRep(){
        if (VibrationPerRep) {
            Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(250);
        }
    }

    public void ActivateVibrationPerSet(){
        if (VibrationPerSet){
            Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(1000);
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

    public void onTimerPause(){
        String value = timer.getText().toString();
        if (!finish){
            time = Integer.valueOf(value);
            timer2.cancel();
        }
    }


    public void onTimerResume(){
        Timer(time,1);
    }

    public void PlayAudio (String file){
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
            AssetFileDescriptor descriptor = getAssets().openFd("audios/"+file+".mp3");
            media.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            media.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mp!=null && mp.isPlaying()){
            mp.setVolume(0.04f,0.04f);
        }
        media.start();
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

    @Override
    protected void onDestroy() {
        timer2.cancel();
        if (media!=null)
            media.release();
        super.onDestroy();
    }

    public void RepsTime(int position){
        totalTime = Exercises_reps[position] * tempo + 5;
    }
    private String SongName(File file){
        String title = null;
        try{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(this, uri);
            title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }catch(Exception e){
            Log.v("Exception",e.toString());
        }
        return title;
    }
    private String SongArtist(File file){
        String artist = null;
        try{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(file);
            mediaMetadataRetriever.setDataSource(this, uri);
            artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }catch(Exception e){
            Log.v("Exception",e.toString());
        }
        return artist;
    }
}
