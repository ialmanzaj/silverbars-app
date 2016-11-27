package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class SavedWorkoutsAdapter extends RecyclerView.Adapter<SavedWorkoutsAdapter.ViewHolder> {

    private static final String TAG = "SavedWorkoutsAdapter";


    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private List<Workout> workouts;
    private Activity activity;
    private Boolean user_workout  = false;

    private Utilities utilities;

    public  class ViewHolder extends RecyclerView.ViewHolder {

        FrameLayout layout;
        ImageView img;
        TextView text;
        Button btn;
        LikeButton like;
        int mPosition;

        public ViewHolder(View v) {
            super(v);

            layout = (CardView) v.findViewById(R.id.layout);
            img  = (ImageView) v.findViewById(R.id.img_local);
            text = (TextView)  v.findViewById(R.id.text);
            btn  = (Button)    v.findViewById(R.id.btn);
        }


        public void setOnClicklistener(int position){

            mPosition = position;

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<ExerciseRep> exercises = new ArrayList<>();
                    Collections.addAll(exercises,workouts.get(mPosition).getExercises());

                    Intent i = new Intent(activity, WorkoutActivity.class);
                    i.putExtra("id", workouts.get(mPosition).getWorkoutId());
                    i.putExtra("name", workouts.get(mPosition).getWorkout_name());
                    i.putExtra("image",workouts.get(mPosition).getWorkout_image());
                    i.putExtra("sets", workouts.get(mPosition).getSets());
                    i.putExtra("level", workouts.get(mPosition).getLevel());
                    i.putExtra("main_muscle", workouts.get(mPosition).getMainMuscle());
                    i.putParcelableArrayListExtra("exercises", exercises);
                    i.putExtra("user_workout",user_workout);
                    activity.startActivity(i);


                }
            });


        }
    }


    public SavedWorkoutsAdapter(Activity activity, List<Workout> workouts, Boolean user_workout) {
        this.activity = activity;
        this.workouts = workouts;
        this.user_workout = user_workout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder,  int position) {

        int height = containerDimensions(activity);
        viewholder.layout.getLayoutParams().height = height / 3;

        utilities = new Utilities();

        String[] workoutImgDir = workouts.get(position).getWorkout_image().split(activity.getFilesDir().getPath()+"/SilverbarsImg/");

        if (workoutImgDir.length == 2){
            String workoutImgName = workoutImgDir[1];
            Bitmap imgBitmap;
            imgBitmap = utilities.loadWorkoutImageFromDevice(activity,workoutImgName);
            viewholder.img.setImageBitmap(imgBitmap);
        }


        viewholder.text.setText(workouts.get(position).getWorkout_name());
        viewholder.setOnClicklistener(position);
    }



    @Override
    public int getItemCount() {
        return workouts.size();
    }


    private static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }




}