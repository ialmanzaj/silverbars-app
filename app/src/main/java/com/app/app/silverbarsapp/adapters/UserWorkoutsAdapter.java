package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.activities.WorkoutActivity;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private Utilities utilities = new Utilities();

    private OnWorkoutListener listener;

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

    public interface OnWorkoutListener {
        void deleteWorkout(int workout_id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout) CardView layout;
        @BindView(R.id.img_local)ImageView workout_img;
        @BindView(R.id.text)TextView workout_name;
        @BindView(R.id.start_button)Button start_button;
        @BindView(R.id.delete) ImageView delete_button;

        @BindView(R.id.initial) TextView initial_workout_name;

        private int workout_id;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }


        @OnClick(R.id.delete)
        public void deleteButton(View view){

            workout_id = (int) view.getTag();
            Log.d(TAG,"delete"+workout_id);

            new MaterialDialog.Builder( context )
                    .title("Delete workout").titleColor(context.getResources().getColor(R.color.black))
                    .content("Desea eliminar este workout").contentColor(context.getResources().getColor(R.color.black))
                    .positiveText("Yes")
                    .onPositive((dialog, which) -> {

                        onDelete(workout_id);

                    })
                    .negativeText("No")
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    }).show();
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
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userworkout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder,  int position) {

        viewholder.layout.getLayoutParams().height = utilities.calculateContainerHeight(context) / 3;

        viewholder.workout_name.setText(workouts.get(position).getWorkout_name());
        viewholder.start_button.setTag(workouts.get(position));
        viewholder.delete_button.setTag(workouts.get(position).getId());

        try {


            if (Objects.equals(workouts.get(position).getWorkout_image(), "/")){

                viewholder.layout.setBackgroundColor(context.getResources().getColor(R.color.secondColor));
                viewholder.initial_workout_name.setText(workouts.get(position).getWorkout_name().substring(0,1));

            }else {
                String[] workoutImgDir = workouts.get(position).getWorkout_image().split(context.getFilesDir().getPath()+"/SilverbarsImg/");
                if (workoutImgDir.length == 2){
                    String workoutImgName = workoutImgDir[1];
                    Bitmap imgBitmap;
                    imgBitmap = utilities.loadWorkoutImageFromDevice(context,workoutImgName);
                    viewholder.workout_img.setImageBitmap(imgBitmap);
                }
            }



        }catch (NullPointerException e){
            Log.e(TAG,"NullPointerException");
        }

    }


    private void onDelete(int workout_id){
        Log.d(TAG,"workout_id: "+workout_id);
        try {

            workouts.remove(utilities.getWorkoutById(workouts,workout_id));
            notifyDataSetChanged();

            listener.deleteWorkout(workout_id);

        }catch (NullPointerException e){Log.e(TAG,"",e);}
    }

    @Override
    public int getItemCount() {
        return workouts.size();

    }




}