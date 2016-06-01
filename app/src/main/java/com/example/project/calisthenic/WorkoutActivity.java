package com.example.project.calisthenic;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ayz4sci.androidfactory.DownloadProgressView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkoutActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;


    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private Button plusReps;
    private Button minusReps;
    private Button SelectMusic;
    private TextView Positive, Negative, Isometric;
    private ArrayList<File> play_list;
    private long[] position;
    private List<String> spinnerArray = new ArrayList<String>();
    private int value = 0;
    private TextView Reps;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private int ExerciseReps = 1;
    private LikeButton likeButton;
    private ListView ListMusic;
    private String[] items, songs;
    private Button clean,done;
    private long[] selected;
    private ArrayList<File> mySongs;
    private ArrayAdapter<String> adp;
    private DownloadProgressView downloadProgressView;
    private Button download;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_workout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ======= TOOL BAR - BACK BUTTON  ADDED
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Workouts");
        }

        FloatingActionButton startButton = (FloatingActionButton) findViewById(R.id.fab);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchWorkingOutActivity();
            }
        });

//        Tab 1
        likeButton = (LikeButton) findViewById(R.id.star_button);
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });

//        downloadProgressView = (DownloadProgressView) findViewById(R.id.downloadProgressView);
        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new DownloadFileFromURL().execute("https://www.dropbox.com/home/Proyecto%20Workout/audio-examples?preview=closedpullups.mp3");
                try {
                    mp3load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

//        Tab 2

        Reps = (TextView) findViewById(R.id.Reps);

        Positive = (TextView) findViewById(R.id.Positive);
        Isometric = (TextView) findViewById(R.id.Isometric);
        Negative = (TextView) findViewById(R.id.Negative);

        plusPositive = (Button) findViewById(R.id.plusPositive);
        plusPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Positive,plusPositive,minusPositive);
            }
        });
        minusPositive = (Button) findViewById(R.id.minusPositive);
        minusPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Positive,minusPositive,plusPositive);
            }
        });
        plusIsometric = (Button) findViewById(R.id.plusIsometric);
        plusIsometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Isometric,plusIsometric,minusIsometric);
            }
        });
        minusIsometric = (Button) findViewById(R.id.minusIsometric);
        minusIsometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Isometric,minusIsometric,plusIsometric);
            }
        });
        plusNegative = (Button) findViewById(R.id.plusNegative);
        plusNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Negative,plusNegative,minusNegative);
            }
        });
        minusNegative = (Button) findViewById(R.id.minusNegative);
        minusNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Negative,minusNegative,plusNegative);
            }
        });
        plusReps = (Button) findViewById(R.id.plusReps);
        plusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Reps,plusReps,minusReps);
                ExerciseReps = Integer.valueOf(Reps.getText().toString());
            }
        });
        minusReps = (Button) findViewById(R.id.minusReps);
        minusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Reps,minusReps,plusReps);
                ExerciseReps = Integer.valueOf(Reps.getText().toString());
            }
        });
        minusReps.setEnabled(false);
        minusReps.setClickable(false);
        minusIsometric.setEnabled(false);
        minusIsometric.setClickable(false);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinnerArray.add("Easy");
        spinnerArray.add("Normal");
        spinnerArray.add("Hard");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,spinnerArray
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        plusPositive.setEnabled(false);
                        plusPositive.setClickable(false);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(false);
                        plusNegative.setClickable(false);
                        minusPositive.setEnabled(true);
                        minusPositive.setClickable(true);
                        minusIsometric.setEnabled(true);
                        minusIsometric.setClickable(true);
                        minusNegative.setEnabled(true);
                        minusNegative.setClickable(true);
                        Positive.setText("3");
                        Isometric.setText("1");
                        Negative.setText("3");
                        break;
                    case 1:
                        plusPositive.setEnabled(true);
                        plusPositive.setClickable(true);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(true);
                        plusNegative.setClickable(true);
                        minusPositive.setEnabled(true);
                        minusPositive.setClickable(true);
                        minusIsometric.setEnabled(true);
                        minusIsometric.setClickable(true);
                        minusNegative.setEnabled(true);
                        minusNegative.setClickable(true);
                        Positive.setText("2");
                        Isometric.setText("1");
                        Negative.setText("2");
                        break;
                    case 2:
                        plusPositive.setEnabled(true);
                        plusPositive.setClickable(true);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(true);
                        plusNegative.setClickable(true);
                        minusPositive.setEnabled(false);
                        minusPositive.setClickable(false);
                        minusIsometric.setEnabled(false);
                        minusIsometric.setClickable(false);
                        minusNegative.setEnabled(false);
                        minusNegative.setClickable(false);
                        Positive.setText("1");
                        Isometric.setText("3");
                        Negative.setText("1");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Tab 3 Inicializar Exercises en el RecyclerView
        List<WorkoutInfo> items = new ArrayList<>();

        items.add(new WorkoutInfo(R.mipmap.imagen1, "Upper Body", "core", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen2, "Core", "Arms and Back", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen3, "Arms and Back", "Legs", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen4, "Legs", "Full Body", String.valueOf(ExerciseReps)));
        items.add(new WorkoutInfo(R.mipmap.imagen5, "Full body", "End Workout", String.valueOf(ExerciseReps)));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        if (recycler != null) {
            recycler.setHasFixedSize(true);
        }

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ExerciseAdapter(items);
        recycler.setAdapter(adapter);



        //Defining Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost2);
        tabHost2.setup();

        TabHost.TabSpec data1 = tabHost2.newTabSpec("Overview");
        TabHost.TabSpec data2 = tabHost2.newTabSpec("Setup");
        TabHost.TabSpec data3 = tabHost2.newTabSpec("Exercises");
        TabHost.TabSpec data4 = tabHost2.newTabSpec("Muscles");

        data1.setIndicator("Overview");
        data1.setContent(R.id.tab1);

        data2.setIndicator("Setup");
        data2.setContent(R.id.tab2);

        data3.setIndicator("Exercises");
        data3.setContent(R.id.tab3);

        data4.setIndicator("Muscles");
        data4.setContent(R.id.tab4);

        tabHost2.addTab(data1);
        tabHost2.addTab(data2);
        tabHost2.addTab(data3);
        tabHost2.addTab(data4);

        SelectMusic = (Button) findViewById(R.id.SelectMusic);
        SelectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMusicDialog();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getLongArrayExtra("positions");
            toast("Activity result");
        }
        else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            mySongs = null;
            position = null;
            toast("No result");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    public void ShowMusicDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(WorkoutActivity.this)
                .theme(Theme.LIGHT)
                .title("Choose source of your music")
                .adapter(new MusicSourceAdapter(WorkoutActivity.this), new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which){
                            case 0:
                                LaunchMusicActivity();
                                break;
                            case 1:
                                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                    }
                })
                .negativeText(android.R.string.cancel)
                .build();
        dialog.show();
    }

    private void LaunchWorkingOutActivity() {
        int positive,isometric,negative, totalReps;
        totalReps = Integer.parseInt(Reps.getText().toString());
        if (totalReps > 0){
            if (Positive.getText().toString() != "" && Isometric.getText().toString() != "" && Negative.getText().toString() != ""){
                positive = Integer.parseInt(Positive.getText().toString());
                isometric = Integer.parseInt(Isometric.getText().toString());
                negative = Integer.parseInt(Negative.getText().toString());
                int tempoTotal = positive + isometric + negative;
                Intent intent = new Intent(this, WorkingOutActivity.class);
                intent.putExtra("reps",totalReps);
                intent.putExtra("tempo", tempoTotal);
                intent.putExtra("pos",position);
                intent.putExtra("songlist",mySongs);
                startActivity(intent);
            }
            else{
                toast("Must select a tempo");
            }
        }
        else
            toast("Dont be lazzy, have to do more than 1 Rep");
    }

    public void LaunchMusicActivity() {
        Intent intent = new Intent(this, PlaylistPickerActivity.class);
        startActivityForResult(intent,1);
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void plusTempo(TextView view, Button button, Button button2){
        value = Integer.parseInt(view.getText().toString());
        view.setText(String.valueOf(value+1));
        value++;
        if (view == Reps){
            if (value == 20){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
            if (value == 10){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }

    }

    public void minusTempo(TextView view, Button button, Button button2){
        value = Integer.parseInt(view.getText().toString());
        view.setText(String.valueOf(value-1));
        value--;
        if (view == Reps){
            if ((value)==1){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 20){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
            if ((value)==1){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 10){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }
    }

    public void mp3load() throws IOException {

//        URL url = new URL("http://farm1.static.flickr.com/114/298125983_0e4bf66782_b.jpg");
//        HttpURLConnection c = (HttpURLConnection) url.openConnection();
//        c.setRequestMethod("GET");
//        c.setDoOutput(true);
//        c.connect();
//
//        String PATH = Environment.getExternalStorageDirectory().getPath();
//        Log.v("LogPath", "PATH: " + PATH);
//        File file = new File(PATH);
//        file.mkdirs();
//
//        String fileName = "test.mp3";
//
//
//        File outputFile = new File(file, fileName);
//        FileOutputStream fos = new FileOutputStream(outputFile);
//
//        InputStream is = c.getInputStream();
//
//        byte[] buffer = new byte[1024];
//        int len1 = 0;
//        while ((len1 = is.read(buffer)) != -1) {
//            fos.write(buffer, 0, len1);
//        }
//        fos.close();
//        is.close();
//            PlayAudio();
    }

}
