package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
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

import static com.app.app.silverbarsapp.Constants.BETTER;
import static com.app.app.silverbarsapp.Constants.EQUAL;
import static com.app.app.silverbarsapp.Constants.WORST;

/**
 * Created by isaacalmanza on 03/28/17.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {
    private static final String TAG = ResultsAdapter.class.getSimpleName();


    private Context context;
    private ArrayList<ExerciseProgression> exercises;
    private Utilities utilities = new Utilities();

    public ResultsAdapter(Context context, ArrayList<ExerciseProgression> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name) TextView mExerciseName;
        @BindView(R.id.total) TextView total;
        @BindView(R.id.weight) TextView mWeight;
        @Nullable @BindView(R.id.type_progress) TextView mTypeProgress;
        @BindView(R.id.sets) TextView mSetsCompleted;
        @BindView(R.id.total_time) TextView mTotalTime;

        @BindView(R.id.progress) TextView mProgression;

        public ResultViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
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
    public ResultViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case WORST:
                return new ResultViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_result_item_negative, viewGroup, false));
            case EQUAL:
                return new ResultViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_result_item_equal, viewGroup, false));
            case BETTER:
                return new ResultViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_result_item_positive, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ResultViewHolder viewHolder, int position) {
        viewHolder.mExerciseName.setText(exercises.get(position).getExercise().getExercise_name());
        viewHolder.mProgression.setText(utilities.formaterDecimal(String.valueOf(exercises.get(position).getProgress())));
        viewHolder.mSetsCompleted.setText(String.valueOf(exercises.get(position).getSets_completed()));
        //viewHolder.mTotalTime.setText(exercises.get(position).getTotal_time());

        if (exercises.get(position).getTotal_weight() > 0) {
            viewHolder.mWeight.setText(
                    " (+"+
                    utilities.formaterDecimal(String.valueOf(exercises.get(position).getTotal_weight()))
                    +"kg" +")");
        }

        if (exercises.get(position).getTotal_repetition() > 0) {
            viewHolder.total.setText(String.valueOf(exercises.get(position).getRepetitions_done() + " reps"));
        }else {
            viewHolder.total.setText(String.valueOf(exercises.get(position).getSeconds_done() + " secs"));
        }


      /*  if (exercises.get(position).isWeightImprove()){
            viewHolder.mTypeProgress.setText("weight");
            return;
        }

        if (exercises.get(position).isRepImprove()){viewHolder.mTypeProgress.setText("reps");}
        else {viewHolder.mTypeProgress.setText("seconds");}*/

    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }

}
