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
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.SilverbarsApp;
import com.app.app.silverbarsapp.adapters.CreateFinalExercisesAdapter;
import com.app.app.silverbarsapp.components.DaggerCreateWorkoutFinalComponent;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.modules.CreateWorkoutFinalModule;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.presenters.CreateWorkoutFinalPresenter;
import com.app.app.silverbarsapp.utils.MuscleHandler;
import com.app.app.silverbarsapp.utils.Utilities;
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

    @BindView(R.id.strength)SeekBar strenghtBar;
    @BindView(R.id.porcentaje) TextView mPorcentajeTextView;

    private int mCurrentSet = 1;
    private String workoutImage = "/";

    private CreateFinalExercisesAdapter adapter;
    private Utilities utilities = new Utilities();

    MuscleHandler mMuscleHandler = new MuscleHandler();
    
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
        setupToolbar();


        Bundle extras = getIntent().getExtras();
        ArrayList<ExerciseRep> mExercisesSelected = utilities.returnExercisesRep(extras.getParcelableArrayList("exercises_selected"));

        mExercisesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CreateFinalExercisesAdapter(this, mExercisesSelected);
        mExercisesList.setAdapter(adapter);

        mSets.setText(String.valueOf(mCurrentSet));

        strenghtBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (int a = 0;a<adapter.getExercises().size();a++){
                    ExerciseRep exercise =  adapter.getExercises().get(a);

                    List<String> muscles = new ArrayList<>();
                    for (MuscleExercise muscle: exercise.getExercise().getMuscles()){muscles.add(muscle.getMuscle());}

                    if (muscles.contains("RECTUS-ABDOMINIS")){

                        exercise.setNumber(progress*2);

                    }else {


                        if (exercise.getNumber() < 10){
                            exercise.setNumber(10);
                        }

                        exercise.setWeight(progress*2);
                    }

                    adapter.set(a,exercise);
                }

                mPorcentajeTextView.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @OnClick(R.id.save)
    public void saveButton(){
        
        if (Objects.equals(workoutName.getText().toString(), "")){
            Toast.makeText(this, "Select your workout name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (mCurrentSet < 1){
            Toast.makeText(this, "Select your sets", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!didYouSelectedReps()){
            Toast.makeText(this, "Select your reps or seconds", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            mCreateWorkoutFinalPresenter.saveWorkout(getWorkoutReady());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnTextChanged(value = R.id.sets)
    public void setsButton(CharSequence charSequence) {
        if (!Objects.equals(charSequence.toString(), "")) {
            mCurrentSet = Integer.parseInt(charSequence.toString());
        }
    }

    @OnClick(R.id.chageImg)
    public void changeImgButton(){
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
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();
                    workoutImage = resultUri.getPath();
                    Log.d(TAG,"workoutImage "+workoutImage);

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
        workout.setMain_muscle(mMuscleHandler.getMainMuscle(adapter.getExercises()));

        return workout;
    }



    @Override
    public void onWorkoutCreated(boolean created) {
        if (created){
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

    private boolean didYouSelectedReps(){
        for (ExerciseRep exerciseRep: adapter.getExercises()){
            if ( exerciseRep.getNumber() < 1) {
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