package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

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



public class CreateWorkoutActivity extends AppCompatActivity implements OnStartDragListener {

    private static final String TAG ="Create Workout activity" ;
    private WebView webView;
    private RecyclerView recycler;
    private RecyclerExerciseSelectedAdapter adapter;
    private Button re_addExercise;

    private LinearLayout empty_content;
    private ItemTouchHelper mItemTouchHelper;

    private  List<String> MusclesArray = new ArrayList<>();
    private static String strSeparator = "__,__";

    private int Items_size = 0;
    JsonExercise[] parsedExercises;
    public static Activity create;
    
    private List<JsonExercise> ExercisesToAdapter = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        create = this;

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


                if ( adapter != null ){

                    if (adapter.getItemCount() > 0){
                        Intent intent = new Intent(CreateWorkoutActivity.this, CreateWorkoutFinalActivity.class);
                        intent.putExtra("exercises", adapter.getSelectedExercisesName());
                        intent.putExtra("reps",adapter.getExerciseReps());

                        startActivityForResult(intent,3);

                    }else {
                        Toast.makeText(CreateWorkoutActivity.this, getResources().getString(R.string.exercises_no_selected),
                                Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(CreateWorkoutActivity.this, getResources().getString(R.string.exercises_no_selected),
                            Toast.LENGTH_SHORT).show();

                }



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
                intent.putExtra("items_selected",adapter.getSelectedExercisesName() );
                Log.v(TAG,"items_selected"+adapter.getSelectedExercisesName() );
                startActivityForResult(intent,2);
                
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

        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);

        Log.v(TAG,"muscle_url: "+muscle_url);
        String fileurl = "file://"+muscle_url;
        webView.loadUrl(fileurl);

        if (adapter != null){
            if (adapter.getItemCount() > 0) {
                Log.v(TAG,"hay algo: "+adapter.getSelectedExercisesName());
            }
        }



        getExercises();
    }//  close create workout




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 ) {
            if (resultCode == RESULT_OK && data != null){

                    if (data.hasExtra("Items")) {
                        ArrayList<String> items_ids_from_activity = data.getStringArrayListExtra("Items");
                        Log.v(TAG, "NAMES ID: " + items_ids_from_activity);


                        if (items_ids_from_activity.size() > 0){
                            putExercisesinRecycler(items_ids_from_activity);
                            Log.v(TAG, "primeros items añadidos: " + items_ids_from_activity);
                        }

                    }
            }
        }if (requestCode == 2 ) {
            Log.v(TAG, "requestCode: 2");

            if (resultCode == RESULT_OK && data != null){

                if (data.hasExtra("Items")) {

                    ArrayList<String> new_Items_id = data.getStringArrayListExtra("Items");
                    Log.v(TAG, "NAMES ID: " + new_Items_id);

                    putExercisesinRecycler(new_Items_id);
                }

            }

        } if (requestCode == 3 ){
            if (resultCode == RESULT_OK && data != null){

                ArrayList<String> exercises_id = data.getStringArrayListExtra("exercises");

                //getExercisesFromJson(exercises_id);
            }
        }



    }
    private String partes = "";

    private void setMusclesToView(List<String> musculos){

        if ( musculos.size() > 0 ){
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


        SharedPreferences sharedPref = this.getSharedPreferences("Mis preferencias",Context.MODE_PRIVATE);
        String default_url = getResources().getString(R.string.muscle_path);
        String muscle_url = sharedPref.getString(getString(R.string.muscle_path),default_url);


        String fileurl = "file://"+muscle_url;
        webView.loadUrl(fileurl);

    }
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

    private void injectJS() {
        try {

            if (partes != null){

                partes = removeLastChar(partes);
                webView.loadUrl("javascript: ("+ "window.onload = function () {"+

                        "partes = Snap.selectAll('"+partes+"');"+
                        "partes.forEach( function(elem,i) {"+
                        "elem.attr({fill: '#602C8D',stroke: '#602C8D',});"+
                        "});"+ "}"+  ")()");
            }


            //Log.v("MAIN ACTIVITY","HA EJECUTADO EL JAVASCRIPT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getExercises(){

        MySQLiteHelper database = new MySQLiteHelper(CreateWorkoutActivity.this);

        int total_of_exercises = 5;

        if (database.getAllExercises() == null){
            insertExercisesInDataBase();
        }else if (database.getAllExercises().length != total_of_exercises){
            insertExercisesInDataBase();
        }else {
            Log.v(TAG,"ya se ha agregado todos los ejercicios: " +database.getAllExercises().length);
        }


    }

    private void insertExercisesInDataBase(){

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

                    parsedExercises = response.body();

                    MySQLiteHelper database = new MySQLiteHelper(CreateWorkoutActivity.this);

                    for (int i = 0; i < parsedExercises.length; i++) {

                        if (!database.checkExercise(parsedExercises[i].getId())) {

                            String imgDir = Environment.getExternalStorageDirectory() + "/SilverbarsImg/" + imageName(i);
                            String mp3Dir = Environment.getExternalStorageDirectory() + "/SilverbarsMp3/" + audioName(i);
                            database.insertExercises(
                                    parsedExercises[i].getId(),
                                    parsedExercises[i].getExercise_name(),
                                    parsedExercises[i].getLevel(),
                                    convertArrayToString(parsedExercises[i].getType_exercise()),
                                    convertArrayToString(parsedExercises[i].getMuscle()),
                                    mp3Dir,
                                    imgDir
                            );

                        }else {
                            Log.v(TAG,"ya se ha agregado ejercicio: "+ parsedExercises[i].getId()+""+parsedExercises[i].getExercise_name());
                        }
                    }


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

    private void putExercisesinRecycler(final ArrayList<String> new_items_to_list){

        Log.v(TAG,"putExercisesinRecycler: "+new_items_to_list);

        empty_content.setVisibility(View.GONE);
        re_addExercise.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.VISIBLE);


            MySQLiteHelper database = new MySQLiteHelper(CreateWorkoutActivity.this);

            List<JsonExercise> AllExercisesList = new ArrayList<>();

            for(int a = 0; a < database.getAllExercises().length;a++){
                AllExercisesList.add(database.getExercise(database.getExercisesIds()[a]));
            }


            Log.v(TAG,"AllExercisesList"+AllExercisesList.size());


            for (int c = 0;c < new_items_to_list.size();c++){
                for (int a = 0; a < AllExercisesList.size(); a++){

                    // si el item seleccionado esta en  la lista principal agregalo
                    //Log.v(TAG,""+AllExercisesList.get(a).getExercise_name()+" : "+new_items_to_list.get(c));

                    if (Objects.equals(AllExercisesList.get(a).getExercise_name(), new_items_to_list.get(c))){

                        //.v(TAG,""+AllExercisesList.get(a).getExercise_name()+":"+Items_ids_from_activity.get(c));

                        if (adapter == null){
                            ExercisesToAdapter.add(AllExercisesList.get(a));

                        }else {
                            ExercisesToAdapter.add(AllExercisesList.get(a));
                            Log.v(TAG,"ITEM INSERTED:"+( ExercisesToAdapter.size()));
                            adapter.notifyItemInserted(ExercisesToAdapter.size());
                        }

                        for (int b = 0; b < ExercisesToAdapter.get(c).muscle.length; b++){
                            String name;
                            name = ExercisesToAdapter.get(c).muscle[b];
                            MusclesArray.add(name);
                        }

                    }
                }
            }

            Context context = CreateWorkoutActivity.this;
            adapter = new RecyclerExerciseSelectedAdapter(context,ExercisesToAdapter,CreateWorkoutActivity.this);
            recycler.setAdapter(adapter);

            setMusclesToView(MusclesArray);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper  = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recycler);


    }


    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    private void saveAllExercises(){
        
        
    }

    public String imageName(int position){
        String[] imageDir = parsedExercises[position].getExercise_image().split("exercises");
        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        String imgName = imagesName[2];
        return imgName;
    }

    public String audioName(int position){
        String[] audioDir = parsedExercises[position].getExercise_audio().split("exercises");;
        String Parsedurl = "exercises"+audioDir[1];
        String[] splitName = Parsedurl.split("/");
        String mp3Name = splitName[2];
        return mp3Name;
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();


    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
