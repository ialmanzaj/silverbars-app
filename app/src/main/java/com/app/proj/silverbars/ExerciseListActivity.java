package com.app.proj.silverbars;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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


public class ExerciseListActivity extends AppCompatActivity {

    private static final String TAG = "EXERCISE LIST";
    Toolbar toolbar;
    RecyclerView recycler;
    Button add_button;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> sExercises_Id = new ArrayList<>();
    List<JsonExercise> OriginalExerciseListAll = new ArrayList<>();
    List<JsonExercise> ExercisesNoSelected = new ArrayList<>();
    ArrayList<String> SelectedItemsIds = new ArrayList<>();

    private LinearLayout error_layout;
    private LinearLayout Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Exercises");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        Progress = (LinearLayout) findViewById(R.id.progress_bar_);
        error_layout = (LinearLayout) findViewById(R.id.error_layout);
        Button button_error_reload = (Button) findViewById(R.id.error_reload_workout);

        button_error_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_layout.setVisibility(View.GONE);
                Progress.setVisibility(View.VISIBLE);
                getExercisesFromAPI();

            }
        });


        recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        add_button = (Button) findViewById(R.id.done_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < OriginalExerciseListAll.size(); i++){
                    if (AllExercisesAdapter.selectedItems.get(i)){
                        sExercises_Id.add(OriginalExerciseListAll.get(i).getExercise_name());
                    }
                }

                // enviar items a la actividad anterior
                Intent return_Intent = new Intent();
                return_Intent.putExtra("Items",sExercises_Id);
                setResult(RESULT_OK, return_Intent);
                finish();


                //deselecionar todos los elementos elegidos
                AllExercisesAdapter.selectedItems.clear();
            }
        });

        getExercisesFromAPI();

    }

    private void getExercisesFromAPI() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "auth-token")
                        .method(original.method(), original.body())
                        .build();
                okhttp3.Response response = chain.proceed(request);
                Log.v("Response",response.toString());
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

                    onErrorOff();

                    JsonExercise[] parsedExercises = response.body();
                    Collections.addAll(OriginalExerciseListAll,parsedExercises);

                    Log.v(TAG,"OriginalExerciseListAll size: "+OriginalExerciseListAll.size());

                    try {

                        Intent i = getIntent();
                        Bundle b = i.getExtras();

                        SelectedItemsIds = b.getStringArrayList("items_selected");
                        Log.v(TAG,"items recibido: "+SelectedItemsIds);

                        ExercisesNoSelected = OriginalExerciseListAll;

                        for (int c = 0;c < SelectedItemsIds.size();c++){
                            for (int a = 0; a < OriginalExerciseListAll.size(); a++){

                                //ejercicio seleccionado lo elimina en la siguiente seleccion
                                if (Objects.equals(OriginalExerciseListAll.get(a).getExercise_name(), SelectedItemsIds.get(c))){
                                    ExercisesNoSelected.remove(a);
                                }
                            }
                        }
                    }catch (NullPointerException e){
                        Log.e(TAG, "no se ha seleccionado ningun ejercicio todavia");
                    }


                    if (ExercisesNoSelected.isEmpty()){
                        adapter = new AllExercisesAdapter(ExerciseListActivity.this,OriginalExerciseListAll);
                        //Log.v(TAG,"OriginalExerciseListAll: active");
                    }else {
                        adapter = new AllExercisesAdapter(ExerciseListActivity.this,ExercisesNoSelected);
                        //Log.v(TAG,"ExercisesNoSelected: active");
                    }



                    recycler.setAdapter(adapter);
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG,errorBody.toString());
                    Log.e(TAG,"statusCode"+statusCode);
                    onErrorOn();

                }
            }

            @Override
            public void onFailure(Call<JsonExercise[]> call, Throwable t) {
                Log.e(TAG,"onFailure: ",t);
                onErrorOn();
            }
        });
    }



    private void onErrorOn(){
        error_layout.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        add_button.setVisibility(View.GONE);
    }
    private void onErrorOff(){
        Progress.setVisibility(View.GONE);
        error_layout.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        add_button.setVisibility(View.VISIBLE);
    }




}