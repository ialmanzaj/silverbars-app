package com.app.project.silverbars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;

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
    private JsonExercise[] Exercises;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        add_button = (Button) findViewById(R.id.add_button);
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        Exercises();
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Exercises");
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

                        adapter = new AllExercisesAdapter(Exercises,exerciseList.this);
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
