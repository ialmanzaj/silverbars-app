package com.app.proj.silverbars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.like.LikeButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.VH> {

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private static final String TAG = "WORKOUT ADAPTER";
    Context context;
    List<Workout> workouts;

    public static class VH extends RecyclerView.ViewHolder {

        FrameLayout layout;
        TextView text;
        Button btn;
        LikeButton like;
        SimpleDraweeView img;

        public VH(View itemview) {
            super(itemview);

            img = (SimpleDraweeView) itemview.findViewById(R.id.img);
            layout = (CardView) itemview.findViewById(R.id.layout);
            text = (TextView)  itemview.findViewById(R.id.text);
            btn  = (Button)    itemview.findViewById(R.id.btn);
        }
    }

    public WorkoutAdapter(Context context, List<Workout> workouts) {
        this.context = context;
        this.workouts = workouts;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_WORKOUT:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
                return new VH(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int i) {

        final int position = viewHolder.getAdapterPosition();

        int height = containerDimensions(context);
        viewHolder.layout.getLayoutParams().height = height / 3;


        switch (viewHolder.getItemViewType()) {
            case TYPE_WORKOUT:

                viewHolder.text.setText(workouts.get(position).getWorkout_name());
                Uri uri = Uri.parse(workouts.get(position).getWorkout_image());
                viewHolder.img.setImageURI(uri);

                viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<ExerciseRep> exerciseRepList = new ArrayList<>();
                        Collections.addAll(exerciseRepList, workouts.get(position).getExercises());

                        Intent i = new Intent(context, WorkoutActivity.class);
                        i.putExtra("workout_id", workouts.get(position).getWorkoutId());
                        i.putExtra("name", workouts.get(position).getWorkout_name());
                        i.putExtra("image", workouts.get(position).getWorkout_image());
                        i.putExtra("sets", workouts.get(position).getSets());
                        i.putExtra("level", workouts.get(position).getLevel());
                        i.putExtra("main_muscle", workouts.get(position).getMainMuscle());
                        i.putParcelableArrayListExtra("exercises",  exerciseRepList);
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

    public static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

}