package com.example.project.calisthenic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        vh.layout.getLayoutParams().height = height / 3;
        switch (vh.getItemViewType()) {
            case TYPE_WORKOUT:
                String[] parts = workouts[position].getWorkout_image().split("workouts");;
                String Parsedurl = "workouts"+parts[1];
                Log.v("Image",Parsedurl);
//                DownloadImage(Parsedurl,vh);
                String[] imagesName = Parsedurl.split("/");
                String imgName = imagesName[2];
                Log.v("Image Name",imgName);
                Bitmap bmp = loadImageFromCache(imgName);
                if (bmp != null){
                    vh.img.setImageBitmap(bmp);
                }
                else{
                    DownloadImage(Parsedurl,vh,imgName);
                }
//                vh.img.setImageBitmap(bmp);
                vh.text.setText(workouts[position].getWorkout_name());
                vh.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, WorkoutActivity.class);
                        i.putExtra("id",workouts[position].getId());
                        i.putExtra("name",workouts[position].getWorkout_name());
                        i.putExtra("sets",workouts[position].getSets());
                        i.putExtra("exercises",workouts[position].getExercises());
                        i.putExtra("level",workouts[position].getLevel());
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
        return position < workouts.length ? TYPE_WORKOUT : TYPE_VIEW_MORE;
    }

    public void DownloadImage(String url, final VH vh, final String imgName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadImage(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
//                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
//                    vh.img.setImageBitmap(bmp);
                    Bitmap bitmap = null;
                    if (response.isSuccessful()) {
                        Log.v("Response","success");
                        boolean writtenToDisk = writeResponseBodyToDisk(response.body(),imgName);
                        if(writtenToDisk){bitmap = loadImageFromCache(imgName);}
                        vh.img.setImageBitmap(bitmap);
                    }
                    else {
                        Log.v("Download", "server contact failed");
                    }
                } else {
                    Log.v("State", "server contact failed");
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
    private Bitmap loadImageFromCache(String imageURI) {
        Bitmap bitmap = null;
        File file = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/"+imageURI);
        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }
    private boolean writeResponseBodyToDisk(ResponseBody body, String imgName) {
        try {
            File Folder = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/");
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/"+imgName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            boolean success = true;
            if (!Folder.exists()) {
                success = Folder.mkdir();
            }
            if (success) {
                try {
                    byte[] fileReader = new byte[4096];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {break;}
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.d("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    return true;

                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {inputStream.close();}
                    if (outputStream != null) {outputStream.close();}
                }
            } else {
                Log.v("Error","Error while creating dir");
            }

        } catch (IOException e) {return false;}
        return false;
    }
}