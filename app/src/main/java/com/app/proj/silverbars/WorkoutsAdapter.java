package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by andre_000 on 4/12/2016.
 */


// ADAPTER FOR WORKING OUT ACTIVITY
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {

    private static final String TAG = "Workout Adapter";
    private List<WorkoutInfo> workouts;
    private Context context;

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {

        //public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public RelativeLayout Layout;

        SimpleDraweeView imagen;

        public WorkoutsViewHolder(View v) {
            super(v);
            imagen = (SimpleDraweeView) v.findViewById(R.id.imagen);
            //imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
            Layout = (RelativeLayout) v.findViewById(R.id.Layout);
//            next = (TextView) v.findViewById(R.id.next);
        }
    }

    public WorkoutsAdapter(List<WorkoutInfo> workouts, Context context) {
        this.workouts = workouts;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return workouts.size();
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

        viewHolder.nombre.setText(workouts.get(position).getNombre());

        Uri uri = Uri.parse(WorkoutActivity.ParsedExercises[position].getExercise_image());

        viewHolder.imagen.setImageURI(uri);
        viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
        viewHolder.Layout.getLayoutParams().width = containerDimensions(context);

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