package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

    private Context context;
    private List<Workout> workouts;
    private Utilities  utilities = new Utilities();

    public WorkoutsAdapter(Context context) {
        this.context = context;
        this.workouts = new ArrayList<>();
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

    public class WorkoutHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout) FrameLayout layout;

        @BindView(R.id.workout_name) TextView mWorkoutName;
        @BindView(R.id.start_button)Button mStartButton;
        @BindView(R.id.workout_img)SimpleDraweeView mWorkoutImgCache;
        @BindView(R.id.webview) WebView webView;

        public WorkoutHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.start_button)
        public void startWorkoutButton(View view){
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
        return new WorkoutHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WorkoutHolder viewHolder, int position) {
        viewHolder.layout.getLayoutParams().height = utilities.calculateContainerHeight(context) / 3;


        //String frameVideo = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/pNNMr5glICM\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        //viewHolder.webView.getSettings().setJavaScriptEnabled(true);
        //viewHolder.webView.loadData(frameVideo, "text/html", "utf-8");


        try {

            viewHolder.mWorkoutName.setText(workouts.get(position).getWorkout_name());
            viewHolder.mStartButton.setTag(workouts.get(position));
            //viewHolder.mWorkoutImgCache.setImageURI(Uri.parse(workouts.get(position).getWorkout_image()));

        }catch (NullPointerException e){Log.e(TAG,"NullPointerException",e.getCause());}

    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

}