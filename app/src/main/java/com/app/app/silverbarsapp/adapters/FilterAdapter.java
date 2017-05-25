package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 05/25/17.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private static final String TAG = ExerciseDetailAdapter.class.getSimpleName();

    private ArrayList<String> muscles;

    private onMuscleSelected listener;

    public FilterAdapter() {
        this.muscles = new ArrayList<>();
        init();
    }

    public void addListener(onMuscleSelected listener){
        this.listener = listener;
    }

    private void init(){
        muscles.add("All muscles");
    }

    public FilterAdapter(ArrayList<String> muscles) {
        this.muscles = muscles;
    }

    public void add(String item) {
        muscles.add(item);
        notifyDataSetChanged();
    }
    public void delete(int position) {
        muscles.remove(position);
        notifyItemRangeRemoved(position,getItemCount());
    }

    public void set(ArrayList<String> muscles) {
        this.muscles.addAll(muscles);
        notifyDataSetChanged();
    }


    public ArrayList<String> getMuscles() {
        return muscles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.muscle) TextView muscle;
        @BindView(R.id.delete) ImageView delete;

        public ViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.delete)
        public void deleteMuscle(View view){
            int position = (int) view.getTag();
            listener.onDeleteMuscle(position);
        }
    }

    @Override
    public int getItemCount() {
        return muscles.size();
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new FilterAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(FilterAdapter.ViewHolder viewHolder, int position) {
        viewHolder.muscle.setText(muscles.get(position));
        viewHolder.delete.setTag(position);
    }

    public interface onMuscleSelected{
        void onDeleteMuscle(int position);
    }

}