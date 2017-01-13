package com.app.proj.silverbars.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.activities.WorkoutActivity;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SavedWorkoutsAdapter extends RecyclerView.Adapter<SavedWorkoutsAdapter.ViewHolder> {

    private static final String TAG = SavedWorkoutsAdapter.class.getSimpleName();


    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private List<Workout> workouts;
    private Activity activity;
    private Boolean user_workout  = false;

    private Utilities utilities;

    public SavedWorkoutsAdapter(Activity activity, List<Workout> workouts, Boolean user_workout) {
        this.activity = activity;
        this.workouts = workouts;
        this.user_workout = user_workout;
        utilities = new Utilities();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout)FrameLayout layout;
        @BindView(R.id.img_local)ImageView img;
        @BindView(R.id.text)TextView text;
        @BindView(R.id.btn)Button btn;


        int mPosition;

        public ViewHolder(View view) {
            super(view);

            //binding views
            ButterKnife.bind(this,view);
        }


        public void setOnClicklistener(){

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = (int) view.getTag();


                    ArrayList<ExerciseRep> exercises = new ArrayList<>();
                    Collections.addAll(exercises,workouts.get(position).getExercises());

                    Intent i = new Intent(activity, WorkoutActivity.class);
                    i.putExtra("id", workouts.get(position).getWorkoutId());
                    i.putExtra("name", workouts.get(position).getWorkout_name());
                    i.putExtra("image",workouts.get(position).getWorkout_image());
                    i.putExtra("sets", workouts.get(position).getSets());
                    i.putExtra("level", workouts.get(position).getLevel());
                    i.putExtra("main_muscle", workouts.get(position).getMainMuscle());
                    i.putParcelableArrayListExtra("exercises", exercises);
                    i.putExtra("user_workout",user_workout);
                    activity.startActivity(i);


                }
            });

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder,  int position) {

        int height = utilities .containerDimensionsHeight(activity);
        viewholder.layout.getLayoutParams().height = height / 3;

        String[] workoutImgDir = workouts.get(position).getWorkout_image().split(activity.getFilesDir().getPath()+"/SilverbarsImg/");

        if (workoutImgDir.length == 2){
            String workoutImgName = workoutImgDir[1];
            Bitmap imgBitmap;
            imgBitmap = utilities.loadWorkoutImageFromDevice(activity,workoutImgName);
            viewholder.img.setImageBitmap(imgBitmap);
        }


        viewholder.text.setText(workouts.get(position).getWorkout_name());
        viewholder.setOnClicklistener();
    }



    @Override
    public int getItemCount() {
        return workouts.size();
    }




}