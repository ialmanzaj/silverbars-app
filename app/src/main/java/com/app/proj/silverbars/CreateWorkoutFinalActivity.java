package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Objects;

public class CreateWorkoutFinalActivity extends AppCompatActivity {

    private static final String TAG = "CreateWorkoutFinal";
    Toolbar toolbar;
    LinearLayout addExercise, reAdd;
    ImageView addImg, imgProfile;
    TextView addText, Sets, Rest, RestSets;
    EditText workoutName;
    Button plusSets, minusSets, plusRest, minusRest, plusRestSet, minusRestSet, Save;


    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;

    private int value = 0;

    ArrayList<String> Exercises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout_final);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        Exercises = b.getStringArrayList("exercises");
        Log.v(TAG,"exercises"+Exercises);




        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Save Workout");
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

        // RECYCLER DONDE ESTAN LOS EJERCICIOS ELEGIDOS
        recycler = (RecyclerView) findViewById(R.id.final_recycler);
        RecyclerView.LayoutManager lManager = new WrappingLinearLayoutManager(this);
        recycler.setLayoutManager(lManager);



        ImageButton changeImg = (ImageButton) findViewById(R.id.chageImg);
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

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    Log.d("Image", String.valueOf(bitmap));
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

        MySQLiteHelper database = new MySQLiteHelper(CreateWorkoutFinalActivity.this);


        List<JsonExercise> AllExercisesList = new ArrayList<>();
        List<JsonExercise> SelectedExercises = new ArrayList<>();


            for (int a = 0; a < database.getAllExercises().length;a++){
                AllExercisesList.add(database.getExercise(database.getExercisesIds()[a]));
            }


        for (int c = 0;c < Exercises.size();c++){
            for (int a = 0; a < AllExercisesList.size();a++){

                if (Objects.equals(AllExercisesList.get(a).getExercise_name(), Exercises.get(c))){

                    SelectedExercises.add(AllExercisesList.get(a));
                }
            }

        }


            Context context = CreateWorkoutFinalActivity.this;
            adapter = new FinalExercisesAdapter(context,SelectedExercises);
            recycler.setAdapter(adapter);




    }


    public class WrappingLinearLayoutManager extends LinearLayoutManager {

        public WrappingLinearLayoutManager(Context context) {
            super(context);
        }

        private int[] mMeasuredDimension = new int[2];

        @Override
        public boolean canScrollVertically() {
            return false;
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                              int widthSpec, int heightSpec) {
            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);

            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);

            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                if (getOrientation() == HORIZONTAL) {
                    measureScrapChild(recycler, i,
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            heightSpec,
                            mMeasuredDimension);

                    width = width + mMeasuredDimension[0];
                    if (i == 0) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    measureScrapChild(recycler, i,
                            widthSpec,
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            mMeasuredDimension);

                    height = height + mMeasuredDimension[1];
                    if (i == 0) {
                        width = mMeasuredDimension[0];
                    }
                }
            }

            switch (widthMode) {
                case View.MeasureSpec.EXACTLY:
                    width = widthSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            switch (heightMode) {
                case View.MeasureSpec.EXACTLY:
                    height = heightSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            setMeasuredDimension(width, height);
        }

        private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                       int heightSpec, int[] measuredDimension) {

            View view = recycler.getViewForPosition(position);
            if (view.getVisibility() == View.GONE) {
                measuredDimension[0] = 0;
                measuredDimension[1] = 0;
                return;
            }
            // For adding Item Decor Insets to view
            super.measureChildWithMargins(view, 0, 0);
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(
                    widthSpec,
                    getPaddingLeft() + getPaddingRight() + getDecoratedLeft(view) + getDecoratedRight(view),
                    p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(
                    heightSpec,
                    getPaddingTop() + getPaddingBottom() + getDecoratedTop(view) + getDecoratedBottom(view),
                    p.height);
            view.measure(childWidthSpec, childHeightSpec);

            // Get decorated measurements
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin;
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
    }


}

