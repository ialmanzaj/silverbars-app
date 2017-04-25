package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.MuscleProgression;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 03/22/17.
 */

public class ProgressionAdapter extends RecyclerView.Adapter<ProgressionAdapter.ViewHolder>  {

    private Context context;
    private ArrayList<MuscleProgression> progressions;

    public ProgressionAdapter(Context context) {
        this.context = context;
        this.progressions = new ArrayList<>();
    }

    public void set(ArrayList<MuscleProgression> progressions){
        this.progressions.addAll(progressions);
        notifyDataSetChanged();
    }
    public void add(MuscleProgression progression){
        progressions.add(progression);
        notifyDataSetChanged();
    }

    public void clear(){
        progressions.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.muscle_name)TextView mMuscleName;
        @BindView(R.id.level)TextView mMuscleLevel;
        @BindView(R.id.progress)TextView mMuscleProgres;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public ProgressionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProgressionAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progression_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProgressionAdapter.ViewHolder viewholder, int position) {
        viewholder.mMuscleName.setText(progressions.get(position).getMuscle().getMuscle_name());
        viewholder.mMuscleLevel.setText(String.valueOf(progressions.get(position).getLevel()));
        viewholder.mMuscleProgres.setText(String.valueOf(progressions.get(position).getMuscle_activation_progress()));
    }

    @Override
    public int getItemCount() {
        return progressions.size();
    }

}
