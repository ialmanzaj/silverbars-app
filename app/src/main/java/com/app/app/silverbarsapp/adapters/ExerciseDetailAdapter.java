package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 04/10/17.
 */

public class ExerciseDetailAdapter extends RecyclerView.Adapter<ExerciseDetailAdapter.ExerciseViewHolder> {

    private static final String TAG = ExerciseDetailAdapter.class.getSimpleName();

    private  static final int BETTER = 2;
    private  static final int EQUAL = 1;
    private  static final int WORST = 0;

    private Context context;
    private ArrayList<ExerciseProgression> exercises;
    private Utilities utilities = new Utilities();

    public ExerciseDetailAdapter(Context context,ArrayList<ExerciseProgression> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name) TextView exercise_name;
        @BindView(R.id.total) TextView total;

        @BindView(R.id.progress) TextView progress;
        @BindView(R.id.type_progress) TextView type_progress;

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
    public int getItemViewType(int position) {
        if (exercises.get(position).isPositive()){
            return BETTER;
        }else if (exercises.get(position).isEqual()){
            return EQUAL;
        }else {
            return WORST;
        }
    }

    @Override
    public ExerciseDetailAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case 0:
                return new ExerciseDetailAdapter.ExerciseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_detail_item_negative, viewGroup, false));
            case 1:
                return new ExerciseDetailAdapter.ExerciseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_detail_item_equal, viewGroup, false));
            case 2:
                return new ExerciseDetailAdapter.ExerciseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_detail_item_positive, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ExerciseDetailAdapter.ExerciseViewHolder viewHolder, int position) {
        viewHolder.exercise_name.setText(exercises.get(position).getExercise().getExercise_name());

        if (exercises.get(position).getTotal_repetition() > 0) {
            viewHolder.total.setText(String.valueOf(exercises.get(position).getRepetitions_done() + " reps"));
            viewHolder.progress.setText(utilities.formaterDecimal(String.valueOf(exercises.get(position).getProgress()))+ "%");
            viewHolder.type_progress.setText("more reps than week before");

        }else {
            viewHolder.total.setText(String.valueOf(exercises.get(position).getSeconds_done() + " secs"));
            viewHolder.progress.setText("25%");
            viewHolder.type_progress.setText("more reps than week before");
        }



    }



}