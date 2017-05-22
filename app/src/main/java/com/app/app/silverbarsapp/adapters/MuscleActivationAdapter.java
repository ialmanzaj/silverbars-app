package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.MuscleActivationCompared;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.app.silverbarsapp.Constants.BETTER;
import static com.app.app.silverbarsapp.Constants.EQUAL;
import static com.app.app.silverbarsapp.Constants.WORST;

/**
 * Created by isaacalmanza on 04/23/17.
 */
public class MuscleActivationAdapter extends RecyclerView.Adapter<MuscleActivationAdapter.ViewHolder> {

    private static final String TAG = MuscleActivationAdapter.class.getSimpleName();

    private Utilities utilities = new Utilities();
    private ArrayList<MuscleActivationCompared> muscles_activations;


    public MuscleActivationAdapter() {
        muscles_activations = new ArrayList<>();
    }

    public void add(MuscleActivationCompared muscleActivation) {
        muscles_activations.add(muscleActivation);   
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.muscle_activation) ProgressBar muscle_activation;
        @BindView(R.id.porcentaje) TextView porcentaje;


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
    public MuscleActivationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case BETTER:
                return new MuscleActivationAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.muscle_activation_positive, viewGroup, false));
            case EQUAL:
                return new MuscleActivationAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.muscle_activation_equal, viewGroup, false));
            case WORST:
                return new MuscleActivationAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.muscle_activation_negative, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(MuscleActivationAdapter.ViewHolder viewHolder, int position) {
        viewHolder.muscle_activation.setProgress(muscles_activations.get(position).getMuscle_activation_average());
        viewHolder.porcentaje.setText(utilities.formaterDecimal(String.valueOf(muscles_activations.get(position).getProgress())));
    }


}
