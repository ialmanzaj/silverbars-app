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


    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nombre) TextView nombre;
        @BindView(R.id.repetitions) TextView repetitions;
        @BindView(R.id.option) TextView option;
        @BindView(R.id.imagen) SimpleDraweeView imagen_cache;
        @BindView(R.id.imagen_local) ImageView imageView_local;

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

    public ArrayList<ExerciseRep> getExercises() {
        return exercises;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ExerciseViewHolder( LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercises_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder,int position) {

        viewHolder.nombre.setText(exercises.get(position).getExercise().getExercise_name());

        //repetitions  or seconds
        if (exercises.get(position).getRepetition() > 0){
            viewHolder.repetitions.setText(String.valueOf(exercises.get(position).getRepetition()));
        }else {
            viewHolder.repetitions.setText(String.valueOf(exercises.get(position).getSeconds()));
            viewHolder.option.setText(context.getString(R.string.exercise_adapter_option));
        }

        //Log.d(TAG,"img "+exercises.get(a).getExercise().getExercise_image());

        try {


            String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");


            if (imageDir.length == 2){
                //Log.d(TAG,"img from json");

                viewHolder.imagen_cache.setVisibility(View.VISIBLE);
                viewHolder.imagen_cache.setImageURI(
                        Uri.parse(exercises.get(position).getExercise().getExercise_image()));

            }else {
                //Log.d(TAG,"img from local");

                viewHolder.imageView_local.setVisibility(View.VISIBLE);
                viewHolder.imageView_local.setImageBitmap(utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image()));
            }


        }catch (NullPointerException e){
            Log.e(TAG,""+e);
        }

    }





}