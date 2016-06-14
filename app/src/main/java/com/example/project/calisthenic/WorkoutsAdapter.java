package com.example.project.calisthenic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by andre_000 on 4/12/2016.
 */
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {
    private List<WorkoutInfo> items;
    private Context context;

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public RelativeLayout Layout;

        public WorkoutsViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
            Layout = (RelativeLayout) v.findViewById(R.id.Layout);
//            next = (TextView) v.findViewById(R.id.next);
        }
    }

    public WorkoutsAdapter(List<WorkoutInfo> items, Context context) {
        this.items = items;
        this.context = context;
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
        WorkoutActivity workout = new WorkoutActivity();
        Bitmap bmp = null;
        //Setting values to each recylerView Element
        URL url = null;
        try {
            url = new URL(workout.ParsedExercises[i].exercise_image);
            Log.v("Url",url.toString());
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewHolder.imagen.setImageBitmap(bmp);
        viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
        viewHolder.Layout.getLayoutParams().width = containerDimensions(context);
        viewHolder.nombre.setText(items.get(i).getNombre());
//        viewHolder.next.setText("Visitas:"+String.valueOf(items.get(i).getVisitas()));
    }

    public static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }
}