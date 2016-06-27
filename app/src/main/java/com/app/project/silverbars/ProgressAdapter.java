package com.app.project.silverbars;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {
    private List<ProgressInfo> items;
    private Context context;

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public TextView Muscle;
        public TextView Progress;

        public ProgressViewHolder(View v) {
            super(v);
            Muscle = (TextView) v.findViewById(R.id.Muscle);
            Progress = (TextView) v.findViewById(R.id.Progress);
    //            next = (TextView) v.findViewById(R.id.next);
        }
    }

        public ProgressAdapter(List<ProgressInfo> items, Context context) {
            this.items = items;
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public ProgressViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.progress_card, viewGroup, false);
            return new ProgressViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ProgressViewHolder viewHolder, int i) {
            viewHolder.Muscle.setText(items.get(i).getMuscle());
            viewHolder.Progress.setText(String.valueOf(items.get(i).getProgress()));
        }
}
