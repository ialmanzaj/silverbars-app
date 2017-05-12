package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 04/17/17.
 */

public class WorkoutsDoneAdapter extends RecyclerView.Adapter<WorkoutsDoneAdapter.ViewHolder> {

    private static final String TAG = WorkoutsDoneAdapter.class.getSimpleName();

    private List<WorkoutDone> items;

    public WorkoutsDoneAdapter() {
        this.items = new ArrayList<>();
    }

    public WorkoutsDoneAdapter(List<WorkoutDone> items) {
        this.items = items;
    }

    public void add(WorkoutDone item){
        items.add(0,item);
        notifyItemInserted(0);
    }

    public void set(List<WorkoutDone> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name) TextView name;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.total_time) TextView total_time;
        @BindView(R.id.sets) TextView sets;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public WorkoutsDoneAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WorkoutsDoneAdapter.ViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workouts_done_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(WorkoutsDoneAdapter.ViewHolder viewHolder, int position) {
        viewHolder.name.setText(items.get(position).getMy_workout().getWorkout_name());
        viewHolder.date.setText(items.get(position).getDate());
        viewHolder.total_time.setText(items.get(position).getTotal_time());
        viewHolder.sets.setText(String.valueOf(items.get(position).getSets_completed()));
    }



}