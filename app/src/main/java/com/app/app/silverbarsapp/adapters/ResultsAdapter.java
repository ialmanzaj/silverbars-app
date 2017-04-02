package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 03/28/17.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ExerciseViewHolder> {

    private Context context;
    private ArrayList<ExerciseRep> exercises;

    public ResultsAdapter(Context context, ArrayList<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name) TextView exercise_name;
        @BindView(R.id.total) TextView total;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return  new ExerciseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercise_result_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {
        viewHolder.exercise_name.setText(exercises.get(position).getExercise().getExercise_name());
        viewHolder.total.setText("25 reps");
    }


    @Override
    public int getItemCount() {
        return exercises.size();
    }



}
