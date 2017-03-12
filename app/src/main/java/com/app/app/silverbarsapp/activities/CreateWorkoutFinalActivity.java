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
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.CreateFinalExercisesAdapter;
import com.app.app.silverbarsapp.components.DaggerCreateWorkoutFinalComponent;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.CreateWorkoutFinalModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.CreateWorkoutFinalPresenter;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutFinalView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

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
    private String workoutImage = "/";


    private ArrayList<Exercise> mAllExercisesList;
    private ArrayList<ExerciseRep> mExercisesSelected;

    CreateFinalExercisesAdapter adapter;
    Utilities utilities = new Utilities();
    
    
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
        Log.v(TAG,"onCreate");

        Bundle extras = getIntent().getExtras();

        mAllExercisesList = extras.getParcelableArrayList("exercises");
        mExercisesSelected = utilities.returnExercisesRep(extras.getParcelableArrayList("exercises_selected"));

       // Log.i(TAG,"mAllExercisesList:"+mAllExercisesList);
        //Log.i(TAG,"mAllExercisesList selected "+mExercisesSelected);


        setupToolbar();

        mExercisesList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CreateFinalExercisesAdapter(this, mExercisesSelected);
        mExercisesList.setAdapter(adapter);

        mSets.setText(String.valueOf(mCurrentSet));
    }


    @OnClick(R.id.save)
    public void save(){
        
        if (Objects.equals(workoutName.getText().toString(), "")){
            Toast.makeText(this, "Select your workout name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (mCurrentSet < 1){
            Toast.makeText(this, "Select your sets", Toast.LENGTH_SHORT).show();
            return;
        }

        if (didYouSelectedReps()){
            Toast.makeText(this, "Select your reps or seconds", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            mCreateWorkoutFinalPresenter.saveWorkout(getWorkoutReady());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnTextChanged(value = R.id.sets, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void setsButton(Editable editable) {
        if (!Objects.equals(editable.toString(), "")) {
            mCurrentSet = Integer.parseInt(editable.toString());
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
    
    private Workout getWorkoutReady(){
        Workout workout = new Workout();

        workout.setWorkout_name(workoutName.getText().toString());
        workout.setSets(mCurrentSet);
        workout.setWorkout_image(workoutImage);
        workout.setExercises(adapter.getExercises());
        workout.setLevel("");
        workout.setMain_muscle("");

        return workout;
    }

    @Override
    public void onWorkoutCreated(boolean created) {
        //Log.v(TAG,"onWorkoutCreated: "+created);
        if (created){
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }


    private boolean didYouSelectedReps(){
        for (ExerciseRep exerciseRep: adapter.getExercises()){
            if (exerciseRep.getRepetition() > 0 ||  exerciseRep.getSeconds() > 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onWorkoutError() {
        Log.e(TAG,"onWorkoutError");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}


