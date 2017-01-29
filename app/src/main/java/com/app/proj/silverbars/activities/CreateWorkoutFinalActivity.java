package com.app.proj.silverbars.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.adapters.CreateFinalWorkoutExercisesAdapter;
import com.app.proj.silverbars.models.Exercise;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Muscle;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.viewsets.CreateWorkoutFinalView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class CreateWorkoutFinalActivity extends AppCompatActivity implements CreateWorkoutFinalView{

    private static final String TAG = CreateWorkoutFinalActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;


    @BindView(R.id.img_profile) ImageView imgProfile;
    @BindView(R.id.workout_name) AutoCompleteTextView workoutName;

    @BindView(R.id.save) Button mSaveButton;

    @BindView(R.id.list) RecyclerView list;

    @BindView(R.id.sets) TextView mTotalSets;

    @BindView(R.id.chageImg) RelativeLayout changeImg;


    private String MAIN_MUSCLE,LEVEL;

    int actual_set = 1;
    

    String workoutImage = "/";

    String[] exercises_ids;
    
    List<ExerciseRep> SelectedExercises = new ArrayList<>();
    ArrayList<String> mExercisesIdsSelected = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout_final);



        Bundle extras = getIntent().getExtras();


        setupToolbar();



        list.setLayoutManager(new Utilities.WrappingLinearLayoutManager(this));


        changeImg.setOnClickListener(dialog -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        });


        mTotalSets.setOnClickListener(this::dialogImplementation);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null){

                if (data.getData() != null) {

                        //Log.d("Select", "Image Picker");

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

        }if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                //Log.d(TAG,"Crop Result");


                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    workoutImage = resultUri.getPath();

                    Log.d(TAG,workoutImage);
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

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.save_workout));

        toolbar.setNavigationOnClickListener(dialog -> {
            Intent return_Intent = new Intent();
            return_Intent.putExtra("exercises",mExercisesIdsSelected);
            setResult(RESULT_OK, return_Intent);
            finish();
        });
    }

    private void dialogImplementation(View view){
        TextView mRestbyExerciseDialog;

        TextView RestbySet;
        TextView RestbyExercise;
        TextView RestSets_dialog;

        Button plusRest;
        Button minusRest;
        Button plusRestSets;
        Button minusRestSets;

        TextView Sets_dialog;
        Button plusSets;
        Button minusSets;

       /* View dialog = new MaterialDialog.Builder(view.getContext())
                .title(R.string.set_edit)
                .customView(R.layout.edit_set_setup, true)
                .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        if ( ){
                            mTotalSets.setText(Sets_dialog.getText());
                            actual_set = Integer.getInteger(Sets_dialog.getText().toString());
                        }

                    }
                })
                .show()
                .getCustomView();

        if (dialog != null) {

            Sets_dialog = (TextView) dialog.findViewById(R.id.Sets_dialog);
            Sets_dialog.setText(String.valueOf(mTotalSets.getText()));

            plusSets = (Button) dialog.findViewById(R.id.plusSets);
            plusSets.setOnClickListener(view12 -> {


            });
            minusSets = (Button) dialog.findViewById(R.id.minusSets);
            minusSets.setOnClickListener(view1 -> {

            });

        }*/

    }

    @Override
    public void displayNetworkError() {
        
    }

    @Override
    public void displayServerError() {

    }

    @Override
    public void displayExercises(List<Exercise> exercises) {

        for (int c = 0;c < mExercisesIdsSelected.size();c++){
            for (int a = 0; a < exercises.size();a++){

                if (Objects.equals(exercises.get(a).getExercise_name(), mExercisesIdsSelected.get(c))){
                    SelectedExercises.add(new ExerciseRep(exercises.get(a)));
                }
            }
        }


        for (int a = 0; a<SelectedExercises.size();a++){
            exercises_ids[a] = String.valueOf(SelectedExercises.get(a).getExercise().getExerciseId());
        }


        Boolean UPPER_BODY = false,LOWER_BODY = false,ABS = false,FULL_BODY=false,NORMAL= false,EASY = false,HARD = false,CHALLENGING = false;


        for (int a = 0;a<SelectedExercises.size();a++){

            for (int b = 0; b<SelectedExercises.get(a).getExercise().getMuscles().length; b++){

                Muscle muscle = SelectedExercises.get(a).getExercise().getMuscles()[b];

                if (Objects.equals(muscle.getMuscleName(), "CALVES")){LOWER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "HAMSTRINGS")){LOWER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "ADDUCTORS")){LOWER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "CUADRICEPS")){LOWER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "RECTUS-ABDOMINIS")){ABS = true;}
                if (Objects.equals(muscle.getMuscleName(), "TRANSVERSUS-ABDOMINIS")){ABS = true;}
                if (Objects.equals(muscle.getMuscleName(), "DELTOIDS")){UPPER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "OBLIQUES")){UPPER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "QUADRICEPS")){LOWER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "PECTORALIS-MAJOR")){UPPER_BODY = true;}
                if (Objects.equals(muscle.getMuscleName(), "TRICEPS")){UPPER_BODY = true;}
            }
            Exercise exercise = SelectedExercises.get(a).getExercise();

            if (Objects.equals(exercise.getLevel(),"NORMAL")){
                NORMAL = true;
            }else if (Objects.equals(exercise.getLevel(),"EASY")){
                EASY = true;
            }else if (Objects.equals(exercise.getLevel(),"HARD")){
                HARD = true;
            }else if (Objects.equals(exercise.getLevel(),"CHALLENGING")){
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

        //Log.d(TAG,"MAIN_MUSCLE: "+MAIN_MUSCLE);
        //Log.d(TAG,"lower:"+LOWER_BODY);
        //Log.d(TAG,"upper:"+UPPER_BODY);
        //Log.d(TAG,"ABS:"+ABS);
        //Log.d(TAG,"FULL:"+FULL_BODY);



        list.setAdapter(new CreateFinalWorkoutExercisesAdapter(this, SelectedExercises));
    }





}


