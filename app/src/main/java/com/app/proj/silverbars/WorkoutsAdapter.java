package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static com.app.proj.silverbars.Utilities.getBitmapFromURL;
import static com.app.proj.silverbars.Utilities.loadExerciseImageFromDevice;

/**
 * Created by andre_000 on 4/12/2016.
 */


// ADAPTER FOR WORKING OUT ACTIVITY
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {

    private static final String TAG = "Workout Adapter";
    private List<WorkoutInfo> workouts;
    private Context context;

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {

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

        Bitmap bmp;

        bmp = getBitmapFromURL(WorkoutActivity.ParsedExercises[position].getExercise_image());
        
        //Log.v(TAG, Arrays.toString(imageDir));
        
        if (bmp != null){
            viewHolder.imagen.setImageBitmap(bmp);
            viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
            viewHolder.Layout.getLayoutParams().width = containerDimensions(context);

        }else {

            String[] imageDir = WorkoutActivity.ParsedExercises[position].getExercise_image().split("exercises");
            if (imageDir.length < 2){
                //Log.v(TAG, "ha entrado en IF imageDir.length < 2");
                bmp = loadExerciseImageFromDevice(context,WorkoutActivity.ParsedExercises[position].getExercise_image());
                viewHolder.imagen.setImageBitmap(bmp);
                viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
                viewHolder.Layout.getLayoutParams().width = containerDimensions(context);
                viewHolder.nombre.setText(workouts.get(position).getNombre());

            }else{

                String Parsedurl = "exercises"+imageDir[1];
                String[] imagesName = Parsedurl.split("/");
                String imgName = imagesName[2];
                bmp = loadExerciseImageFromDevice(context,imgName);

                if (bmp != null){
                    //Log.v(TAG, "ha entrado en if bmp != null");
                    viewHolder.imagen.setImageBitmap(bmp);
                    viewHolder.imagen.getLayoutParams().width = containerDimensions(context);
                    viewHolder.Layout.getLayoutParams().width = containerDimensions(context);
                    viewHolder.nombre.setText(workouts.get(position).getNombre());
                }
            }

        }
            viewHolder.nombre.setText(workouts.get(position).getNombre());

//        viewHolder.next.setText("Visitas:"+String.valueOf(workouts.get(position).getVisitas()));
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