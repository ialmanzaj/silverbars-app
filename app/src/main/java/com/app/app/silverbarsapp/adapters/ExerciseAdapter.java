package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private static final String TAG = ExerciseAdapter.class.getSimpleName();

    private Context context;
    private Utilities utilities = new Utilities();
    private ArrayList<ExerciseRep> exercises;

    public ExerciseAdapter(Context context,ArrayList<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public ArrayList<ExerciseRep> getExercises() {
        return exercises;
    }


    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name) TextView mExerciseName;

        @BindView(R.id.imagen) SimpleDraweeView mExerciseImgCache;
        @BindView(R.id.imagen_local) ImageView mExerciseImglocal;

        @BindView(R.id.exercise_number) TextView mExerciseNumber;
        @BindView(R.id.exercise_option) TextView mExerciseOption;

        @BindView(R.id.weight) TextView mExerciseWeight;
        @BindView(R.id.weight_layout)LinearLayout mExerciseLayoutWeight;

        public ExerciseViewHolder(View view) {
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
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ExerciseViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercises_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder,int position) {

        viewHolder.mExerciseName.setText(exercises.get(position).getExercise().getExercise_name());

        //exercise option ( rep or second )
        if(utilities.checkIfRep(exercises.get(position))){

            viewHolder.mExerciseNumber.setText(String.valueOf(exercises.get(position).getRepetition()));
            viewHolder.mExerciseOption.setText(context.getString(R.string.reps_text));

        }else {

            viewHolder.mExerciseNumber.setText(String.valueOf(exercises.get(position).getSeconds()));
            viewHolder.mExerciseOption.setText(context.getString(R.string.exercise_adapter_option));
        }

        //exercise weight
        if (exercises.get(position).getWeight() > 0){
            viewHolder.mExerciseLayoutWeight.setVisibility(View.VISIBLE);
            viewHolder.mExerciseWeight.setText(utilities.formaterDecimal(String.valueOf(exercises.get(position).getWeight())));
        }


        try {

            String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");

            if (imageDir.length == 2){
                //Log.d(TAG,"img from json");

                viewHolder.mExerciseImgCache.setVisibility(View.VISIBLE);
                viewHolder.mExerciseImgCache.setImageURI(Uri.parse(exercises.get(position).getExercise().getExercise_image()));

            }else {
                //Log.d(TAG,"img from local");

                viewHolder.mExerciseImglocal.setVisibility(View.VISIBLE);
                viewHolder.mExerciseImglocal.setImageBitmap(utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image()));
            }


        }catch (NullPointerException e){Log.e(TAG,""+e);}
    }



}