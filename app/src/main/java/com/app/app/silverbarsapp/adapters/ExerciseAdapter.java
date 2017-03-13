package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        @BindView(R.id.nombre) TextView exercise_name;
        @BindView(R.id.repetitions) TextView exercise_rep;
        @BindView(R.id.option) TextView option;
        @BindView(R.id.imagen) SimpleDraweeView exercise_img_cache;
        @BindView(R.id.imagen_local) ImageView exercise_img_local;

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
        return new ExerciseViewHolder( LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercises_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder,int position) {

        viewHolder.exercise_name.setText(exercises.get(position).getExercise().getExercise_name());

        if(utilities.checkIfRep(exercises.get(position))){

            viewHolder.exercise_rep.setText(String.valueOf(exercises.get(position).getRepetition()));

        }else {

            viewHolder.exercise_rep.setText(String.valueOf(exercises.get(position).getSeconds()));
            viewHolder.option.setText(context.getString(R.string.exercise_adapter_option));
        }


        try {

            String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");

            if (imageDir.length == 2){
                //Log.d(TAG,"img from json");

                viewHolder.exercise_img_cache.setVisibility(View.VISIBLE);
                viewHolder.exercise_img_cache.setImageURI(
                        Uri.parse(exercises.get(position).getExercise().getExercise_image()));

            }else {
                //Log.d(TAG,"img from local");

                viewHolder.exercise_img_local.setVisibility(View.VISIBLE);
                viewHolder.exercise_img_local.setImageBitmap(utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image()));
            }


        }catch (NullPointerException e){Log.e(TAG,""+e);}
    }



}