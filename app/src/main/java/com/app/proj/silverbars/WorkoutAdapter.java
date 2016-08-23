package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.like.LikeButton;


public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.VH> {

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private static final String TAG = "WORKOUT ADAPTER";
    //    private static final String[] WORKOUTS = {"Chest", "Upper Body", "Core", "Back Destruction", "Chest"};
//    private static final int[] IMG = {R.mipmap.imagen1, R.mipmap.imagen2, R.mipmap.imagen3, R.mipmap.imagen4, R.mipmap.imagen5};
    private MainFragment outerClass = new MainFragment();
    private JsonWorkout[] workouts = outerClass.Workouts;

    private final Activity context;
    public static class VH extends RecyclerView.ViewHolder {

        FrameLayout layout;
        //ImageView img;
        TextView text;
        Button btn;
        LikeButton like;
        SimpleDraweeView img;

        public VH(View itemview) {
            super(itemview);

            img = (SimpleDraweeView) itemview.findViewById(R.id.img);
            layout = (CardView) itemview.findViewById(R.id.layout);
            text = (TextView)  itemview.findViewById(R.id.text);
            btn  = (Button)    itemview.findViewById(R.id.btn);

            //like = (LikeButton) v.findViewById(R.id.like);
            //like.setAnimationScaleFactor(2);
//            like.setIconSizePx(64);
            //like.setExplodingDotColorsRes(R.color.colorPrimaryText,R.color.bookmark);
        }
    }

    public WorkoutAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_WORKOUT:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
                return new VH(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int i) {

        final int position = viewHolder.getAdapterPosition();
        int height = containerDimensions(context);
        viewHolder.layout.getLayoutParams().height = height / 3;
        
        
        switch (viewHolder.getItemViewType()) {
            case TYPE_WORKOUT:

                viewHolder.text.setText(workouts[position].getWorkout_name());

                Uri uri = Uri.parse(workouts[position].getWorkout_image());
                viewHolder.img.setImageURI(uri);


                String[] parts = workouts[position].getWorkout_image().split("workouts");
                String Parsedurl = "workouts"+parts[1];
                String[] imagesName = Parsedurl.split("/");
                final String imgName = imagesName[2];


/*

                Bitmap bmp = loadWorkoutImageFromDevice(context,imgName);

                if (bmp != null){
                    viewHolder.img.setImageBitmap(bmp);
                } else{
                    DownloadImage(Parsedurl,viewHolder,imgName);
                }
*/


                viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, WorkoutActivity.class);
                        i.putExtra("id",workouts[position].getId());
                        i.putExtra("name",workouts[position].getWorkout_name());
                        i.putExtra("image",imgName);
                        i.putExtra("sets",workouts[position].getSets());
                        i.putExtra("level",workouts[position].getLevel());
                        i.putExtra("muscle",workouts[position].getMain_muscle());
                        i.putExtra("exercises",workouts[position].getExercises());
                        context.startActivity(i);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return workouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position < getItemCount() ? TYPE_WORKOUT : TYPE_VIEW_MORE;
    }



    public static int containerDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

}