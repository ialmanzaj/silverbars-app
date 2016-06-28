package com.app.project.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
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

import java.io.File;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        String[] imageDir = workout.ParsedExercises[i].getExercise_image().split("exercises");;
        String Parsedurl = "exercises"+imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        String imgName = imagesName[2];
        bmp = loadImageFromCache(imgName);
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

    public void DownloadImage(final String url, final WorkoutsViewHolder vh, final String imgName) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        SilverbarsService downloadService = retrofit.create(SilverbarsService.class);
        Call<ResponseBody> call = downloadService.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                    vh.imagen.setImageBitmap(bmp);
                    Log.d("State", "server contacted and has file");
//                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(),imgName);
//                    Log.d("Download", "file download was a success? " + writtenToDisk);

                } else {
                    Log.d("State", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private Bitmap loadImageFromCache(String imageURI) {
        Bitmap bitmap = null;
        File file = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/"+imageURI);
        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }
}