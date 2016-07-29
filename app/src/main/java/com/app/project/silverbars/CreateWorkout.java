package com.app.project.silverbars;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;

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

public class CreateWorkout extends AppCompatActivity {

    private static final String TAG ="create workout activity" ;
    private WebView webView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private Button addExercise,readdExercise;
    public static JsonExercise[] Exercises;
    public ArrayList<Integer> exerciseOrder = new ArrayList<>();
    public ArrayList<Integer> sItems = new ArrayList<>();

    private LinearLayout empty_content;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (toolbar != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Workout");
            toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

        empty_content = (LinearLayout) findViewById(R.id.content_empty);


        /// boton de siguiente
        Button nextButton = (Button) findViewById(R.id.Next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(CreateWorkout.this, CreateWorkoutFinalActivity.class));
//                String name = workoutName.getText().toString();
//                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
//                sqLiteHelper.insertWorkouts(name,);
            }
        });



        //botton de agregar ejercicios
        addExercise = (Button) findViewById(R.id.add_exercises);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateWorkout.this,exerciseList.class);
                startActivityForResult(intent,1);
            }
        });

        // boton para volver agregar mas ejercicios
        readdExercise = (Button) findViewById(R.id.reAdd);
        readdExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateWorkout.this,exerciseList.class);
                startActivityForResult(intent,1);
            }
        });

        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.recycler_exercises_selected);
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);




        //Defining Tabs
        TabHost tabHost2 = (TabHost) findViewById(R.id.tabHost3);

        tabHost2.setup();

        TabHost.TabSpec rutina = tabHost2.newTabSpec(getResources().getString(R.string.tab_overview));

        TabHost.TabSpec muscles = tabHost2.newTabSpec(getResources().getString(R.string.tab_muscles));


        rutina.setIndicator(getResources().getString(R.string.tab_overview));
        rutina.setContent(R.id.rutina_);

        muscles.setIndicator(getResources().getString(R.string.tab_muscles));
        muscles.setContent(R.id.muscles_);

        tabHost2.addTab(rutina);

        tabHost2.addTab(muscles);

        webView = (WebView) findViewById(R.id.WebView_create);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJS();
                super.onPageFinished(view, url);
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        String fileurl = "file://"+ Environment.getExternalStorageDirectory()+"/html/"+"index.html";

        webView.loadUrl(fileurl);




    }//  close create workout

    private void injectJS() {
        try {
            String partes = "";


            webView.loadUrl("javascript: ("+ "window.onload = function () {"+

                    "partes = Snap.selectAll('"+partes+"');"+
                    "partes.forEach( function(elem,i) {"+
                    "elem.attr({fill: 'rgba(96%,44%,141%,50%)',stroke: 'rgba(96%,44%,141%,50%)',});"+
                    "});"+ "}"+  ")()");

            //Log.v("MAIN ACTIVITY","HA EJECUTADO EL JAVASCRIPT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 ) {
            if (resultCode == RESULT_OK && data != null){
                if (data.hasExtra("Order") && data.hasExtra("Items")) {
                    exerciseOrder = data.getIntegerArrayListExtra("Order");
                    sItems = data.getIntegerArrayListExtra("Items");
                    Log.v("sItems", String.valueOf(sItems.size()));
                    Log.v("Order",String.valueOf(exerciseOrder.size()));

                    //addExercise.setVisibility(View.GONE);

                    Exercises(exerciseOrder,sItems);
                    Log.v("Order","Result");
                }
            }
        }


    }


    public void Exercises(final ArrayList<Integer> newOrder, final ArrayList<Integer> Items){
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
                    Log.v("sItems", String.valueOf(Items.size()));
                    Log.v("Order",String.valueOf(newOrder.size()));
                    JsonExercise[] parsedExercises = response.body();
                    JsonExercise[] selectedExercises = new JsonExercise[Items.size()];



                    int y = 0;
                    for (int i = 0; i < parsedExercises.length; i++){
                        if (y < Items.size() && Items.get(y) == parsedExercises[i].getId()){
                            selectedExercises[y] = parsedExercises[i];
                            y++;
                        }
                    }
                    Exercises = new JsonExercise[newOrder.size()];
                    for(int i = 0; i < newOrder.size(); i++){
                        for (int j = 0; j < selectedExercises.length; j++){
                            if (newOrder.get(i) == selectedExercises[j].getId()){
                                Exercises[i] = selectedExercises[j];
                            }
                        }
                    }
                    for (int i = 0; i < Exercises.length; i++){
                        Log.v(TAG,String.valueOf(Exercises[i].getId()));
                    }

                    // ASIGNAR ADAPTER DE EJERCICIOS ELEGIDOS

                    adapter = new selectedExercisesAdapter(CreateWorkout.this);
                    recycler.setAdapter(adapter);

                    /*ItemTouchHelper.Callback callback =
                            new SimpleItemTouchHelperCallback(adapter);
                    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                    touchHelper.attachToRecyclerView(recycler);
*/

                    empty_content.setVisibility(View.GONE);

                    recycler.setVisibility(View.VISIBLE);
                    readdExercise.setVisibility(View.VISIBLE);
                    //addExercise.setVisibility(View.GONE);

                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.v(TAG,errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonExercise[]> call, Throwable t) {
                Log.v(TAG,t.toString());
            }
        });
    }// exercises method close






}
