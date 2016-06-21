package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by roberto on 5/24/2016.
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.VH> {

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private static final String[] WORKOUTS = {"Chest", "Upper Body", "Core", "Back Destruction", "Chest"};
    private static final int[] IMG = {R.mipmap.imagen1, R.mipmap.imagen2, R.mipmap.imagen3, R.mipmap.imagen4, R.mipmap.imagen5};
    private MainFragment outerClass = new MainFragment();
    private JsonWorkout[] workouts = outerClass.Workouts;

    private final FragmentActivity context;

    public static class VH extends RecyclerView.ViewHolder {

        FrameLayout layout;
        ImageView img;
        TextView text;
        Button btn;
        LikeButton like;

        public VH(View v) {
            super(v);

            layout = (CardView) v.findViewById(R.id.layout);
            img  = (ImageView) v.findViewById(R.id.img);
            text = (TextView)  v.findViewById(R.id.text);
            btn  = (Button)    v.findViewById(R.id.btn);
            like = (LikeButton) v.findViewById(R.id.like);
            like.setAnimationScaleFactor(2);
//            like.setIconSizePx(64);

            like.setExplodingDotColorsRes(R.color.colorPrimaryText,R.color.bookmark);
        }
    }

    public WorkoutAdapter(FragmentActivity context) {
//        Log.v("Workouts:", String.valueOf(workouts));
        this.context = context;
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
    public void onBindViewHolder(VH vh, final int position) {
        int height = containerDimensions(context);
        Log.v("Height",String.valueOf(height));
        vh.layout.getLayoutParams().height = height / 3;
        switch (vh.getItemViewType()) {
            case TYPE_WORKOUT:
                String[] parts = workouts[position].getWorkout_image().split("workouts");;
                String Parsedurl = "workouts"+parts[1];
                DownloadImage(Parsedurl,vh);
//                vh.img.setImageBitmap(bmp);
                vh.text.setText(workouts[position].getWorkout_name());
                vh.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("Workout id",String.valueOf(workouts[position].getId()));
                        Intent i = new Intent(context, WorkoutActivity.class);
                        i.putExtra("id",workouts[position].getId());
                        i.putExtra("name",workouts[position].getWorkout_name());
                        i.putExtra("sets",workouts[position].getSets());
                        i.putExtra("exercises",workouts[position].getExercises());
                        Log.v("Exercises",Arrays.toString(workouts[position].exercises));
                        context.startActivity(i);
                    }
                });

                break;
            /*
            case TYPE_VIEW_MORE:
                vh.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;
                */
        }

    }

    @Override
    public int getItemCount() {
        return workouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position < workouts.length ? TYPE_WORKOUT : TYPE_VIEW_MORE;
    }

    public void DownloadImage(String url, final VH vh){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadImage(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("State", "server contacted and has file");
                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                    vh.img.setImageBitmap(bmp);
                } else {
                    Log.d("State", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
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