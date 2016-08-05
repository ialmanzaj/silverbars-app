package com.app.proj.silverbars;

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

public class CreateWorkoutActivity extends AppCompatActivity {

    private static final String TAG ="Create Workout activity" ;
    private WebView webView;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private Button re_addExercise;

    private LinearLayout empty_content;

    ArrayList<String> sItems;
    private  List<String> MusclesArray = new ArrayList<>();

    public static boolean active = false;

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
                    //startActivity(new Intent(CreateWorkoutActivity.this, CreateWorkoutFinalActivity.class));
//                String name = workoutName.getText().toString();
//                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
//                sqLiteHelper.insertWorkouts(name,);
            }
        });



        //botton de agregar ejercicios
        Button addExercise = (Button) findViewById(R.id.add_exercises);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);
                startActivityForResult(intent,1);
            }
        });

        // boton para volver agregar mas ejercicios
        re_addExercise = (Button) findViewById(R.id.reAdd);
        re_addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateWorkoutActivity.this,ExerciseListActivity.class);

                if (sItems != null){
                    intent.putExtra("items_selected",sItems);
                    Log.v(TAG,"items_selected"+sItems);
                }

                startActivityForResult(intent,1);
            }
        });


        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.recycler_exercises_selected);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
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
                super.onPageFinished(view, url);
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        String fileurl = "file://"+Environment.getExternalStorageDirectory()+"/html/"+"index.html";
        webView.loadUrl(fileurl);





    }//  close create workout



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 ) {
            if (resultCode == RESULT_OK && data != null){

                if (data.hasExtra("Items")) {
                    sItems = data.getStringArrayListExtra("Items");

                    Log.v(TAG,"NAMES ID: "+sItems);
                    Exercises(sItems);

                }
            }
        }


    }
    private String partes = "";

    private void setMusclesToView(List<String> musculos){

        if ( musculos.size() > 0 ){

            // asignar musculos a text view si es primario o secundario
            for (String s : musculos)
            {
                /*final TextView MuscleView = new TextView(WorkoutActivity.this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MuscleView.setTextColor(getResources().getColor(R.color.gray_active_icon,null));
                }else {
                    MuscleView.setTextColor(getResources().getColor(R.color.gray_active_icon));
                }*/

                partes += "#"+ s + ",";
                //MuscleView.setGravity(Gravity.CENTER);
                //MuscleView.setText(s);

                //Log.v(TAG, (String) MuscleView.getText());
                //primary_linear.addView(MuscleView);

            }
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                injectJS();
                super.onPageFinished(view, url);
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        String fileurl = "file://"+Environment.getExternalStorageDirectory()+"/html/"+"index.html";
        webView.loadUrl(fileurl);

    }
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

    private void injectJS() {
        try {

            partes = removeLastChar(partes);
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


    public void Exercises(final ArrayList<String> sItems_selected){
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
                    List<JsonExercise> selectedExercises = new ArrayList<>();
                    List<JsonExercise> CompleteExercises = new ArrayList<>();
                    Collections.addAll(CompleteExercises, parsedExercises);



                    for (int c = 0;c < sItems_selected.size();c++){
                        for (int a = 0; a < CompleteExercises.size(); a++){
                            if (Objects.equals(CompleteExercises.get(a).getExercise_name(), sItems_selected.get(c))){
                                selectedExercises.add(CompleteExercises.get(a));

                                for (int b = 0; b < selectedExercises.get(c).muscle.length; b++){
                                    String name;
                                    name = selectedExercises.get(c).muscle[b];
                                    MusclesArray.add(name);

                                    Log.v(TAG,"MusclesArray: "+MusclesArray);
                                }

                                Log.v(TAG,"selectedExercises: "+selectedExercises.get(c).getExercise_name());
                            }
                        }
                    }

                    Log.v(TAG,"selectedExercises: "+selectedExercises);

                    // ASIGNAR ADAPTER DE EJERCICIOS ELEGIDOS

                    adapter = new selectedExercisesAdapter(CreateWorkoutActivity.this,selectedExercises);
                    recycler.setAdapter(adapter);
                    setMusclesToView(MusclesArray);


                    /*ItemTouchHelper.Callback callback =
                            new SimpleItemTouchHelperCallback(adapter);
                    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                    touchHelper.attachToRecyclerView(recycler);
                    */

                    empty_content.setVisibility(View.GONE);
                    re_addExercise.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.VISIBLE);

                    //addExercise.setVisibility(View.GONE);

                } else {
                    int statusCode = response.code();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG,errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonExercise[]> call, Throwable t) {
                Log.e(TAG,"onFailure",t);
            }
        });
    }// exercises method close

    @Override
    protected void onStart() {
        super.onStart();
        active = true;



    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;


    }




}
