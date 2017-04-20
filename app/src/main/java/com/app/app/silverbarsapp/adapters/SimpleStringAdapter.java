package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 04/11/17.
 */

public class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.ViewHolder> {

    private static final String TAG = ExerciseDetailAdapter.class.getSimpleName();

    private List<String> items;

    public SimpleStringAdapter() {
        this.items = new ArrayList<>();
    }

    public SimpleStringAdapter(List<String> items) {
        this.items = items;
    }

    public void add(String item){
        items.add(0,item);
        notifyItemInserted(0);
    }

    public void set(List<String> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name) TextView name;

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
    public SimpleStringAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SimpleStringAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.simple_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(SimpleStringAdapter.ViewHolder viewHolder, int position) {
        viewHolder.name.setText(items.get(position));
    }



}