package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.ExerciseRep;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class CreateFinalWorkoutExercisesAdapter extends RecyclerView.Adapter<CreateFinalWorkoutExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = CreateFinalWorkoutExercisesAdapter.class.getSimpleName();

    private ArrayList<ExerciseRep> mExercises = new ArrayList<>();
    private Context mContext;

    public CreateFinalWorkoutExercisesAdapter(Context context, ArrayList<ExerciseRep> exerciseReps) {
        mContext = context;
        mExercises = exerciseReps;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nombre) TextView nombre;
        @BindView(R.id.repetitions) TextView repetitions;
        @BindView(R.id.imagen) SimpleDraweeView imagen;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_create_final, parent, false);
        return new ExerciseViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        try {

            //asignar nombre y repeticiones a cada elemento del recycler.
            viewHolder.nombre.setText(mExercises.get(position).getExercise().getExercise_name());
            viewHolder.repetitions.setText(String.valueOf(mExercises.get(position).getRepetition()));

            Uri uri = Uri.parse((mExercises.get(position).getExercise().getExercise_image()));
            viewHolder.imagen.setImageURI(uri);

        }catch (NullPointerException e){}
    }

}