package com.app.app.silverbarsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.Skill;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 04/14/17.
 */

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {

    private static final String TAG = ExerciseDetailAdapter.class.getSimpleName();

    private List<Skill> items;

    public SkillAdapter() {
        this.items = new ArrayList<>();
    }

    public SkillAdapter(List<Skill> items) {
        this.items = items;
    }

    public void add(Skill item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void set(List<Skill> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.type) TextView mName;
        @BindView(R.id.progress_bar) ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public SkillAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SkillAdapter.ViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.skill_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(SkillAdapter.ViewHolder viewHolder, int position) {
        viewHolder.mName.setText(items.get(position).getType());
        viewHolder.progressBar.setProgress(items.get(position).getProgress());
    }

}

