package com.example.project.calisthenic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkingOut extends AppCompatActivity implements View.OnClickListener {

     static MediaPlayer mp;
    ArrayList<File> mySongs, playlist;
    Thread updateSeekBar;
    SeekBar sb;
    ImageButton btPlay, btPause;
    Uri u;
    long[] position;
    int x = 0, y=0, elements = 0;
    TextView timer;

    ContinuableCircleCountDownView mCountDownView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_out);

//        mCountDownView = (ContinuableCircleCountDownView) findViewById(R.id.CountDownView);

        // Inicializar Animes
        List<Workouts_info> items = new ArrayList<>();

        items.add(new Workouts_info(R.mipmap.imagen1, "Upper Body", "core"));
        items.add(new Workouts_info(R.mipmap.imagen2, "Core", "Arms and Back"));
        items.add(new Workouts_info(R.mipmap.imagen3, "Arms and Back", "Legs"));
        items.add(new Workouts_info(R.mipmap.imagen4, "Legs", "Full Body"));
        items.add(new Workouts_info(R.mipmap.imagen5, "Full body", "End Workout"));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new WorkoutsAdapter(items);
        recycler.setAdapter(adapter);

        elements = adapter.getItemCount();




//        mCountDownView.setTimer(30000);
//        mCountDownView.start();

        btPlay = (ImageButton)findViewById(R.id.btnPlay);
        btPause = (ImageButton)findViewById(R.id.btnPause);

        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);

        sb = (SeekBar)findViewById(R.id.seekBar);
        updateSeekBar = new Thread(){
            @Override
            public void run(){
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
//                sb.setMax(totalDuration);
                while (currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
//                super.run();
            }
        };

        if (mp!=null){
            mp.stop();
            mp.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getLongArray("pos");
        playlist = new ArrayList<>();

        for(int j = 0; j < mySongs.size(); j++){
            if (j == position[j]){
                playlist.add(mySongs.get(j));
//                x++;
            }
        }

        u = Uri.parse(playlist.get(x).toString());
        mp = MediaPlayer.create(getApplicationContext(),u);

        sb.setMax(mp.getDuration());
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
        updateSeekBar.start();
        int playlist_size = playlist.size();
        if (playlist_size>1){
//            Toast.makeText(getApplicationContext(),
//
//                    "mas de 1 cancion",
//
//                    Toast.LENGTH_LONG).show();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.stop();
                    mp.release();
                    x = (x+1)%playlist.size();
                    u = Uri.parse(playlist.get(x).toString());
                    mp = MediaPlayer.create(getApplicationContext(),u);
//                    updateSeekBar.start();
                    mp.start();
                    sb.setMax(mp.getDuration());

                }
            });
        }else{
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.stop();
                    mp.release();
//                    x = 0;
                    u = Uri.parse(playlist.get(x).toString());
                    mp = MediaPlayer.create(getApplicationContext(),u);
                    mp.start();
                    sb.setMax(mp.getDuration());
                }
            });
        }

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert 3");
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
                timer = (TextView) findViewById(R.id.timer);
                Timer(31,1);
                mp.start();
                alertDialog.dismiss();
            }
        }.start();


    }

    public void Timer(int seconds,int interval){
        int totalsecs= seconds * 1000;
        int sec_interval= interval * 1000 ;
        y++;
//        timer.setText(seconds+"");
        new CountDownTimer(totalsecs, sec_interval) {

            public void onTick(long millisUntilFinished) {

                timer.setText(millisUntilFinished / 1000+"");
            }

            public void onFinish() {

                if (y < elements){
                    recycler.smoothScrollToPosition(y);
                    Timer(31,1);
                }

            }
        }.start();
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
}
