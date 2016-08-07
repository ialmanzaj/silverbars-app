package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class CreateWorkoutFinalActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CreateWorkoutFinal";
    Toolbar toolbar;
    LinearLayout addExercise, reAdd;
    ImageView addImg, imgProfile;
    TextView addText, Sets, Rest, RestSets;
    EditText workoutName;
    Button plusSets, minusSets, plusRest, minusRest, plusRestSet, minusRestSet, Save;
    private ImageButton changeImg;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;

    private int value = 0;

    ArrayList<String> Exercises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout_final);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        Exercises = b.getStringArrayList("exercises");
        Log.v(TAG,"exercises"+Exercises);




        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Save Workout");
        }


        workoutName = (EditText) findViewById(R.id.workoutName);
        Save = (Button) findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String name = workoutName.getText().toString();
//                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
//                sqLiteHelper.insertWorkouts(name,);
            }
        });

        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.final_recycler);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);




        plusSets = (Button) findViewById(R.id.plusSets);
        plusSets.setOnClickListener(this);
        minusSets = (Button) findViewById(R.id.minusSets);
        minusSets.setOnClickListener(this);
        minusSets.setEnabled(false);
        plusRest = (Button) findViewById(R.id.plusRest);
        plusRest.setOnClickListener(this);
        minusRest = (Button) findViewById(R.id.minusRest);
        minusRest.setOnClickListener(this);
        plusRestSet = (Button) findViewById(R.id.plusRestSets);
        plusRestSet.setOnClickListener(this);
        minusRestSet = (Button) findViewById(R.id.minusRestSets);
        minusRestSet.setOnClickListener(this);
        Sets = (TextView) findViewById(R.id.Sets);
        Rest = (TextView) findViewById(R.id.Rest);
        RestSets = (TextView) findViewById(R.id.RestSets);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);



        changeImg = (ImageButton) findViewById(R.id.chageImg);
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }

        });

        //addImg = (ImageView) findViewById(R.id.addImg);
        //addText = (TextView) findViewById(R.id.addText);




    }





    public void plusTempo(TextView view, Button button, Button button2){
        if (view == Sets){
            value = Integer.parseInt(view.getText().toString());
            if (value+1 == 10)
                view.setText(String.valueOf(value+1));
            else
                view.setText("0"+String.valueOf(value+1));
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
        }else if(view == Rest){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 5;
            if (value + 5 == 5)
                view.setText("0"+String.valueOf(value+"s"));
            else
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
        if (view == Sets){
            value = Integer.parseInt(view.getText().toString());
            view.setText("0"+String.valueOf(value-1));
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
        }else if(view == Rest){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 5;
            if (value == 5)
                view.setText("0"+String.valueOf(value+"s"));
            else if(value == 0)
                view.setText("0"+String.valueOf(value+"s"));
            else
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
            if (value == 0)
                view.setText("0"+String.valueOf(value+"s"));
            else
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

    private void putExercisesinRecycler(){


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
        Call<JsonExercise[]> call = service.getAllExercises();
        call.enqueue(new Callback<JsonExercise[]>() {
            @Override
            public void onResponse(Call<JsonExercise[]> call, Response<JsonExercise[]> response) {

                if (response.isSuccessful()) {
                    JsonExercise[] parsedExercises = response.body();
                    List<JsonExercise> AllExercisesList = new ArrayList<>();

                    Collections.addAll(AllExercisesList, parsedExercises);




                    Context context = CreateWorkoutFinalActivity.this;

                    //adapter = new RecyclerExerciseSelectedAdapter(context,ExercisesToAdapter,CreateWorkoutActivity.this);



                    recycler.setAdapter(adapter);


                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG,errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonExercise[]> call, Throwable t) {
                Log.e(TAG,"onFailure: ",t);
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.plusSets:
                plusTempo(Sets,plusSets,minusSets);
                break;
            case R.id.minusSets:
                minusTempo(Sets,minusSets,plusSets);
                break;
            case R.id.plusRest:
                plusTempo(Rest,plusRest,minusRest);
                break;
            case R.id.minusRest:
                minusTempo(Rest,minusRest,plusRest);
                break;
            case R.id.plusRestSets:
                plusTempo(RestSets,plusRestSet,minusRestSet);
                break;
            case R.id.minusRestSets:
                minusTempo(RestSets,minusRestSet,plusRestSet);
                break;

        }
    }
}