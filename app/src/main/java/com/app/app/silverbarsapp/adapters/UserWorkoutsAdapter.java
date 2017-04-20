package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.app.silverbarsapp.Filter;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.activities.WorkoutActivity;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 03/11/17.
 */


public class UserWorkoutsAdapter extends RecyclerView.Adapter<UserWorkoutsAdapter.ViewHolder> {

    private static final String TAG = UserWorkoutsAdapter.class.getSimpleName();

    private List<Workout> workouts;
    private Context context;
    private OnWorkoutListener listener;

    private Utilities utilities = new Utilities();
    Filter filter = new Filter();

    public UserWorkoutsAdapter(Context context) {
        this.context = context;
        this.workouts = new ArrayList<>();
    }

    public void setWorkoutListener(OnWorkoutListener listener){
        this.listener = listener;
    }

    public void set(List<Workout> workouts){
        this.workouts.addAll(workouts);
        notifyDataSetChanged();
    }

    public void add(Workout workout){
        workouts.add(0,workout);
        notifyItemInserted(0);
    }

    private void delete(int workout_id){
        //remove from the adapter
        workouts.remove(filter.getWorkoutById(workouts,workout_id));
        notifyDataSetChanged();

        //notify listener of the activity and delete from the database
        listener.deleteWorkout(workout_id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout) CardView layout;
        @BindView(R.id.img_local)ImageView mWorkoutImg;
        @BindView(R.id.text)TextView mWorkoutName;
        @BindView(R.id.start_button)Button mStartButton;
        @BindView(R.id.delete) ImageView mDeleteButton;

        @BindView(R.id.initial) TextView initial_workout_name;

        private int workout_id;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.delete)
        void deleteButton(View view){
            workout_id = (int) view.getTag();

            new MaterialDialog.Builder( context )
                    .title("Delete workout").titleColor(context.getResources().getColor(R.color.black))
                    .content("Desea eliminar este workout").contentColor(context.getResources().getColor(R.color.black))
                    .positiveText("Yes")
                    .onPositive((dialog, which) -> {

                        delete(workout_id);

                    })
                    .negativeText("No")
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        }


        @OnClick(R.id.start_button)
        void startButton(View view){
            Workout workout = (Workout) view.getTag();

            Intent intent = new Intent(context, WorkoutActivity.class);
            intent.putExtra("workout_id", workout.getId());
            intent.putExtra("name", workout.getWorkout_name());
            intent.putExtra("image", workout.getWorkout_image());
            intent.putExtra("sets", workout.getSets());
            intent.putExtra("level", workout.getLevel());
            intent.putExtra("main_muscle", workout.getMainMuscle());
            intent.putParcelableArrayListExtra("exercises",  workout.getExercises());
            intent.putExtra("user_workout",true);

            context.startActivity(intent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userworkout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder,  int position) {

        viewholder.mWorkoutName.setText(workouts.get(position).getWorkout_name());

        viewholder.layout.getLayoutParams().height = utilities.calculateContainerHeight(context) / 3;

        viewholder.mStartButton.setTag(workouts.get(position));
        viewholder.mDeleteButton.setTag(workouts.get(position).getId());



                /*String[] workoutImgDir = workouts.get(position).getWorkout_image().split(context.getFilesDir().getPath()+"/SilverbarsImg/");

                if (workoutImgDir.length == 2){
                    String workoutImgName = workoutImgDir[1];
                    Bitmap imgBitmap;
                    imgBitmap = utilities.loadWorkoutImageFromDevice(context,workoutImgName);
                    viewholder.mWorkoutImg.setImageBitmap(imgBitmap);
                }*/

    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }


    public interface OnWorkoutListener {
        void deleteWorkout(int workout_id);
    }

}