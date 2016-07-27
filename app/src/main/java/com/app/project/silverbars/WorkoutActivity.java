package com.app.project.silverbars;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
/*import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private TextView Positive, Negative, Isometric, Reps, Workout_name, Sets, Rest, RestSets,RestSets_dialog,Sets_dialog;
    private String[] position;
    private List<String> spinnerArray = new ArrayList<String>();
    private int value = 0;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private int ExerciseReps = 1;
    private ArrayList<File> mySongs;
    static public int[] Exercises_reps;
    private LinearLayout primary_linear,secondary_linear;



    private String[] exercises;
    private int workout_id = 0;
    private List<WorkoutInfo> items = new ArrayList<>();
    public static JsonExercise[] ParsedExercises;
    public static JsonReps[] ParsedReps;
    private int[] exercises_id;


    private static final String TAG = WorkoutActivity.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "8a91678afa49446c9aff1beaabe9c807";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "testschema://callback";

    private static final int REQUEST_CODE = 1337;


    private static boolean VibrationIsActivePerRep=true;
    private static boolean VibrationIsActivePerSet=true;

    private String partes = "";
    private WebView webview;
    private TextView Rest_exercise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        exercises = intent.getStringArrayExtra("exercises");
        Log.v(TAG,Arrays.toString(exercises));

        String workout_name = intent.getStringExtra("name");
        String level = intent.getStringExtra("level");

        workout_id = intent.getIntExtra("id",0);
        int workout_sets = intent.getIntExtra("sets", 0);
        exercises_id = new int[exercises.length];
        ParsedExercises = new JsonExercise[exercises.length];
        Exercises();
        setContentView(R.layout.activity_workout);
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        if (recycler != null) {
                recycler.setHasFixedSize(true);

        }
        primary_linear = (LinearLayout) findViewById(R.id.primary_muscles);
        secondary_linear = (LinearLayout) findViewById(R.id.sec_muscles);



        //
        // Usar un administrador para LinearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


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


        Sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.set_edit)
                        .customView(R.layout.edit_set_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener

                            }
                        })
                        .show()
                        .getCustomView();
                if (v != null) {

                    Sets_dialog = (TextView) v.findViewById(R.id.Sets_dialog);
                    Sets_dialog.setText(String.valueOf(Sets.getText()));

                    plusSets = (Button) v.findViewById(R.id.plusSets);
                    plusSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            plusTempo(Sets_dialog,plusSets,minusSets);

                        }
                    });
                    minusSets = (Button) v.findViewById(R.id.minusSets);
                    minusSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            minusTempo(Sets_dialog,minusSets,plusSets);
                        }
                    });
                }

            }
        });

        Rest = (TextView) findViewById(R.id.Rest);
        Rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rest_exercise_text)
                        .customView(R.layout.edit_rest_exercise_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener

                            }
                        })
                        .show()
                        .getCustomView();
                if (v != null) {
                    Rest_exercise = (TextView) v.findViewById(R.id.Rest_exercise);
                    Rest_exercise.setText(String.valueOf(Rest.getText()));


                    plusRest = (Button) v.findViewById(R.id.plusRest);
                    plusRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plusTempo(Rest_exercise,plusRest,minusRest);
                        }
                    });
                    minusRest = (Button) v.findViewById(R.id.minusRest);
                    minusRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(Rest_exercise,minusRest,plusRest);
                        }
                    });
                }


            }
        });

        RestSets = (TextView) findViewById(R.id.RestSets);

        RestSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rest_sets_text)
                        .customView(R.layout.edit_rest_sets_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener

                            }
                        })
                        .show()
                        .getCustomView();

                if (v != null) {
                    RestSets_dialog = (TextView) v.findViewById(R.id.RestSets_dialog);
                    RestSets_dialog.setText(String.valueOf(RestSets.getText()));

                    plusRestSets = (Button) v.findViewById(R.id.plusRestSets);
                    plusRestSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plusTempo(RestSets_dialog,plusRestSets,minusRestSets);



                        }
                    });
                    minusRestSets = (Button) v.findViewById(R.id.minusRestSets);
                    minusRestSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(RestSets_dialog,minusRestSets,plusRestSets);

                        }
                    });
                }


            }
        });




/*
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
        });*/



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



        /*minusIsometric.setEnabled(false);
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
*/
        // Tab 3 Inicializar Exercises en el RecyclerView

        //Defining Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost2);
        
        tabHost2.setup();

        TabHost.TabSpec overview = tabHost2.newTabSpec(getResources().getString(R.string.tab_overview));

        TabHost.TabSpec muscles = tabHost2.newTabSpec(getResources().getString(R.string.tab_muscles));


        overview.setIndicator(getResources().getString(R.string.tab_overview));
        overview.setContent(R.id.overview);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles);

        tabHost2.addTab(overview);

        tabHost2.addTab(muscles);

        LinearLayout selectMusic = (LinearLayout) findViewById(R.id.SelectMusic);
        selectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), SelectionMusicActivity.class);
                startActivityForResult(i,1);
