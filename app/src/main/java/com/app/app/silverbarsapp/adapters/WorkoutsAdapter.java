package com.app.app.silverbarsapp.adapters;

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

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.activities.WorkoutActivity;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.Utilities;
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
        @BindView(R.id.btn)Button start_button;
        @BindView(R.id.img)SimpleDraweeView img;

        public WorkoutHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.btn)
        public void startWorkout(View view){
            Workout workout = (Workout) view.getTag();

            Intent intent = new Intent(context, WorkoutActivity.class);
            intent.putExtra("workout_id", workout.getId());
            intent.putExtra("name", workout.getWorkout_name());
            intent.putExtra("image", workout.getWorkout_image());
            intent.putExtra("sets", workout.getSets());
            intent.putExtra("level", workout.getLevel());
            intent.putExtra("main_muscle", workout.getMainMuscle());
            intent.putParcelableArrayListExtra("exercises", workout.getExercises());

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
            viewHolder.start_button.setTag(workouts.get(position));

            viewHolder.img.setImageURI(Uri.parse(workouts.get(position).getWorkout_image()));

        }catch (NullPointerException e){
            Log.e(TAG,"NullPointerException",e.getCause());
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