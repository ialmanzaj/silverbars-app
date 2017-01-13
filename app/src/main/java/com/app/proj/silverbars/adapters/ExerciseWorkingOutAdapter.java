package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.ExerciseRep;
import com.app.proj.silverbars.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseWorkingOutAdapter extends RecyclerView.Adapter<ExerciseWorkingOutAdapter.WorkoutsViewHolder> {

    private static final String TAG = ExerciseWorkingOutAdapter.class.getSimpleName();


    private List<ExerciseRep> exercises;
    private Context context;
    private Utilities utilities;

    public ExerciseWorkingOutAdapter(Context context, List<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
        utilities = new Utilities();
    }

    public class WorkoutsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nombre) TextView nombre;
        @BindView(R.id.Layout)RelativeLayout Layout;
        @BindView(R.id.imagen_cache)SimpleDraweeView imagen_cache;
        @BindView(R.id.imagen_local)ImageView imageView_local;

        public WorkoutsViewHolder(View view) {
            super(view);

            //binding views
            ButterKnife.bind(this,view);

        }
    }



    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public WorkoutsViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workouts_card, viewGroup, false);
        return new WorkoutsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WorkoutsViewHolder viewHolder, int a) {

        int position = viewHolder.getAdapterPosition();

        viewHolder.nombre.setText(exercises.get(position).getExercise().getExercise_name());
        viewHolder.Layout.getLayoutParams().width = utilities.containerDimensionsWidth(context);


        String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");

        if (imageDir.length == 2){
            Uri uri = Uri.parse(exercises.get(position).getExercise().getExercise_image());
            viewHolder.imagen_cache.setImageURI(uri);

        }else {
            viewHolder.imageView_local.setVisibility(View.VISIBLE);
            Bitmap bitmap = utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image());
            viewHolder.imageView_local.setImageBitmap(bitmap);
        }


    }





}