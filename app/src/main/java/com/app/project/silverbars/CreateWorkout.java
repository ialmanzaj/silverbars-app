package com.app.project.silverbars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

public class CreateWorkout extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    LinearLayout addExercise, reAdd;
    ImageView addImg, imgProfile;
    TextView addText, Sets, Rest, RestSets;
    EditText workoutName;
    Button plusSets, minusSets, plusRest, minusRest, plusRestSet, minusRestSet, Save;
    private ImageButton changeImg;
    public ArrayList<Integer> exerciseOrder = new ArrayList<>();
    public ArrayList<Integer> sItems = new ArrayList<>();
    private int value = 0;
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
        recycler = (RecyclerView) findViewById(R.id.recycler);
        lManager = new LinearLayoutManager(this);
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
        if (requestCode == 1 ) {
            if (resultCode == RESULT_OK && data != null){
                if (data.hasExtra("Order") && data.hasExtra("Items")) {
                    exerciseOrder = data.getIntegerArrayListExtra("Order");
                    sItems = data.getIntegerArrayListExtra("Items");
                    Log.v("sItems", String.valueOf(sItems.size()));
                    Log.v("Order",String.valueOf(exerciseOrder.size()));
                    addExercise.setVisibility(View.GONE);
                    Exercises(exerciseOrder,sItems);
                    Log.v("Order","Result");
                } else {
                    Log.v("Select", "Image Picker");
                    if (data.getData() != null) {
                        CropImage.activity(data.getData())
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setActivityTitle("Crop Image")
                                .setAllowRotation(true)
                                .setActivityMenuIconColor(R.color.white)
                                .setFixAspectRatio(true)
                                .setAspectRatio(4, 4)
                                .start(this);
                    }
                }
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.v("Crop","Result");
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    Log.d("Image", String.valueOf(bitmap));
                    imgProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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
