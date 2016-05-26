package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vignesh_iopex.confirmdialog.Confirm;
import com.github.vignesh_iopex.confirmdialog.Dialog;
//import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkingOutActivity extends AppCompatActivity implements View.OnClickListener {

     static MediaPlayer mp;
    ArrayList<File> mySongs, playlist;
//    Thread updateSeekBar;
//    SeekBar sb;
    private ImageButton btPlay, btPause, nxtExercise, prvExercise;
    Uri u;
    long[] position;
    private int x = 0, y=0, elements = 0, time=0, tempo = 0, count = 0, totalReps, actualReps;
    private int totalTime;
    private TextView timer, song_name, CurrentSet, TotalSet, CurrentExercise, TotalExercise, TimeView;
    private CountDownTimer timer2;
    private FrameLayout prvLayout, nxtLayout, PlayerLayout;
    private Button PauseButton;
    private boolean exit = false, SelectedSongs = false, finish = false;

//    ContinuableCircleCountDownView mCountDownView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    AlertDialog alertDialog;
//    public PowerManager powerManager ;
//    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);

//        mCountDownView = (ContinuableCircleCountDownView) findViewById(R.id.CountDownView);
//        powerManager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
//        wakeLock.acquire();


        prvExercise = (ImageButton) findViewById(R.id.prvExercise);
        nxtExercise = (ImageButton) findViewById(R.id.nxtExercise);

        CurrentSet = (TextView) findViewById(R.id.CurrentSet);
        TotalSet = (TextView) findViewById(R.id.TotalSet);
        CurrentExercise = (TextView) findViewById(R.id.CurrentExercise);
        TotalExercise = (TextView) findViewById(R.id.TotalExercise);

        prvLayout = (FrameLayout) findViewById(R.id.prvLayout);
        nxtLayout = (FrameLayout) findViewById(R.id.nxtLayout);

        PlayerLayout = (FrameLayout) findViewById(R.id.PlayerLayout);

        PauseButton = (Button) findViewById(R.id.PauseButton);

        TimeView = (TextView) findViewById(R.id.time);

        actualReps = totalReps;

        timer = (TextView) findViewById(R.id.timer);
        timer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                toast("Cambio"+charSequence);
                actualReps = Integer.valueOf(charSequence.toString());

                if (actualReps == 0){
                    if (y+1 < elements){
                        y++;
                        prvLayout.setVisibility(View.VISIBLE);
                        if((y+1)==elements){
                            nxtLayout.setVisibility(View.GONE);
                        }
                        recycler.smoothScrollToPosition(y);
                        CurrentExercise.setText(String.valueOf(y+1));

                        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(1000);
                        timer.setText(String.valueOf(totalReps+1));
                        timer2.cancel();
                        Timer(totalTime,1);
                    }
                    else{
//                    timer.setText("Well Done!");
                        toast("done");
                        new CountDownTimer(4000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                alertDialog.setMessage(""+ (millisUntilFinished/1000));
                            }

                            @Override
                            public void onFinish() {
//                info.setVisibility(View.GONE);
                                timer.setText(String.valueOf(totalReps));
                                Timer(totalTime,1);
                                if (SelectedSongs == true){
                                    mp.start();
                                }
                                alertDialog.dismiss();
                            }
                        }.start();
                        timer2.cancel();
                        finish = true;

                        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(500);
//                    wakeLock.release();
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        PlayerLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                if (SelectedSongs == true){
                    int playlist_size = playlist.size();
                    if (playlist_size > 1 && (x-1) >= 0 ){
                        //                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        mp.release();
                        x = (x-1)%playlist.size();
                        u = Uri.parse(playlist.get(x).toString());
                        String Songname = playlist.get(x).getName().toString().replace(".mp3","");
                        song_name.setText(Songname);
                        mp = MediaPlayer.create(getApplicationContext(),u);
                        mp.start();
                    }
                    else{
                        //                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
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
                if (SelectedSongs == true) {
                    //                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                    int playlist_size = playlist.size();
                    if (playlist_size > 1 && x + 1 < playlist_size) {
                        //                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        mp.stop();
                        mp.release();
                        x = (x + 1) % playlist.size();
                        u = Uri.parse(playlist.get(x).toString());
                        song_name.setText(SongName(playlist.get(x)));
                        mp = MediaPlayer.create(getApplicationContext(), u);
                        mp.start();
                    } else {
                        //                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
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

        items.add(new WorkoutInfo(R.mipmap.imagen1, "Upper Body", "core",null));
        items.add(new WorkoutInfo(R.mipmap.imagen2, "Core", "Arms and Back",null));
        items.add(new WorkoutInfo(R.mipmap.imagen3, "Arms and Back", "Legs",null));
        items.add(new WorkoutInfo(R.mipmap.imagen4, "Legs", "Full Body",null));
        items.add(new WorkoutInfo(R.mipmap.imagen5, "Full body", "End Workout",null));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        //if (recycler != null) {
            //recycler.setHasFixedSize(true);
        //}

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new WorkoutsAdapter(items);
        recycler.setAdapter(adapter);

        elements = adapter.getItemCount();
        CurrentExercise.setText("1");
        TotalExercise.setText(String.valueOf(elements));
//        toast(String.valueOf(elements))
        recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lManager.getPosition(view);
//                toast(String.valueOf(lManager.getPosition(view)));
            }
        });
        prvExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = PauseButton.getText().toString();
                recycler.smoothScrollToPosition(y-1);
                y--;
                CurrentExercise.setText(String.valueOf(y+1));
                timer2.cancel();
                timer.setText(String.valueOf(totalReps));
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
                        if (SelectedSongs){
                            mp.pause();
                        }
                        break;
                    case "RESUME":
                        onTimerPause();
                        if (SelectedSongs) {
                            mp.start();
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
                timer.setText(String.valueOf(totalReps));
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
                        if (SelectedSongs){
                            mp.pause();
                        }
                        break;
                    case "RESUME":
//                        PauseButton.setText("PAUSE");
                        onTimerPause();
                        if (SelectedSongs) {
                            mp.start();
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

        Intent i = getIntent();
        Bundle b = i.getExtras();
        totalReps = b.getInt("reps");
        tempo = b.getInt("tempo");
        totalTime = totalReps * tempo + 2;
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getLongArray("pos");
        playlist = new ArrayList<>();

//        for (int z = 0; z < position.length; z++){
//            toast(String.valueOf(position[z]));
//        }
        if (mySongs != null && mySongs.size() > 0){
            SelectedSongs = true;
            for(int j = 0; j < mySongs.size(); j++){
                if (j == position[j]){
                    playlist.add(mySongs.get(j));
                }
            }
            u = Uri.parse(playlist.get(x).toString());
            song_name.setText(SongName(playlist.get(x)));
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
                timer.setText(String.valueOf(totalReps));
                Timer(totalTime,1);
                if (SelectedSongs == true){
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
        Confirm.using(this).ask("Are you sure you want to exit?").onPositive("Yes", new Dialog.OnClickListener() {
            @Override public void onClick(Dialog dialog, int which) {
//                mp.stop();
                if (SelectedSongs){
                    mp.release();
                }
//                wakeLock.release();
                dialog.dismissDialog();
                finish();
            }}).onNegative("No",  new Dialog.OnClickListener() {
            @Override public void onClick(Dialog dialog, int which) {
                exit = false;
                if (SelectedSongs){
                    mp.start();
                }
                onTimerResume();
            }}).build().show();
    }

    public void MusicPlayList(final int playlist_size){
        if (playlist_size>1){
//            mp.stop();
//            mp.reset();
            x = (x+1)%playlist.size();
            u = Uri.parse(playlist.get(x).toString());
            song_name.setText(SongName(playlist.get(x)));
            mp = MediaPlayer.create(getApplicationContext(),u);
            mp.start();
        }else{
//            mp.stop();
//            mp.reset();
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

        timer2 = new CountDownTimer(totalsecs, sec_interval) {
            public void onTick(long millisUntilFinished) {performTick(millisUntilFinished);}
            public void onFinish() {}
        }.start();
        performTick(totalsecs);
    }

    // CONTADOR DE REPETICIONES
    void performTick(long millisUntilFinished) {
        String Format_Time = String.valueOf(Math.round(millisUntilFinished * 0.001f));
        if (count == tempo){
            actualReps--;
            timer.setText(String.valueOf(actualReps));
            // VIBRADOR POR REPETICION
            Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(250);
            count = 0;
        }
        count++;
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
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

    private String SongName(File file){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(file);
        mediaMetadataRetriever.setDataSource(this, uri);
        String name = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        return name;
    }

    public void onTimerResume(){
        Timer(time,1);
    }

}
