package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import static com.app.app.silverbarsapp.Constants.DAILY;
import static com.app.app.silverbarsapp.Constants.EQUAL;
import static com.app.app.silverbarsapp.Constants.MONTH;
import static com.app.app.silverbarsapp.Constants.WEEK;
import static com.app.app.silverbarsapp.Constants.WORST;

/**
 * Created by isaacalmanza on 04/25/17.
 */

public class ExerciseDetailMonthlyAdapter extends RecyclerView.Adapter<ExerciseDetailMonthlyAdapter.ProgressionViewHolder> {

    private static final String TAG = ExerciseDetailMonthlyAdapter.class.getSimpleName();

    private ArrayList<ExerciseProgression> exercises;
    private Utilities utilities = new Utilities();
    private int type_date;

    public ExerciseDetailMonthlyAdapter(ArrayList<ExerciseProgression> exercises,int type_date) {
        this.exercises = exercises;
        this.type_date = type_date;
    }

    public class ProgressionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name)
        TextView exercise_name;
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
    public ExerciseDetailMonthlyAdapter.ProgressionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case BETTER:
                return new ExerciseDetailMonthlyAdapter.ProgressionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_detail_item_positive, viewGroup, false));
            case EQUAL:
                return new ExerciseDetailMonthlyAdapter.ProgressionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_detail_item_equal, viewGroup, false));
            case WORST:
                return new ExerciseDetailMonthlyAdapter.ProgressionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.exercise_detail_item_negative, viewGroup, false));
            default:
                return null;
        }
    }

    private String getDate(){
        switch (type_date){
            case DAILY: return "day";
            case WEEK: return "week";
            case MONTH: return "month";
            default:
                return "";
        }
    }

    @Override
    public void onBindViewHolder(ExerciseDetailMonthlyAdapter.ProgressionViewHolder viewHolder, int position) {
        viewHolder.exercise_name.setText(exercises.get(position).getExercise().getExercise_name());
        viewHolder.date.setText(getDate());
        viewHolder.progress.setText(utilities.formaterDecimal(String.valueOf(exercises.get(position).getProgress())));

        if (exercises.get(position).getTotal_weight() > 0) {
            viewHolder.weight.setText(" (+"+
                    utilities.formaterDecimal(String.valueOf(exercises.get(position).getTotal_weight()))
                    +"kg)");
        }

        Log.d(TAG,"getTotal_repetition "+exercises.get(position).getTotal_repetition() );

        if (exercises.get(position).getTotal_repetition() > 0) {
            viewHolder.total.setText(String.valueOf(exercises.get(position).getRepetitions_done() + " reps"));

        }else {
            viewHolder.total.setText(String.valueOf(exercises.get(position).getSeconds_done() + " secs"));
        }

        if (exercises.get(position).isWeightImprove()){
            viewHolder.type_progress.setText("weight");
            return;
        }

        if (exercises.get(position).isRepImprove()){viewHolder.type_progress.setText("reps");}else {viewHolder.type_progress.setText("seconds");}
    }


}