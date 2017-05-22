package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseProgressionCompared;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.app.silverbarsapp.Constants.BETTER;
import static com.app.app.silverbarsapp.Constants.EQUAL;
import static com.app.app.silverbarsapp.Constants.WORST;


/**
 * Created by isaacalmanza on 04/10/17.
 */

public class ExerciseDetailAdapter extends RecyclerView.Adapter<ExerciseDetailAdapter.ProgressionViewHolder> {

    private static final String TAG = ExerciseDetailAdapter.class.getSimpleName();

    private ArrayList<ExerciseProgressionCompared> progressions;
    private Utilities utilities = new Utilities();
    

    public ExerciseDetailAdapter(ArrayList<ExerciseProgressionCompared> progressions) {
        this.progressions = progressions;
    }

    public class ProgressionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name) TextView exercise_name;
        @BindView(R.id.total) TextView total;

        @BindView(R.id.progress) TextView progress;
        @BindView(R.id.type_progress) TextView type_progress;

        @BindView(R.id.weight) TextView weight;

        @BindView(R.id.date) TextView date;

        public ProgressionViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public int getItemCount() {
        return progressions.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (progressions.get(position).isPositive()){
            return BETTER;
        }else if (progressions.get(position).isEqual()){
            return EQUAL;
        }else {
            return WORST;
        }
    }

    @Override
    public ExerciseDetailAdapter.ProgressionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case BETTER:
                return new ProgressionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_progresion_detail_positive, viewGroup, false));
            case EQUAL:
                return new ProgressionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_progression_detail_equal, viewGroup, false));
            case WORST:
                return new ProgressionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_progression_detail_negative, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ProgressionViewHolder viewHolder, int position) {
        viewHolder.exercise_name.setText(progressions.get(position).getExerciseProgression_newer().getExercise().getExercise_name());
        viewHolder.date.setText(progressions.get(position).getPeriod_in_beetween());
        viewHolder.progress.setText(utilities.formaterDecimal(String.valueOf(progressions.get(position).getProgress())));

        if (progressions.get(position).getExerciseProgression_newer().getTotal_weight() > 0) {
            viewHolder.weight.setText(" (+"+
                    utilities.formaterDecimal(String.valueOf(progressions.get(position).getExerciseProgression_newer().getTotal_weight()))
                    +"kg)");
        }

        if (progressions.get(position).getExerciseProgression_newer().getTotal_repetition() > 0) {
            viewHolder.total.setText(String.valueOf(progressions.get(position).getExerciseProgression_newer().getRepetitions_done() + " reps"));

        }else {
            viewHolder.total.setText(String.valueOf(progressions.get(position).getExerciseProgression_newer().getSeconds_done() + " secs"));
        }


        if (progressions.get(position).isWeightImprove()){
            viewHolder.type_progress.setText("weight");
            return;
        }

        if (progressions.get(position).isRepImprove()){viewHolder.type_progress.setText("reps");}
        else {viewHolder.type_progress.setText("seconds");}
    }



}