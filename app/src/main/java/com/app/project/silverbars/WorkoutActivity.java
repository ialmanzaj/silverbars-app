package com.app.project.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkoutActivity extends AppCompatActivity {

    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private Button plusSets;
    private Button minusSets;
    private Button plusRest;
    private Button minusRest;
    private Button plusRestSets;
    private Button minusRestSets;
    private Button SelectMusic;
    private TextView Positive, Negative, Isometric, Reps, Workout_name, Sets, Rest, RestSets;
    private String[] position;
    private List<String> spinnerArray = new ArrayList<String>();
    private int value = 0;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private int ExerciseReps = 1;
    private ArrayList<File> mySongs;
    static public int[] Exercises_reps;
    private String[] exercises;
    private String workout_name, level;
    private int workout_id = 0, workout_sets = 0;
    private List<WorkoutInfo> items = new ArrayList<>();
    public static JsonExercise[] ParsedExercises;
    public static JsonReps[] ParsedReps;
    private int[] exercises_id;


    private static boolean VibrationIsActivePerRep=true;
    private static boolean VibrationIsActivePerSet=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        exercises = intent.getStringArrayExtra("exercises");
        Log.v("Exercises",Arrays.toString(exercises));
        workout_name = intent.getStringExtra("name");
        level = intent.getStringExtra("level");
        workout_id = intent.getIntExtra("id",0);
        workout_sets = intent.getIntExtra("sets",0);
        exercises_id = new int[exercises.length];
        ParsedExercises = new JsonExercise[exercises.length];
        Exercises();
        setContentView(R.layout.activity_workout);
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        if (recycler != null) {
            recycler.setHasFixedSize(true);
        }
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        Log.v("Item size test", String.valueOf(items.size()));

        // ======= TOOL BAR - BACK BUTTON  ADDED
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(workout_name);
        }
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchWorkingOutActivity();
            }
        });

