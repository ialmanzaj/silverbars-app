package com.app.proj.silverbars;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class AllExercisesAdapter extends RecyclerView.Adapter<AllExercisesAdapter.AllExercisesViewHolder> {

    private static final String TAG = "AllExercisesAdapter";
    private Context mContext;
    private List<Exercise> mExercises = new ArrayList<>();

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();

    public AllExercisesAdapter(Context context,List<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }


    public class AllExercisesViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public TextView next;
        public ImageView unchecked, checked;

        SimpleDraweeView imagen;



        public AllExercisesViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            imagen = (SimpleDraweeView) itemView.findViewById(R.id.imagen);
            unchecked = (ImageView) itemView.findViewById(R.id.unchecked);
            checked = (ImageView) itemView.findViewById(R.id.checked);
        }

        public void setListener(){


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (selectedItems.get((Integer) view.getTag(), false)) {
                        selectedItems.delete((Integer) view.getTag());

                        checked.setVisibility(View.GONE);
                        unchecked.setVisibility(View.VISIBLE);

                    } else {

                        selectedItems.put((Integer) view.getTag(), true);
                        unchecked.setVisibility(View.GONE);
                        checked.setVisibility(View.VISIBLE);
                    }

                }

            });
        }
    }


    @Override
    public void onBindViewHolder(AllExercisesViewHolder viewHolder,int position) {

        viewHolder.nombre.setText(mExercises.get(position).getExercise_name());

        Uri uri = Uri.parse(mExercises.get(position).getExercise_image());
        viewHolder.imagen.setImageURI(uri);

        viewHolder.itemView.setTag(position);

        viewHolder.setListener();

    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public AllExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_exercises_list, viewGroup, false);
        return new AllExercisesViewHolder(v);
    }


}