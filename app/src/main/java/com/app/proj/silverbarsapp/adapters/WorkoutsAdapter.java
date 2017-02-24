package com.app.proj.silverbarsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.proj.silverbarsapp.R;
import com.app.proj.silverbarsapp.activities.WorkoutActivity;
import com.app.proj.silverbarsapp.models.ExerciseRep;
import com.app.proj.silverbarsapp.models.Workout;
import com.app.proj.silverbarsapp.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutHolder> {

    private static final String TAG = WorkoutsAdapter.class.getSimpleName();

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private Context context;
    private List<Workout> workouts;

    private Utilities  utilities = new Utilities();


    public WorkoutsAdapter(Context context) {
        this.context = context;
        this.workouts = new ArrayList<>();

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

        @OnClick(R.id.btn)
        public void startWorkout(View view){
            Workout workout = (Workout) view.getTag();

            ArrayList<ExerciseRep> exerciseRepList = new ArrayList<>();
            exerciseRepList.addAll(workout.getExercises());

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
    public WorkoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkoutHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(WorkoutHolder viewHolder, int position) {

        int height = utilities.containerDimensionsHeight(context);
        viewHolder.layout.getLayoutParams().height = height / 3;

        try {

            viewHolder.text.setText(workouts.get(position).getWorkout_name());
            Uri uri = Uri.parse(workouts.get(position).getWorkout_image());
            viewHolder.img.setImageURI(uri);
            viewHolder.btn.setTag(workouts.get(position));

        }catch (NullPointerException e){
            Log.e(TAG,"NullPointerException");
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