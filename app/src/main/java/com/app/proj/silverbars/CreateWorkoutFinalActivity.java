package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import static com.app.proj.silverbars.Utilities.getFileReady;
import static com.app.proj.silverbars.Utilities.getUrlReady;
import static com.app.proj.silverbars.Utilities.saveAudioInDevice;
import static com.app.proj.silverbars.Utilities.saveWorkoutImgInDevice;

public class CreateWorkoutFinalActivity extends AppCompatActivity {

    private static final String TAG = "CreateWorkoutFinal";

    Toolbar toolbar;
    ImageView imgProfile;
    AutoCompleteTextView workoutName;
    Button Save;
    private RecyclerView recycler;
    TextView Sets, RestbySet, RestbyExercise,RestSets_dialog,Sets_dialog;
    private Button plusSets,minusSets,plusRest,minusRest,plusRestSets,minusRestSets;

    int[] reps;
    ArrayList<String> Exercises = new ArrayList<>();
    int value = 0,actual_set;
    private TextView Rest_by_exercise_dialog;
    static String MAIN_MUSCLE,LEVEL;
    private static String strSeparator = "__,__";

    String workoutImage = "/";
    String[] exercises_ids;
    List<JsonExercise> SelectedExercises = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout_final);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        Exercises = b.getStringArrayList("exercises");
        reps = b.getIntArray("reps");

        exercises_ids = new String[Exercises.size()];
        Log.v(TAG,"exercises"+Exercises);
        Log.v(TAG,"reps"+ Arrays.toString(reps));

        // cantidad de sets
        Sets = (TextView) findViewById(R.id.Sets);

        //descanso entre ejercicio
        RestbyExercise = (TextView) findViewById(R.id.RestbyExercise);

        // descanso entre sets
        RestbySet = (TextView) findViewById(R.id.RestbySet);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.save_workout));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent return_Intent = new Intent();
                    return_Intent.putExtra("exercises",Exercises);
                    setResult(RESULT_OK, return_Intent);
                    finish();
                }
            });
        }


        workoutName = (AutoCompleteTextView) findViewById(R.id.workoutName);

        Save = (Button) findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWorkoutInDB();
            }
        });

        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.final_recycler);
        RecyclerView.LayoutManager lManager = new Utilities.WrappingLinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        imgProfile = (ImageView) findViewById(R.id.imgProfile);

        RelativeLayout changeImg = (RelativeLayout) findViewById(R.id.chageImg);
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


        putExercisesinRecycler();
        actual_set = 1;
        Sets.setText(String.valueOf(actual_set));

        Sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.set_edit)
                        .customView(R.layout.edit_set_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                Sets.setText(Sets_dialog.getText());
                                actual_set = Integer.getInteger(Sets_dialog.getText().toString());
                            }
                        })
                        .show()
                        .getCustomView();
                if (v != null) {

                    Sets_dialog = (TextView) v.findViewById(R.id.Sets_dialog);
                    Sets_dialog.setText(String.valueOf(Sets.getText()));

                    plusSets = (Button) v.findViewById(R.id.plusSets);
                    plusSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            plusTempo(Sets_dialog,plusSets,minusSets);

                        }
                    });
                    minusSets = (Button) v.findViewById(R.id.minusSets);
                    minusSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            minusTempo(Sets_dialog,minusSets,plusSets);
                        }
                    });


                    int setsValue = Integer.parseInt(Sets.getText().toString());
                    if (setsValue <= 1){
                        plusSets.setEnabled(true);
                        plusSets.setClickable(true);
                        minusSets.setEnabled(false);
                        minusSets.setClickable(false);
                    }else if(setsValue >=10){
                        plusSets.setEnabled(false);
                        plusSets.setClickable(false);
                        minusSets.setEnabled(true);
                        minusSets.setClickable(true);
                    }
                }

            }
        });


        RestbyExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rest_exercise_text)
                        .customView(R.layout.edit_rest_exercise_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                RestbyExercise.setText(Rest_by_exercise_dialog.getText());
                            }
                        })
                        .show()
                        .getCustomView();
                if (v != null) {
                    Rest_by_exercise_dialog = (TextView) v.findViewById(R.id.Rest_exercise);
                    Rest_by_exercise_dialog.setText(String.valueOf(RestbyExercise.getText()));


                    plusRest = (Button) v.findViewById(R.id.plusRest);
                    plusRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plusTempo(Rest_by_exercise_dialog,plusRest,minusRest);
                        }
                    });
                    minusRest = (Button) v.findViewById(R.id.minusRest);
                    minusRest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(Rest_by_exercise_dialog,minusRest,plusRest);
                        }
                    });
                }


            }
        });



        RestbySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rest_sets_text)
                        .customView(R.layout.edit_rest_sets_setup, true)
                        .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                RestbySet.setText(RestSets_dialog.getText());
                            }
                        })
                        .show()
                        .getCustomView();

                if (v != null) {

                    RestSets_dialog = (TextView) v.findViewById(R.id.RestSets_dialog);
                    RestSets_dialog.setText(String.valueOf(RestbySet.getText()));

                    plusRestSets = (Button) v.findViewById(R.id.plusRestSets);
                    plusRestSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plusTempo(RestSets_dialog,plusRestSets,minusRestSets);



                        }
                    });
                    minusRestSets = (Button) v.findViewById(R.id.minusRestSets);
                    minusRestSets.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            minusTempo(RestSets_dialog,minusRestSets,plusRestSets);

                        }
                    });
                }


            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 ) {
            if (resultCode == RESULT_OK && data != null){

                if (data.getData() != null) {

                        Log.v("Select", "Image Picker");
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
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Log.v(TAG,"Crop Result");
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                workoutImage = resultUri.getPath();

                Log.v(TAG,workoutImage);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imgProfile.setImageBitmap(bitmap);

                } catch (IOException e) {
                    Log.e(TAG,"IOException",e);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG,"error",error);
            }
        }
    }


    private void putExercisesinRecycler(){


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
                    JsonExercise[] exercises = response.body();
                    List<JsonExercise> AllExercisesList = new ArrayList<>();


                    Collections.addAll(AllExercisesList,exercises);

                    for (int c = 0;c < Exercises.size();c++){
                        for (int a = 0; a < AllExercisesList.size();a++){

                            if (Objects.equals(AllExercisesList.get(a).getExercise_name(), Exercises.get(c))){

                                SelectedExercises.add(AllExercisesList.get(a));

                            }
                        }
                    }
                    for (int a = 0; a<SelectedExercises.size();a++){
                        exercises_ids[a] = String.valueOf(SelectedExercises.get(a).getId());
                    }


                    Boolean UPPER_BODY = false,LOWER_BODY = false,ABS = false,FULL_BODY=false,NORMAL= false,EASY = false,HARD = false,CHALLENGING = false;


                    for (int a = 0;a<SelectedExercises.size();a++){

                        for (int b = 0;b<SelectedExercises.get(a).getMuscle().length;b++){

                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "CALVES")){LOWER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "HAMSTRINGS")){LOWER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "ADDUCTORS")){LOWER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "CUADRICEPS")){LOWER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "RECTUS-ABDOMINIS")){ABS = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "TRANSVERSUS-ABDOMINIS")){ABS = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "DELTOIDS")){UPPER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "OBLIQUES")){UPPER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "QUADRICEPS")){LOWER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "PECTORALIS-MAJOR")){UPPER_BODY = true;}
                            if (Objects.equals(SelectedExercises.get(a).getMuscle()[b], "TRICEPS")){UPPER_BODY = true;}
                        }
                        if (Objects.equals(SelectedExercises.get(a).getLevel(),"NORMAL")){
                            NORMAL = true;
                        }else if (Objects.equals(SelectedExercises.get(a).getLevel(),"EASY")){
                            EASY = true;
                        }else if (Objects.equals(SelectedExercises.get(a).getLevel(),"HARD")){
                            HARD = true;
                        }else if (Objects.equals(SelectedExercises.get(a).getLevel(),"CHALLENGING")){
                            CHALLENGING = true;
                        }

                    }

                    if (CHALLENGING){
                        LEVEL = "CHALLENGING";
                        HARD = false;
                        EASY = false;
                        NORMAL = false;
                    }else if (HARD){
                        LEVEL = "HARD";
                        NORMAL = false;
                        EASY = false;
                    }else if (NORMAL){
                        LEVEL = "NORMAL";
                        EASY = false;
                    }else if (EASY){
                        LEVEL = "EASY";
                    }

                    Log.v(TAG,"LEVEL: "+LEVEL);
                    Log.v(TAG,"CHALLENGING: "+CHALLENGING);
                    Log.v(TAG,"HARD:"+HARD);
                    Log.v(TAG,"EASY:"+EASY);
                    Log.v(TAG,"NORMAL:"+NORMAL);


                    if (LOWER_BODY && UPPER_BODY && ABS){
                        MAIN_MUSCLE = "FULL BODY";
                        FULL_BODY = true;
                        UPPER_BODY = false;
                        ABS = false;
                        LOWER_BODY = false;
                    }else if (!LOWER_BODY && UPPER_BODY && ABS) {
                        UPPER_BODY = true;
                        ABS = false;
                        MAIN_MUSCLE = "UPPER BODY";

                    }else if (LOWER_BODY && !UPPER_BODY && ABS){
                        MAIN_MUSCLE = "LOWER BODY";
                        LOWER_BODY = true;
                        ABS = false;
                    }else if (!LOWER_BODY && !UPPER_BODY && ABS){
                        ABS = true;
                        MAIN_MUSCLE = "ABS/CORE";
                    }

                    Log.v(TAG,"MAIN_MUSCLE: "+MAIN_MUSCLE);
                    Log.v(TAG,"lower:"+LOWER_BODY);
                    Log.v(TAG,"upper:"+UPPER_BODY);
                    Log.v(TAG,"ABS:"+ABS);
                    Log.v(TAG,"FULL:"+FULL_BODY);



                    Context context = CreateWorkoutFinalActivity.this;
                    RecyclerView.Adapter adapter = new FinalExercisesAdapter(context, SelectedExercises,reps);
                    recycler.setAdapter(adapter);




                } else {
                    int statusCode = response.code();
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

    private void saveWorkoutInDB(){
        MySQLiteHelper database = new MySQLiteHelper(CreateWorkoutFinalActivity.this);

        for (int i = 0; i < SelectedExercises.size(); i++){
            if (!database.checkExercise(SelectedExercises.get(i).getId()) ){
                File imgDir,mp3Dir;
                imgDir = getFileReady(this,"/SilverbarsImg/"+getImageName(i));
                mp3Dir = getFileReady(this,"/SilverbarsMp3/"+getAudioName(i));

                if (!imgDir.exists()){
                    Log.v(TAG,"img url "+SelectedExercises.get(i).exercise_image);
                    DownloadImage(SelectedExercises.get(i).exercise_image,getImageName(i));
                }
                if (!mp3Dir.exists()){
                    Log.v(TAG,"mp3 url "+SelectedExercises.get(i).exercise_audio);
                    DownloadMp3(SelectedExercises.get(i).exercise_audio,getAudioName(i));
                }
                database.insertExercises(
                        SelectedExercises.get(i).getId(),
                        SelectedExercises.get(i).getExercise_name(),
                        SelectedExercises.get(i).getLevel(),
                        convertArrayToString(SelectedExercises.get(i).getType_exercise()),
                        convertArrayToString(SelectedExercises.get(i).getMuscle()),
                        mp3Dir.getPath(),
                        imgDir.getPath()
                );
            }
        }
        saveWorkoutinData();
    }

    private void saveWorkoutinData(){

        String name = workoutName.getText().toString();
        int workoutSets = actual_set;
        String workoutLevel = LEVEL;
        String mainMuscle = MAIN_MUSCLE;

        int user_id = 1;
        Log.v(TAG,"name: "+name);
        if (!Objects.equals(name, "")){
            String imgDir = workoutImage;

                MySQLiteHelper database = new MySQLiteHelper(CreateWorkoutFinalActivity.this);
                database.addNewUserWorkouts(
                        name,
                        imgDir,
                        workoutSets,
                        workoutLevel,
                        mainMuscle,
                        convertArrayToString(exercises_ids),
                        user_id
                    );

                int workout_id = database.getUserWorkoutSize();
                for (int i = 0; i < Exercises.size(); i++) {
                    int exerciseid = Integer.parseInt(exercises_ids[i]);
                    database.addNewUserWorkout(
                            workout_id,
                            exerciseid,
                            reps[i]
                    );
                }
                finish();
                CreateWorkoutActivity.create.finish();
        }else {
            Toast.makeText(CreateWorkoutFinalActivity.this, "Elija un nombre para su rutina",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getImageName(int position){
        String[] imageDir = SelectedExercises.get(position).getExercise_image().split("exercises");
        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        Log.v(TAG,"getImageName: "+imagesName[2]);
        return imagesName[2];
    }

    private void DownloadImage(String url, final String imgName){
        Log.v(TAG,"DownloadImage "+imgName);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {

                        Boolean guardado = saveWorkoutImgInDevice(CreateWorkoutFinalActivity.this,response.body(),imgName);

                        if (guardado){
                            Log.v(TAG,"download complete "+imgName);
                        }


                    } else {
                        Log.v(TAG, "Download server contact failed");
                    }
                } else {
                    Log.v(TAG,"State server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,"onFAILURE",t);
            }
        });
    }

    private void DownloadMp3(final String url, final String audioName) {
        Log.v(TAG,"DownloadMp3 "+audioName);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        final SilverbarsService downloadService = retrofit.create(SilverbarsService.class);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... workouts_ids) {
                Call<ResponseBody> call = downloadService.downloadFile(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Boolean guardado = saveAudioInDevice(CreateWorkoutFinalActivity.this,response.body(),audioName);

                            if (guardado){
                                Log.v(TAG,"download complete "+audioName);
                            }

                        }
                        else {Log.e(TAG, "Download server contact failed");}
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "DownloadMp3: onFailure",t);
                    }
                });
                return null;
            };
        }.execute();
    }


    private String getAudioName(int position){
        String[] audioDir = SelectedExercises.get(position).getExercise_audio().split("exercises");;
        String Parsedurl = "exercises"+audioDir[1];
        String[] splitName = Parsedurl.split("/");
        Log.v(TAG,"getAudioName: "+splitName[2]);
        return splitName[2];
    }



    private static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }



    private void plusTempo(TextView view, Button button, Button button2){


        if(view == Rest_by_exercise_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value + 5;
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
        }else if(view == RestSets_dialog){
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

    private void minusTempo(TextView view, Button button, Button button2){

        if(view == Rest_by_exercise_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 5;
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
        }else if(view == RestSets_dialog){
            String[] elements = view.getText().toString().split("s");
            value = Integer.parseInt(elements[0]);
            value = value - 10;
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





}

