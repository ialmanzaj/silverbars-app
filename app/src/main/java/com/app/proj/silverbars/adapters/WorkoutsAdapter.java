package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.activities.WorkoutActivity;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.models.Workout;
import com.app.proj.silverbars.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutHolder> {


    private static final String TAG = WorkoutsAdapter.class.getSimpleName();

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private Context context;
    private List<Workout> workouts;

    private Utilities utilities;

    public WorkoutsAdapter(Context context) {
        this.context = context;
        this.workouts = new ArrayList<>();
        utilities = new Utilities();
    }


    public class WorkoutHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout) FrameLayout layout;
        @BindView(R.id.text) TextView text;
        @BindView(R.id.btn)Button btn;
        @BindView(R.id.img)SimpleDraweeView img;

        public WorkoutHolder(View view) {
            super(view);

            //binding views
            ButterKnife.bind(this,view);
        }


        public void setButtonListener(){
            btn.setOnClickListener(view -> {

                Workout workout = (Workout) view.getTag();

                ArrayList<ExerciseRep> exerciseRepList = new ArrayList<>();
                Collections.addAll(exerciseRepList,workout.getExercises());

                Intent i = new Intent(context, WorkoutActivity.class);
                i.putExtra("workout_id", workout.getWorkoutId());
                i.putExtra("name", workout.getWorkout_name());
                i.putExtra("image", workout.getWorkout_image());
                i.putExtra("sets", workout.getSets());
                i.putExtra("level", workout.getLevel());
                i.putExtra("main_muscle", workout.getMainMuscle());
                i.putParcelableArrayListExtra("exercises",  exerciseRepList);
                context.startActivity(i);
            });
        }
    }

    @Override
    public WorkoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_WORKOUT:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
                return new WorkoutHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(WorkoutHolder viewHolder, int position) {

        int height = utilities.containerDimensionsHeight(context);


        viewHolder.layout.getLayoutParams().height = height / 3;


        switch (viewHolder.getItemViewType()) {
            case TYPE_WORKOUT:

                viewHolder.text.setText(workouts.get(position).getWorkout_name());
                Uri uri = Uri.parse(workouts.get(position).getWorkout_image());
                viewHolder.img.setImageURI(uri);

                viewHolder.btn.setTag(workouts.get(position));

                viewHolder.setButtonListener();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < getItemCount() ? TYPE_WORKOUT : TYPE_VIEW_MORE;
    }


    public void setWorkouts(List<Workout> workouts){
        this.workouts.addAll(workouts);
        notifyDataSetChanged();
    }


    public List<Workout> getWorkouts() {
        return workouts;
    }


    public void clear(){
        workouts.clear();
        notifyDataSetChanged();
    }



}