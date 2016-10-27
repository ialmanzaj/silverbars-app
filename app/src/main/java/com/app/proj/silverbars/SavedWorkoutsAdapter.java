package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import static com.app.proj.silverbars.Utilities.loadWorkoutImageFromDevice;


public class SavedWorkoutsAdapter extends RecyclerView.Adapter<SavedWorkoutsAdapter.VH> {

    private static final String TAG = "SavedWorkoutsAdapter";


    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private List<Workout> workouts;
    private final Activity context;
    private Boolean user_workout  = false;

    public static class VH extends RecyclerView.ViewHolder {
        FrameLayout layout;
        ImageView img;
        TextView text;
        Button btn;
        LikeButton like;

        public VH(View v) {
            super(v);

            layout = (CardView) v.findViewById(R.id.layout);
            img  = (ImageView) v.findViewById(R.id.img_local);
            text = (TextView)  v.findViewById(R.id.text);
            btn  = (Button)    v.findViewById(R.id.btn);
            //like = (LikeButton) v.findViewById(R.id.like);
            //like.setAnimationScaleFactor(2);
//            like.setIconSizePx(64);

            //like.setExplodingDotColorsRes(R.color.colorPrimaryText,R.color.bookmark);
        }
    }


    public SavedWorkoutsAdapter(Activity context, List<Workout> workouts, Boolean user_workout) {
        this.context = context;
        this.workouts = workouts;
        this.user_workout = user_workout;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case TYPE_WORKOUT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
                return new VH(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(VH viewholder,  int i) {

        final int position = viewholder.getAdapterPosition();

        int height = containerDimensions(context);
        viewholder.layout.getLayoutParams().height = height / 3;

        switch (viewholder.getItemViewType()) {
            case TYPE_WORKOUT:

                viewholder.img.setVisibility(View.VISIBLE);

                String[] workoutImgDir = workouts.get(position).getWorkout_image().split(context.getFilesDir().getPath()+"/SilverbarsImg/");


                if (workoutImgDir.length == 2){
                    String workoutImgName = workoutImgDir[1];
                    Log.v(TAG,"Image Name: "+workoutImgName);
                    Bitmap imgBitmap;
                    imgBitmap = loadWorkoutImageFromDevice(context,workoutImgName);
                    viewholder.img.setImageBitmap(imgBitmap);
                }


                viewholder.text.setText(workouts.get(position).getWorkout_name());
                viewholder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ArrayList<ExerciseRep> exercises = new ArrayList<>();
                        Collections.addAll(exercises,workouts.get(position).getExercises());

                        Intent i = new Intent(context, WorkoutActivity.class);
                        i.putExtra("id", workouts.get(position).getWorkoutId());
                        i.putExtra("name", workouts.get(position).getWorkout_name());
                        i.putExtra("image",workouts.get(position).getWorkout_image());
                        i.putExtra("sets", workouts.get(position).getSets());
                        i.putExtra("level", workouts.get(position).getLevel());
                        i.putExtra("main_muscle", workouts.get(position).getMainMuscle());
                        i.putParcelableArrayListExtra("exercises", exercises);
                        i.putExtra("user_workout",user_workout);
                        context.startActivity(i);
                    }
                });
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

    private static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }




}