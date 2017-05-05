package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseWorkingOutAdapter extends RecyclerView.Adapter<ExerciseWorkingOutAdapter.WorkoutsViewHolder> {

    private static final String TAG = ExerciseWorkingOutAdapter.class.getSimpleName();

    private List<ExerciseRep> exercises;
    private Context context;
    private Utilities utilities = new Utilities();

    public ExerciseWorkingOutAdapter(Context context, List<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public class WorkoutsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.Layout) RelativeLayout layout;

        @BindView(R.id.nombre) TextView mExerciseName;
        @BindView(R.id.imagen_cache)SimpleDraweeView mExerciseImgCache;
        @BindView(R.id.imagen_local)ImageView mExerciseImglocal;

        public WorkoutsViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public WorkoutsViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new WorkoutsViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_working_out, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(WorkoutsViewHolder viewHolder, int position) {
        viewHolder.layout.getLayoutParams().width = utilities.calculateContainerWidth(context);

        viewHolder.mExerciseName.setText(exercises.get(position).getExercise().getExercise_name());


        try {

            String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");


            if (imageDir.length == 2){
                viewHolder.mExerciseImgCache.setImageURI(Uri.parse(exercises.get(position).getExercise().getExercise_image()));

            }else {
                viewHolder.mExerciseImglocal.setVisibility(View.VISIBLE);
                viewHolder.mExerciseImglocal.setImageBitmap(utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image()));
            }


        }catch (NullPointerException e){
            Log.e(TAG,"NullPointerException");
        }


    }



}