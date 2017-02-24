package com.app.proj.silverbarsapp.adapters;

import android.content.Context;
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

import com.app.proj.silverbarsapp.R;
import com.app.proj.silverbarsapp.activities.WorkoutActivity;
import com.app.proj.silverbarsapp.models.ExerciseRep;
import com.app.proj.silverbarsapp.models.Workout;
import com.app.proj.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SavedWorkoutsAdapter extends RecyclerView.Adapter<SavedWorkoutsAdapter.ViewHolder> {

    private static final String TAG = SavedWorkoutsAdapter.class.getSimpleName();


    private List<Workout> workouts;
    private Context context;
    private Utilities utilities = new Utilities();


    public SavedWorkoutsAdapter(Context context) {
        this.context = context;
        this.workouts = new ArrayList<>();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout)FrameLayout layout;
        @BindView(R.id.img_local)ImageView img;
        @BindView(R.id.text)TextView text;
        @BindView(R.id.btn)Button btn;



        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.btn)
        public void start(View view){
            Workout workout = (Workout) view.getTag();

            ArrayList<ExerciseRep> exerciseRepList = new ArrayList<>();
            exerciseRepList.addAll( workout.getExercises() );

            Intent intent = new Intent(context, WorkoutActivity.class);
            intent.putExtra("workout_id", workout.getId());
            intent.putExtra("name", workout.getWorkout_name());
            intent.putExtra("image", workout.getWorkout_image());
            intent.putExtra("sets", workout.getSets());
            intent.putExtra("level", workout.getLevel());
            intent.putExtra("main_muscle", workout.getMainMuscle());
            intent.putParcelableArrayListExtra("exercises",  exerciseRepList);
            context.startActivity(intent);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder,  int position) {

        int height = utilities.containerDimensionsHeight(context);

        viewholder.layout.getLayoutParams().height = height / 3;


        String[] workoutImgDir = workouts.get(position).getWorkout_image().split(context.getFilesDir().getPath()+"/SilverbarsImg/");

        if (workoutImgDir.length == 2){
            String workoutImgName = workoutImgDir[1];
            Bitmap imgBitmap;
            imgBitmap = utilities.loadWorkoutImageFromDevice(context,workoutImgName);
            viewholder.img.setImageBitmap(imgBitmap);
        }

        viewholder.text.setText(workouts.get(position).getWorkout_name());
        viewholder.btn.setTag(workouts.get(position));
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }


    public void set(List<Workout> workouts){
        this.workouts.addAll(workouts);
        notifyDataSetChanged();
    }

    public void add(Workout workout){
        workouts.add(0,workout);
       notifyItemInserted(0);
    }




}