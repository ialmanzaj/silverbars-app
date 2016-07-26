package com.app.project.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by andre_000 on 7/8/2016.
 */
public class AllExercisesAdapter extends RecyclerView.Adapter<AllExercisesAdapter.AllExercisesViewHolder> {

    private Context mContext;
    public static exerciseList exerciseList = new exerciseList();
    public static boolean[] Selected = new boolean[getCount()];

    public boolean[] getSelected() {
        return Selected;
    }
    public static ArrayList<Integer> order = new ArrayList<>();

    public static class AllExercisesViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public ImageView unchecked, checked;
        public TextView order;

        public AllExercisesViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
//            next = (TextView) v.findViewById(R.id.next);
            unchecked = (ImageView) v.findViewById(R.id.unchecked);
            checked = (ImageView) v.findViewById(R.id.checked);
            order = (TextView) v.findViewById(R.id.order);
        }
    }

    public AllExercisesAdapter(Context context) {
        mContext = context;

    }

    public void Reset(){
        for(int x = 0; x < getCount(); x++){
            Selected[x] = false;
        }
    }

    public static int getCount() {
        return exerciseList.Exercises.length;
    }

    @Override
    public int getItemCount() {
        return exerciseList.Exercises.length;
    }

    @Override
    public AllExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_exercises_list, viewGroup, false);
        Reset();
        return new AllExercisesViewHolder(v);
    }

    public static ArrayList<Integer> getOrder() {
        return order;
    }

    public int fillOrder(int elementId){
        int position = 0;
        for (int i = 0; i < getItemCount(); i++){
            try {
                Log.v("Position / element",String.valueOf(i)+" / "+String.valueOf(order.get(i)));
                order.get(i);
                if (order.get(i) == 0){
                    order.remove(i);
                    order.add( i, elementId );
                    position = i+1;
                    Log.v("Elements", order.toString());
                    break;
                }
            } catch ( IndexOutOfBoundsException e ) {
                order.add( i, elementId );
                position = i+1;
                Log.v("Elements", order.toString());
                break;
            }
        }
        return position;
    }
    public void unfillOrder(int elementId){
        Log.v("Order Size",String.valueOf(getItemCount()));
        for (int i = 0; i < order.size(); i++){
            if (order.get(i)==elementId){
                Log.v("Order actual position",String.valueOf(i));
                order.remove(i);
                order.add(i,0);
                Log.v("Elements", order.toString());
                break;
            }
        }
    }

    @Override
    public void onBindViewHolder(final AllExercisesViewHolder viewHolder, int i) {
        final int a = i;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Selected[a]){
                    Selected[a] = true;
                    Log.v("Seleccionado",String.valueOf(Selected[a]));
                    viewHolder.unchecked.setVisibility(View.GONE);
                    viewHolder.checked.setVisibility(View.VISIBLE);
                    viewHolder.order.setText(String.valueOf(fillOrder(exerciseList.Exercises[a].getId())));
                }else{
                    Selected[a] = false;
                    Log.v("Seleccionado",String.valueOf(Selected[a]));
                    viewHolder.checked.setVisibility(View.GONE);
                    viewHolder.unchecked.setVisibility(View.VISIBLE);
                    viewHolder.order.setText("");
                    unfillOrder(exerciseList.Exercises[a].getId());
                }
            }
        });
        //Setting values to each recylerView Element
        Log.v("Exercise",exerciseList.Exercises[a].getExercise_name());
        String[] imageDir = exerciseList.Exercises[a].getExercise_image().split("exercises");
        ;
        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        String imgName = imagesName[2];
        Bitmap bmp = loadImageFromCache(imgName);
        if (bmp != null) {
            viewHolder.imagen.setImageBitmap(bmp);
        } else {
            DownloadImage(Parsedurl, viewHolder, imgName);
        }
        viewHolder.nombre.setText(exerciseList.Exercises[a].getExercise_name());

    }

    public void DownloadImage(final String url, final AllExercisesViewHolder vh, final String imgName) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-ap-northeast-1.amazonaws.com/silverbarsmedias3/")
                .build();
        final SilverbarsService downloadService = retrofit.create(SilverbarsService.class);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<ResponseBody> call = downloadService.downloadFile(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Bitmap bitmap = null;
                        if (response.isSuccessful()) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),imgName);
                            if(writtenToDisk){bitmap = loadImageFromCache(imgName);}
                            vh.imagen.setImageBitmap(bitmap);
                        }
                        else {
                            Log.d("Download", "server contact failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v("Failure", t.toString());
                    }
                });
                return null;
            };
        }.execute();
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
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/"+imgName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
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
        } catch (IOException e) {return false;}
    }
}