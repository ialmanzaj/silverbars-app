package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.CreateFinalWorkoutExercisesAdapter;
import com.app.app.silverbarsapp.components.DaggerCreateWorkoutFinalComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.CreateWorkoutFinalModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.CreateWorkoutFinalPresenter;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutFinalView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateWorkoutFinalActivity extends BaseActivity implements CreateWorkoutFinalView{

    private static final String TAG = CreateWorkoutFinalActivity.class.getSimpleName();


    @Inject
    CreateWorkoutFinalPresenter mCreateWorkoutFinalPresenter;


    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.sets) TextView mSets;
    @BindView(R.id.img_profile) ImageView imgProfile;
    @BindView(R.id.workout_name) AutoCompleteTextView workoutName;

    @BindView(R.id.save) Button mSaveButton;
    @BindView(R.id.list) RecyclerView mExercisesList;
    @BindView(R.id.chageImg) RelativeLayout changeImg;

    private int mCurrentSet = 1;

    String workoutImage = "/";

    private ArrayList<Exercise> mAllExercisesList;
    private ArrayList<ExerciseRep> mExercisesSelected;
    
    
    @Override
    protected int getLayout() {
        return R.layout.activity_create_workout_final;
    }


    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return mCreateWorkoutFinalPresenter;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();

        DaggerCreateWorkoutFinalComponent.builder()
                .silverbarsComponent(SilverbarsApp.getApp(this).getComponent())
                .createWorkoutFinalModule(new CreateWorkoutFinalModule(this))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        mAllExercisesList = extras.getParcelableArrayList("exercises");
        ArrayList<Exercise> exercises_selected = extras.getParcelableArrayList("exercises_selected");

        mExercisesSelected = getExerciseRep(exercises_selected);

        Log.i(TAG,"mAllExercisesList:"+mAllExercisesList);
        Log.i(TAG,"mAllExercisesList selected "+mExercisesSelected);


        setupToolbar();

        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        mExercisesList.setAdapter(new CreateFinalWorkoutExercisesAdapter(this, mExercisesSelected));

        mSets.setText(String.valueOf(mCurrentSet));
    }


    
    private ArrayList<ExerciseRep> getExerciseRep(List<Exercise> exercises){
        ArrayList<ExerciseRep> exerciseReps = new ArrayList<>();
        
        for (Exercise exercise: exercises){
            ExerciseRep exerciseRep = new ExerciseRep();
            exerciseRep.setExercise(exercise);
            exerciseReps.add(exerciseRep);
        }
        
        return exerciseReps;
    }


    
    @OnClick(R.id.sets)
    public void selectSets(View view){
        Log.i(TAG,"sets");
        dialogImplementation(view);
    }

    @OnClick(R.id.save)
    public void save(){
        
        if (Objects.equals(workoutName.getText().toString(), "")){
            Toast.makeText(this, "Select your workout name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (mCurrentSet <= 0){
            Toast.makeText(this, "Select your sets", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            mCreateWorkoutFinalPresenter.saveWorkout(getWorkoutready());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.chageImg)
    public void changeImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }


    public void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.save_workout));

      /*  toolbar.setNavigationOnClickListener(dialog -> {
            Intent return_Intent = new Intent();
            return_Intent.putExtra("mAllExercisesList",mExercisesIdsSelected);
            setResult(RESULT_OK, return_Intent);
            finish();
        });*/

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

    private void dialogImplementation(View view){
        TextView mRestbyExerciseDialog;

        TextView RestbySet;
        TextView RestbyExercise;
        TextView RestSets_dialog;

        Button plusRest;
        Button minusRest;
        Button plusRestSets;
        Button minusRestSets;

        TextView Sets_dialog = null;
        Button plusSets;
        Button minusSets;

       /* View dialog = new MaterialDialog.Builder(view.getContext())
                .title(R.string.set_edit)
                .customView(R.layout.edit_set_setup, true)
                .positiveText(R.string.done_text).onPositive((dialog1, which) -> {
                    dialog1.dismiss();

                    if (mCurrentSet > 0){
                        mSets.setText(mCurrentSet);
                    }

                })
                .show()
                .getCustomView();

        if (dialog != null) {

            Sets_dialog = (TextView) dialog.findViewById(R.id.Sets_dialog);
            Sets_dialog.setText(String.valueOf(mSets.getText()));

            plusSets = (Button) dialog.findViewById(R.id.plusSets);
            plusSets.setOnClickListener(view12 -> {});
            minusSets = (Button) dialog.findViewById(R.id.minusSets);
            minusSets.setOnClickListener(view1 -> {});

        }*/

    }
    
    private Workout getWorkoutready(){
        Workout workout = new Workout();

        workout.setWorkout_name(workoutName.getText().toString());
        workout.setSets(mCurrentSet);
        workout.setWorkout_image(workoutImage);
        workout.setExercises(mExercisesSelected);
        workout.setLevel("");
        workout.setMain_muscle("");

        return workout;
    }

    @Override
    public void onWorkoutCreated(boolean created) {
        Intent return_intent = new Intent();
        setResult(RESULT_OK, return_intent);
        finish();
    }


    @Override
    public void onWorkoutError() {
        Log.e(TAG,"onWorkoutError");
    }


}


