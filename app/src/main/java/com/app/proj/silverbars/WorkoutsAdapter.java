package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import static com.app.proj.silverbars.AdaptersUtilities.loadImageFromCache;

/**
 * Created by andre_000 on 4/12/2016.
 */
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {
    private static final String TAG = "Workout Adapter";
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
        Bitmap bmp = null;
        //Setting values to each recylerView Element
        String[] imageDir = WorkoutActivity.ParsedExercises[i].getExercise_image().split("exercises");

        Log.v(TAG, Arrays.toString(imageDir));

        if (imageDir.length < 2){
            //Log.v(TAG, "ha entrado en IF imageDir.length < 2");
            bmp = loadImageFromCache(WorkoutActivity.ParsedExercises[i].getExercise_image());
            viewHolder.imagen.setImageBitmap(bmp);
            viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
            viewHolder.Layout.getLayoutParams().width = containerDimensions(context);
            viewHolder.nombre.setText(items.get(i).getNombre());

        }else{

            String Parsedurl = "exercises"+imageDir[1];
            String[] imagesName = Parsedurl.split("/");
            String imgName = imagesName[2];
            bmp = loadImageFromCache(imgName);

            if (bmp != null){
                //Log.v(TAG, "ha entrado en if bmp != null");
                viewHolder.imagen.setImageBitmap(bmp);
                viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
                viewHolder.Layout.getLayoutParams().width = containerDimensions(context);
                viewHolder.nombre.setText(items.get(i).getNombre());
            }
        }

//        viewHolder.next.setText("Visitas:"+String.valueOf(items.get(i).getVisitas()));
    }

    private static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }




}