//                LaunchMusicActivity();
            }
        });


        // Web view     ========================
        webview = (WebView) findViewById(R.id.webview);



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mySongs = (ArrayList<File>) data.getSerializableExtra("songs");
            position = data.getStringArrayExtra("positions");
            Log.v("Position",Arrays.toString(position));
            Log.v("Songs",mySongs.toString());
            toast("Activity result");
        }
        else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            mySongs = null;
            position = null;
            toast("No result");
        }
        /*else if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    startMainActivity(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }*/
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void LaunchWorkingOutActivity() {
        int positive,isometric,negative,sets;
        /*
        positive = Integer.parseInt(Positive.getText().toString());
        isometric = Integer.parseInt(Isometric.getText().toString());
        negative = Integer.parseInt(Negative.getText().toString());
        */
        sets = Integer.parseInt(Sets.getText().toString());

        int tempoTotal = 5;

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

    public void LaunchMusicActivity() {
        Intent intent = new Intent(this, SelectionMusicActivity.class);
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
        }else if(view == Rest_exercise){
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
        }else if(view == RestSets_dialog){
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
        }else if(view == Rest_exercise){
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
        }else if(view == RestSets_dialog){
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

    private  List<String> muscles = new ArrayList<String>();

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
                Log.v(TAG,response.toString());
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

                        // muscles

                        //Collections.addAll(Muscles_names,name);
                        for (int b = 0; b < ParsedExercises[a].muscle.length; b++){
                            String name;
                            name = ParsedExercises[a].muscle[b];

                            muscles.add(name);
                        }



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
                        Log.e(TAG,errorBody.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonExercise> call, Throwable t) {
                    Log.e(TAG,t.toString(),t);
                }
            });
        }
    }

    private void setMusclesNames(List<String> muscle){
        List<String> muscles_names = muscles;
        //Log.v("MUSCLES NAMES", String.valueOf(Muscles_names) );
       /* Log.v(TAG," se ha asignado musculos");
        Log.v(TAG, String.valueOf(Muscles_names.size()));*/

        if ( muscles_names.size() > 0 ){

            for (String s : muscles_names)
            {
                final TextView MuscleView = new TextView(this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MuscleView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));
                }else {
                    MuscleView.setTextColor(getResources().getColor(R.color.gray_active_icon));
                }

                partes += "#"+ s + ",";
                MuscleView.setGravity(Gravity.CENTER);
                MuscleView.setText(s);

                //Log.v(TAG, (String) MuscleView.getText());
                primary_linear.addView(MuscleView);

            }
        }

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJS();
                super.onPageFinished(view, url);
            }

        });
        webview.getSettings().setJavaScriptEnabled(true);
        String fileurl = "file://"+Environment.getExternalStorageDirectory()+"/html/"+"index.html";

        webview.loadUrl(fileurl);

    }
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
    private void injectJS() {
        try {
            partes = removeLastChar(partes);
            Log.v(TAG,partes);

            webview.loadUrl("javascript: ("+ "window.onload = function () {"+

                    "partes = Snap.selectAll('"+partes+"');"+
                    "partes.forEach( function(elem,i) {"+
                    "elem.attr({fill: 'rgba(96%,44%,141%,50%)',stroke: 'rgba(96%,44%,141%,50%)',});"+
                    "});"+ "}"+  ")()");

            //Log.v("MAIN ACTIVITY","HA EJECUTADO EL JAVASCRIPT");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                Log.v(TAG,response.toString());
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
                        if (workout.indexOf("workouts/" + workout_id ) > 0){
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
                                Log.v(TAG,"Error creating dir");
                        }

                        Exercises_reps[i] = ParsedReps[i].getRepetition();
                    }
                    adapter = new ExerciseAdapter(items,WorkoutActivity.this,getSupportFragmentManager());
                    recycler.setAdapter(adapter);
                    setMusclesNames(muscles);
                    Log.v(TAG, String.valueOf(muscles));






                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.v(TAG,errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonReps[]> call, Throwable t) {
                Log.v(TAG,t.toString());
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
                        if (response.isSuccessful()) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),audioName);

                            Log.v(TAG, String.valueOf(writtenToDisk));
                        }
                        else {Log.e(TAG, "Download server contact failed");}
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {Log.v(TAG, t.toString(),t);}
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
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
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

   /* public void SpotifyLogin(){
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"playlist-read","user-library-read"})
                .build();

        AuthenticationClient.openLoginActivity(WorkoutActivity.this, REQUEST_CODE, request);
    }
*/
    private void startMainActivity(String token) {
//        Intent intent = new Intent(this, SpotifyMusic.class);
//        startActivityForResult(intent,1);
        Intent intent = SpotifyMusic.createIntent(this);
        intent.putExtra(SpotifyMusic.EXTRA_TOKEN, token);
        startActivity(intent);
        finish();
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
}
