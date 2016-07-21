package com.app.project.silverbars;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

public class CreateWorkout extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout addExercise, reAdd;
    ImageView addImg;
    TextView addText;
    private ImageButton changeImg;
    public int[] exerciseOrder;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    public static JsonExercise[] Exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Workout");
        }
        recycler = (RecyclerView) findViewById(R.id.recycler);
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
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
        addImg = (ImageView) findViewById(R.id.addImg);
        addText = (TextView) findViewById(R.id.addText);
        reAdd = (LinearLayout) findViewById(R.id.reAdd);
        reAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        addImg.setImageResource(R.drawable.add_white);
                        addText.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case MotionEvent.ACTION_UP:
                        addImg.setImageResource(R.drawable.add);
                        addText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        Intent intent = new Intent(CreateWorkout.this,exerciseList.class);
                        startActivityForResult(intent,1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        addExercise = (LinearLayout) findViewById(R.id.addExercise);
        addExercise.setClickable(true);
        addExercise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        addImg.setImageResource(R.drawable.add_white);
                        addText.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case MotionEvent.ACTION_UP:
                        addImg.setImageResource(R.drawable.add);
                        addText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        Intent intent = new Intent(CreateWorkout.this,exerciseList.class);
                        startActivityForResult(intent,1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            if (data.hasExtra("Order")){
                exerciseOrder = data.getIntArrayExtra("Order");
                addExercise.setVisibility(View.GONE);
                Exercises(exerciseOrder);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                         Log.d("Image", String.valueOf(bitmap));
                        addImg.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                Log.v("Activity", "Else");
                if (data.getData()!=null){
                    Log.v("Activity", "If");
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("Crop Image")
                            .setAllowRotation(true)
                            .setActivityMenuIconColor(R.color.white)
                            .setFixAspectRatio(true)
                            .setAspectRatio(4,4)
                            .start(this);
                }
            }
        }

    public void Exercises(final int[] newOrder){
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
                    Exercises = new JsonExercise[newOrder.length];
                    int x = 0;
                    for(int i = 0; i < newOrder.length; i++){
                        for (int j = 0; j < parsedExercises.length; j++){
                            if (newOrder[i] == parsedExercises[j].getId()){
                                Exercises[i] = parsedExercises[j];
                            }
                        }
                    }
                    for (int i = 0; i < Exercises.length; i++){
                        Log.v("Element",String.valueOf(Exercises[i].getId()));
                    }

                    adapter = new selectedExercisesAdapter(CreateWorkout.this);
                    recycler.setAdapter(adapter);
                    addExercise.setVisibility(View.GONE);
                    reAdd.setVisibility(View.VISIBLE);
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
