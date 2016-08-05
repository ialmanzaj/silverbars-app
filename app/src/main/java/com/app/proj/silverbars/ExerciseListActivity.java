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

import java.io.IOException;
import java.util.ArrayList;
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
    public static JsonExercise[] Exercises;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> sItems = new ArrayList<>();

    private ArrayList<String> sItemsComplete = new ArrayList<>();
    List<JsonExercise> OriginalExerciseListAll = new ArrayList<>();

    List<JsonExercise> NewExercisesList = new ArrayList<>();


    ArrayList<String> SelectedItemsIds = new ArrayList<>();

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


        recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);



        add_button = (Button) findViewById(R.id.done_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (int i = 0; i < OriginalExerciseListAll.size(); i++){
                    if (AllExercisesAdapter.selectedItems.get(i)){

                        sItems.add(OriginalExerciseListAll.get(i).getExercise_name());

                        try {
                            sItemsComplete = SelectedItemsIds;
                            sItemsComplete.add(OriginalExerciseListAll.get(i).getExercise_name());

                            Log.v(TAG,"SelectedItemsIds 1: "+sItemsComplete);


                        } catch (NullPointerException e){
                            //Log.e(TAG, "no se ha seleccionado nada todavia");
                        }


                    }
                }



                // enviar items a la actividad anterior
                Intent return_Intent = new Intent();

                    if (sItemsComplete.isEmpty()){
                        return_Intent.putExtra("Items",sItems);
                        
                    }else {
                        return_Intent.putExtra("Items",sItemsComplete);

                    }




                setResult(RESULT_OK, return_Intent);
                finish();


                //deselecionar todos los elementos elegidos
                AllExercisesAdapter.selectedItems.clear();



            }
        });


        Exercises();





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


        Call<JsonExercise[]> call = service.getAllExercises();
        call.enqueue(new Callback<JsonExercise[]>() {
            @Override
            public void onResponse(Call<JsonExercise[]> call, Response<JsonExercise[]> response) {
                if (response.isSuccessful()) {

                    Exercises = response.body();

                        for (int i = 0; i < Exercises.length; i++){
                            OriginalExerciseListAll.add(i,Exercises[i]);

                        }


                    Intent i = getIntent();
                    Bundle b = i.getExtras();
                    try {
                        
                        SelectedItemsIds = b.getStringArrayList("items_selected");
                        Log.v(TAG,"items recibido: "+SelectedItemsIds);

                        NewExercisesList = OriginalExerciseListAll;

                        for (int c = 0;c < SelectedItemsIds.size();c++){
                            for (int a = 0; a < OriginalExerciseListAll.size(); a++){
                                if (Objects.equals(OriginalExerciseListAll.get(a).getExercise_name(), SelectedItemsIds.get(c))){
                                    NewExercisesList.remove(a);
                                    
                                    Log.v(TAG,"nueva lista: "+OriginalExerciseListAll);
                                }
                            }
                        }



                    }catch (NullPointerException e){
                        Log.e(TAG, "no se ha seleccionado nada todavia");
                    }


                    if (NewExercisesList.isEmpty()){
                        adapter = new AllExercisesAdapter(ExerciseListActivity.this,OriginalExerciseListAll);
                        Log.v(TAG,"OriginalExerciseListAll: active");
                    }else {
                        adapter = new AllExercisesAdapter(ExerciseListActivity.this,NewExercisesList);
                        Log.v(TAG,"NewExercisesList: active");
                    }

                    recycler.setAdapter(adapter);



                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.v("Error",errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonExercise[]> call, Throwable t) {
                Log.v("Exception",t.toString());
            }
        });
    }
}