//        Tab 2

        Reps = (TextView) findViewById(R.id.Reps);

        Sets = (TextView) findViewById(R.id.Sets);
        Sets.setText(String.valueOf(workout_sets));

        Positive = (TextView) findViewById(R.id.Positive);
        Isometric = (TextView) findViewById(R.id.Isometric);
        Negative = (TextView) findViewById(R.id.Negative);
        Rest = (TextView) findViewById(R.id.Rest);
        RestSets = (TextView) findViewById(R.id.RestSets);

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
        plusSets = (Button) findViewById(R.id.plusSets);
        plusSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Sets,plusSets,minusSets);
            }
        });
        minusSets = (Button) findViewById(R.id.minusSets);
        minusSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Sets,minusSets,plusSets);
            }
        });
        plusRest = (Button) findViewById(R.id.plusRest);
        plusRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(Rest,plusRest,minusRest);
            }
        });
        minusRest = (Button) findViewById(R.id.minusRest);
        minusRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(Rest,minusRest,plusRest);
            }
        });
        plusRestSets = (Button) findViewById(R.id.plusRestSets);
        plusRestSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusTempo(RestSets,plusRestSets,minusRestSets);
            }
        });
        minusRestSets = (Button) findViewById(R.id.minusRestSets);
        minusRestSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusTempo(RestSets,minusRestSets,plusRestSets);
            }
        });
        int setsValue = Integer.parseInt(Sets.getText().toString());
        if (setsValue <= 1){
            plusSets.setEnabled(true);
            plusSets.setClickable(true);
            minusSets.setEnabled(false);
            minusSets.setClickable(false);
        }else if(setsValue >=10){
            plusSets.setEnabled(false);
            plusSets.setClickable(false);
            minusSets.setEnabled(true);
            minusSets.setClickable(true);
        }
        minusIsometric.setEnabled(false);
        minusIsometric.setClickable(false);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinnerArray.add("NORMAL");
        spinnerArray.add("EASY");
        spinnerArray.add("HARD");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item,spinnerArray
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        if (!level.equals(null)) {
            int spinnerPosition = arrayAdapter.getPosition(level);
            spinner.setSelection(spinnerPosition);
            switch (spinnerPosition){
                case 0:
                    Positive.setText("2");
                    Isometric.setText("1");
                    Negative.setText("2");
                    break;
                case 1:
                    Positive.setText("3");
                    Isometric.setText("1");
                    Negative.setText("3");
                    break;
                case 2:
                    Positive.setText("1");
                    Isometric.setText("3");
                    Negative.setText("1");
                    break;


            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        plusPositive.setEnabled(true);
                        plusPositive.setClickable(true);
                        plusIsometric.setEnabled(true);
                        plusIsometric.setClickable(true);
                        plusNegative.setEnabled(true);
                        plusNegative.setClickable(true);
                        minusPositive.setEnabled(true);
                        minusPositive.setClickable(true);
                        minusIsometric.setEnabled(false);
                        minusIsometric.setClickable(false);
                        minusNegative.setEnabled(true);
                        minusNegative.setClickable(true);
                        Positive.setText("2");
                        Isometric.setText("1");
                        Negative.setText("2");

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
                        minusIsometric.setEnabled(false);
                        minusIsometric.setClickable(false);
                        minusNegative.setEnabled(true);
                        minusNegative.setClickable(true);
                        Positive.setText("3");
                        Isometric.setText("1");
                        Negative.setText("3");
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
                        minusIsometric.setEnabled(true);
                        minusIsometric.setClickable(true);
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

//        items.add(new WorkoutInfo(R.mipmap.imagen1, "Upper Body", "core", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen2, "Core", "Arms and Back", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen3, "Arms and Back", "Legs", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen4, "Legs", "Full Body", String.valueOf(ExerciseReps)));
//        items.add(new WorkoutInfo(R.mipmap.imagen5, "Full body", "End Workout", String.valueOf(ExerciseReps)));


        //Defining Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost2);
        tabHost2.setup();



        TabHost.TabSpec overview = tabHost2.newTabSpec("Overview");
        TabHost.TabSpec setup = tabHost2.newTabSpec("Setup");
        TabHost.TabSpec muscles = tabHost2.newTabSpec("Muscles");



        setup.setIndicator("Setup");
        setup.setContent(R.id.setup);

        overview.setIndicator("Overview");
        overview.setContent(R.id.overview);

        muscles.setIndicator("Muscles");
        muscles.setContent(R.id.muscles);

        tabHost2.addTab(overview);
        tabHost2.addTab(setup);
        tabHost2.addTab(muscles);

        SelectMusic = (Button) findViewById(R.id.SelectMusic);
        SelectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMusicDialog();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
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
        int positive,isometric,negative,sets;
            if (Positive.getText().toString() != "" && Isometric.getText().toString() != "" && Negative.getText().toString() != "" && Sets.getText().toString() != ""){
                positive = Integer.parseInt(Positive.getText().toString());
                isometric = Integer.parseInt(Isometric.getText().toString());
                negative = Integer.parseInt(Negative.getText().toString());
                sets = Integer.parseInt(Sets.getText().toString());
                int tempoTotal = positive + isometric + negative;
                Intent intent = new Intent(this, WorkingOutActivity.class);
                intent.putExtra("ExercisesReps",Exercises_reps);
                intent.putExtra("tempo", tempoTotal);
                intent.putExtra("pos",position);
                intent.putExtra("songlist",mySongs);
                intent.putExtra("Sets",sets);
                intent.putExtra("VibrationPerSet",VibrationIsActivePerSet);
                intent.putExtra("VibrationPerRep",VibrationIsActivePerRep);
                startActivity(intent);
            }
            else{
                toast("Must select a tempo");
            }
    }

    public void LaunchMusicActivity() {
        Intent intent = new Intent(this, PlaylistPickerActivity.class);
        startActivityForResult(intent,1);
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void plusTempo(TextView view, Button button, Button button2){
        if (view == Reps){
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value+1));
            value++;
            if (value == 20){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == Rest){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 5;
            view.setText(String.valueOf(value+"s"));
            if (value == 60){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 0){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == RestSets){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 10;
            view.setText(String.valueOf(value+"s"));
            if (value == 180){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 0){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value+1));
            value++;
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
        if (view == Reps){
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value-1));
            value--;
            if ((value)==1){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 20){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == Rest){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 5;
            view.setText(String.valueOf(value+"s"));
            if (value == 0){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 60){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else if(view == RestSets){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 10;
            view.setText(String.valueOf(value+"s"));
            if (value == 0){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value < 180){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }
        }else{
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value-1));
            value--;
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

    public void Exercises(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                // Customize the request
                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();
                okhttp3.Response response = chain.proceed(request);
                Log.v("Response",response.toString());
                // Customize or return the response
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.silverbarsapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        SilverbarsService service = retrofit.create(SilverbarsService.class);
        for (int i = 0; i < exercises.length; i++){
            final int a = i;
            String [] parts = exercises[i].split("exercises");
            exercises[i] = "/exercises"+parts[1];
            Call<JsonExercise> call = service.getExercises(exercises[i]);
            call.enqueue(new Callback<JsonExercise>() {
                @Override
                public void onResponse(Call<JsonExercise> call, Response<JsonExercise> response) {
                    if (response.isSuccessful()) {
                        ParsedExercises[a] = response.body();
//                        Log.v("Response",ParsedExercises[a].getExercise_name()+" / "+ExerciseReps);
                        items.add(new WorkoutInfo(ParsedExercises[a].exercise_name, String.valueOf(ExerciseReps)));
//                        Log.v("Items size",String.valueOf(items.size()));
                        exercises_id[a] = ParsedExercises[a].getId();
                        if ( items.size() == exercises.length){
                            exercisesReps();
                        }
//                    Workouts = response.body();
                    } else {
                        int statusCode = response.code();
                        // handle request errors yourself
                        ResponseBody errorBody = response.errorBody();
                        Log.v("Error",errorBody.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonExercise> call, Throwable t) {
                    Log.v("Exception",t.toString());
                }
            });
        }
//
    }
//    JsonReps[] RepsData = JsonData.getReps("http://api.silverbarsapp.com/workout/?format=json",workout_id,exercises.length);
//    ParsedReps = RepsData;
    public void exercisesReps(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();

                okhttp3.Response response = chain.proceed(request);
                Log.v("Response",response.toString());
                // Customize or return the response
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.silverbarsapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        SilverbarsService service = retrofit.create(SilverbarsService.class);
        Call<JsonReps[]> call = service.getReps();
        call.enqueue(new Callback<JsonReps[]>() {
            @Override
            public void onResponse(Call<JsonReps[]> call, Response<JsonReps[]> response) {
                if (response.isSuccessful()) {
                    JsonReps[] Reps = response.body();
                    ParsedReps = new JsonReps[items.size()];
                    int y = 0;
                    for (int z = 0; z < Reps.length; z++){
                        String workout = Reps[z].getWorkout_id();
                        if (workout.indexOf("workouts/"+workout_id)>0){
                            ParsedReps[y] = Reps[z];
                            y++;
                        }
                    }
                    Exercises_reps = new int[items.size()];
                    for (int i = 0; i <items.size() ; i++){
                        String[] audioDir = ParsedExercises[i].getExercise_audio().split("exercises");;
                        String Parsedurl = "exercises"+audioDir[1];
                        String[] splitName = Parsedurl.split("/");
                        String mp3Name = splitName[2];
                        File Dir = new File(Environment.getExternalStorageDirectory()+"/SilverbarsMp3");
                        if (Dir.isDirectory()){
                            File file = new File(Environment.getExternalStorageDirectory()+"/SilverbarsMp3/"+mp3Name);
                            if (!file.exists()){
                                DownloadMp3(Parsedurl,mp3Name);
                            }
                        }else{
                            boolean success = Dir.mkdir();
                            if (success)
                                DownloadMp3(Parsedurl,mp3Name);
                            else
                                Log.v("Dir","Error creating dir");
                        }

                        Exercises_reps[i] = ParsedReps[i].getRepetition();
                    }
                    adapter = new ExerciseAdapter(items,WorkoutActivity.this);
                    recycler.setAdapter(adapter);
                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.v("Error",errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonReps[]> call, Throwable t) {
                Log.v("Exception",t.toString());
            }
        });
    }

    public void DownloadMp3(final String url, final String audioName) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        final SilverbarsService downloadService = retrofit.create(SilverbarsService.class);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<ResponseBody> call = downloadService.downloadFile(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {boolean writtenToDisk = writeResponseBodyToDisk(response.body(),audioName);}
                        else {Log.d("Download", "server contact failed");}
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {Log.v("Failure", t.toString());}
                });
                return null;
            };
        }.execute();
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String audioName) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory()+"/SilverbarsMp3/"+audioName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {break;}
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;

            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {inputStream.close();}
                if (outputStream != null) {outputStream.close();}
            }
        } catch (IOException e) {return false;}
    }
}
