package com.app.proj.silverbars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.loadWorkoutImageFromDevice;
import static com.app.proj.silverbars.Utilities.saveWorkoutImgInDevice;


public class savedWorkoutAdapter extends RecyclerView.Adapter<savedWorkoutAdapter.VH> {

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private static final String TAG = "savedWorkoutAdapter";
    private List<JsonWorkout> workouts;
    private final Activity context;
    private Boolean user_workout  = false;

    public static class VH extends RecyclerView.ViewHolder {
        FrameLayout layout;
        ImageView img;
        TextView text;
        Button btn;
        LikeButton like;

        public VH(View v) {
            super(v);

            layout = (CardView) v.findViewById(R.id.layout);
            img  = (ImageView) v.findViewById(R.id.img_local);
            text = (TextView)  v.findViewById(R.id.text);
            btn  = (Button)    v.findViewById(R.id.btn);
            //like = (LikeButton) v.findViewById(R.id.like);
            //like.setAnimationScaleFactor(2);
//            like.setIconSizePx(64);

            //like.setExplodingDotColorsRes(R.color.colorPrimaryText,R.color.bookmark);
        }
    }


    public savedWorkoutAdapter(Activity context, List<JsonWorkout> workouts,Boolean user_workout) {
        this.context = context;
        this.workouts = workouts;
        this.user_workout = user_workout;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case TYPE_WORKOUT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
                return new VH(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(VH viewholder,  int i) {

        final int position = viewholder.getAdapterPosition();

        int height = containerDimensions(context);
        viewholder.layout.getLayoutParams().height = height / 3;

        switch (viewholder.getItemViewType()) {
            case TYPE_WORKOUT:

                Log.v(TAG,"Image: "+ workouts.get(position).getWorkout_image());
                viewholder.img.setVisibility(View.VISIBLE);

                String[] workoutImgDir = workouts.get(position).getWorkout_image().split(context.getFilesDir().getPath());
                Bitmap imgBitmap;
                String workoutImgName = getWorkoutImageName(workoutImgDir);
                Log.v(TAG,"Image Name: "+workoutImgName);

                imgBitmap = loadWorkoutImageFromDevice(context,workoutImgName);
                viewholder.img.setImageBitmap(imgBitmap);


                viewholder.text.setText(workouts.get(position).getWorkout_name());
                viewholder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, WorkoutActivity.class);
                        i.putExtra("id", workouts.get(position).getId());
                        i.putExtra("name", workouts.get(position).getWorkout_name());
                        i.putExtra("image",workouts.get(position).getWorkout_image());
                        i.putExtra("sets", workouts.get(position).getSets());
                        i.putExtra("level", workouts.get(position).getLevel());
                        i.putExtra("muscle", workouts.get(position).getMain_muscle());
                        i.putExtra("exercises", workouts.get(position).getExercises());
                        i.putExtra("user_workout",user_workout);
                        context.startActivity(i);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    private String getWorkoutImageName(String[] workoutImgDir){
        String Parsedurl = "workouts"+workoutImgDir[1];
        String[] imagesName = Parsedurl.split("/");
        return imagesName[2];
    }

    @Override
    public int getItemViewType(int position) {
        return position < getItemCount() ? TYPE_WORKOUT : TYPE_VIEW_MORE;
    }

    private void DownloadImage(String url, final VH viewholder, final String workoutImgName){

        Log.v(TAG,"DownloadImage");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = null;
                    if (response.isSuccessful()) {

                        boolean writtenToDisk = saveWorkoutImgInDevice(context,response.body(),workoutImgName);

                        if(writtenToDisk){
                            bitmap = loadWorkoutImageFromDevice(context,workoutImgName);
                        }

                        viewholder.img.setImageBitmap(bitmap);

                    } else {
                        Log.v(TAG, "Download server contact failed");
                    }



                } else {
                    Log.v(TAG, "State: server contact failed");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,"onFAILURE",t);
            }
        });
    }

    
    
    private static int containerDimensions(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }




}