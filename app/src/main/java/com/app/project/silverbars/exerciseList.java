package com.app.project.silverbars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

//import com.davidecirillo.multichoicerecyclerview.MultiChoiceRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class exerciseList extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recycler;
    Button add_button;
    public static JsonExercise[] Exercises;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
//    private MultiChoiceRecyclerView mMultiChoiceRecyclerView;
    private ArrayList<Integer> sItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
//        mMultiChoiceRecyclerView = (MultiChoiceRecyclerView) findViewById(R.id.multiChoiceRecyclerView);
        add_button = (Button) findViewById(R.id.done_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sItems.clear();
                for (int i = 0; i < Exercises.length; i++){
                    if (AllExercisesAdapter.Selected[i]){
                        sItems.add(Exercises[i].getId());
                    }
                }
                Intent intent = new Intent(exerciseList.this,orderExercises.class);
                intent.putIntegerArrayListExtra("Items",sItems);
                startActivityForResult(intent,1);
                for (int i = 0; i < Exercises.length; i++){
                    Log.v("Elemento"+String.valueOf(i),String.valueOf(AllExercisesAdapter.Selected[i]));
                }

            }
        });
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        Exercises();
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Exercises");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("Order",data.getIntArrayExtra("Order"));
            setResult(RESULT_OK, returnIntent);
            finish();
            Log.v("Result","Ok");
            sItems.clear();
        }
        else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            Log.v("Result","Cancel");
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
            Call<JsonExercise[]> call = service.getAllExercises();
            call.enqueue(new Callback<JsonExercise[]>() {
                @Override
                public void onResponse(Call<JsonExercise[]> call, Response<JsonExercise[]> response) {
                    if (response.isSuccessful()) {
                        Exercises = response.body();
//                        for (int i = 0; i < Exercises.length; i++){
//                            Log.v("Name",Exercises[i].getExercise_name());
//                        }
                        adapter = new AllExercisesAdapter(exerciseList.this);
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
