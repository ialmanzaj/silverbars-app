package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FinalExercisesAdapter extends RecyclerView.Adapter<FinalExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = "FinalExercisesAdapter";
    private List<ExerciseRep> mExercises = new ArrayList<>();
    private int[] exercise_reps;
    private InputStream bmpInput;

    private Button plusPositive,plusNegative,minusPositive,plusIsometric,minusIsometric,minusNegative;
    private TextView Positive, Negative, Isometric;

    Context mContext;

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_handle;
        public TextView nombre;
        public TextView next;
        public TextView repetitions;

        SimpleDraweeView imagen;

        public ExerciseViewHolder(View v) {
            super(v);
            //imagen = (ImageView) itemView.findViewById(R.id.imagen);
            nombre = (TextView) itemView.findViewById(R.id.nombre);

            imagen = (SimpleDraweeView) v.findViewById(R.id.imagen);

            repetitions = (TextView) itemView.findViewById(R.id.repetitions);

            img_handle = (ImageView) itemView.findViewById(R.id.handle);
            img_handle.setVisibility(View.GONE);
        }

    }

    public FinalExercisesAdapter(Context context, List<ExerciseRep> mExercises, int[] exercise_reps) {
        mContext = context;
        this.mExercises = mExercises;
        this.exercise_reps = exercise_reps;

    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }


    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercises, parent, false);
        return new ExerciseViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        final int a = viewHolder.getAdapterPosition();

        //asignar nombre y repeticiones a cada elemento del recycler.
        viewHolder.nombre.setText(mExercises.get(a).getExercise().getExercise_name());
        viewHolder.repetitions.setText(String.valueOf(exercise_reps[a]));


        Uri uri = Uri.parse((mExercises.get(a).getExercise().getExercise_image()));
        viewHolder.imagen.setImageURI(uri);

    }



}