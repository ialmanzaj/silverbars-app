package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.MuscleActivation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.app.silverbarsapp.Constants.BETTER;
import static com.app.app.silverbarsapp.Constants.EQUAL;
import static com.app.app.silverbarsapp.Constants.WORST;

/**
 * Created by isaacalmanza on 04/28/17.
 */

public class ResultsMuscleActivation  extends RecyclerView.Adapter<ResultsMuscleActivation.ViewHolder> {

    private static final String TAG = ResultsMuscleActivation.class.getSimpleName();

    private ArrayList<MuscleActivation> muscles_activations;

    public ResultsMuscleActivation(ArrayList<MuscleActivation> muscleActivations) {
        muscles_activations = muscleActivations;
    }
    public void set(List<MuscleActivation> muscleActivations) {
        muscles_activations.addAll(muscleActivations);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.muscle_activation) ProgressBar muscle_activation;
        @BindView(R.id.name) TextView name;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        return muscles_activations.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (muscles_activations.get(position).isPositive()) {
            return BETTER;
        } else if (muscles_activations.get(position).isNegative()) {
            return WORST;
        } else {
            return EQUAL;
        }
    }

    @Override
    public ResultsMuscleActivation.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case BETTER:
                return new ResultsMuscleActivation.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.results_muscle_activation_positive, viewGroup, false));
            case EQUAL:
                return new ResultsMuscleActivation.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.results_muscle_activation_equal, viewGroup, false));
            case WORST:
                return new ResultsMuscleActivation.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.results_muscle_activation_negative, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ResultsMuscleActivation.ViewHolder viewHolder, int position) {
        viewHolder.name.setText(muscles_activations.get(position).getMuscle_name());
        viewHolder.muscle_activation.setProgress(muscles_activations.get(position).getMuscle_activation());
    }


}
