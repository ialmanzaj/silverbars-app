package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.activities.WorkoutActivity;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.Utilities;

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


    public void set(List<Workout> workouts){
        this.workouts.addAll(workouts);
        notifyDataSetChanged();
    }

    public void add(Workout workout){
        workouts.add(0,workout);
        notifyItemInserted(0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout)FrameLayout layout;

        @BindView(R.id.text)TextView exercise_name;
        @BindView(R.id.img_local)ImageView exercise_img_local;
        @BindView(R.id.start_button)Button start_button;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.start_button)
        public void start(View view){
            Workout workout = (Workout) view.getTag();

            Intent intent = new Intent(context, WorkoutActivity.class);
            intent.putExtra("workout_id", workout.getId());
            intent.putExtra("name", workout.getWorkout_name());
            intent.putExtra("image", workout.getWorkout_image());
            intent.putExtra("sets", workout.getSets());
            intent.putExtra("level", workout.getLevel());
            intent.putExtra("main_muscle", workout.getMainMuscle());
            intent.putParcelableArrayListExtra("exercises",  workout.getExercises());

            context.startActivity(intent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder,  int position) {

        viewholder.layout.getLayoutParams().height = utilities.calculateContainerHeight(context) / 3;

        viewholder.exercise_name.setText(workouts.get(position).getWorkout_name());
        viewholder.start_button.setTag(workouts.get(position));

        try {

            String[] workoutImgDir = workouts.get(position).getWorkout_image().split(context.getFilesDir().getPath()+"/SilverbarsImg/");

            if (workoutImgDir.length == 2){
                viewholder.exercise_img_local.setImageBitmap( utilities.loadWorkoutImageFromDevice(context,workoutImgDir[1]));
            }


        }catch (NullPointerException e){Log.e(TAG,"NullPointerException");}
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }



}