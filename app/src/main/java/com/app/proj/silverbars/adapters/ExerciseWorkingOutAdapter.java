package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.models.ExerciseRep;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;

public class ExerciseWorkingOutAdapter extends RecyclerView.Adapter<ExerciseWorkingOutAdapter.WorkoutsViewHolder> {

    private static final String TAG = ExerciseWorkingOutAdapter.class.getSimpleName();


    private List<ExerciseRep> exercises;
    private Context context;
    private Utilities utilities;


    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {

        @BindView() TextView nombre;
        @BindView()RelativeLayout Layout;
        @BindView()SimpleDraweeView imagen_cache;
        @BindView()ImageView imageView_local;

        public WorkoutsViewHolder(View v) {
            super(v);
            imagen_cache = (SimpleDraweeView) v.findViewById(R.id.imagen_cache);
            imageView_local = (ImageView) v.findViewById(R.id.imagen_local);
            nombre = (TextView) v.findViewById(R.id.nombre);
            Layout = (RelativeLayout) v.findViewById(R.id.Layout);

        }
    }

    public ExerciseWorkingOutAdapter(Context context, List<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
        utilities = new Utilities();
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
        viewHolder.Layout.getLayoutParams().width = containerDimensions(context);


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

    private int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }




}