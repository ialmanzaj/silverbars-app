package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.Exercise;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.AllExercisesViewHolder> {

    private static final String TAG = ExercisesAdapter.class.getSimpleName();


    private Context mContext;

    private List<Exercise> mExercises = new ArrayList<>();

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();

    public ExercisesAdapter(Context context, List<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }


    public class AllExercisesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nombre) TextView nombre;
        @BindView(R.id.unchecked) ImageView unchecked;
        @BindView(R.id.checked) ImageView checked;
        @BindView(R.id.imagen) SimpleDraweeView imagen;

        public AllExercisesViewHolder(View view) {
            super(view);

            //binding views
            ButterKnife.bind(this,view);
        }

        public void setListener(){
           /* itemView.setOnClickListener(view -> {

                if (selectedItems.get((Integer) view.getTag(), false)) {
                    selectedItems.delete((Integer) view.getTag());

                    checked.setVisibility(View.GONE);
                    unchecked.setVisibility(View.VISIBLE);

                } else {

                    selectedItems.put((Integer) view.getTag(), true);
                    unchecked.setVisibility(View.GONE);
                    checked.setVisibility(View.VISIBLE);
                }

            });*/
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