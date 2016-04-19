package com.example.project.calisthenic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andre_000 on 4/12/2016.
 */
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {
    private List<Workouts_info> items;

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView next;

        public WorkoutsViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
            next = (TextView) v.findViewById(R.id.next);
        }
    }

    public WorkoutsAdapter(List<Workouts_info> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public WorkoutsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workouts_card, viewGroup, false);
        return new WorkoutsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WorkoutsViewHolder viewHolder, int i) {
        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.next.setText("Visitas:"+String.valueOf(items.get(i).getVisitas()));
    }
}