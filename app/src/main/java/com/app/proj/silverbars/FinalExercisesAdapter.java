package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.AdaptersUtilities.loadImageFromCache;
import static com.app.proj.silverbars.AdaptersUtilities.writeResponseBodyToDisk;

public class FinalExercisesAdapter extends RecyclerView.Adapter<FinalExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = "FinalExercisesAdapter";
    private List<JsonExercise> mExercises = new ArrayList<>();
    private InputStream bmpInput;

    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private TextView Positive, Negative, Isometric;

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public TextView repetitions;
        public TextView positive;
        public TextView negative;
        public TextView isometric;

        public ExerciseViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
            repetitions = (TextView) v.findViewById(R.id.repetitions);

            positive = (TextView) v.findViewById(R.id.positive);
            isometric = (TextView) v.findViewById(R.id.isometric);
            negative = (TextView) v.findViewById(R.id.negative);

        }

    }

    public FinalExercisesAdapter(Context context,List<JsonExercise> mExercises) {
        Context mContext = context;
        this.mExercises = mExercises;

    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }


    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_exercise_cards, parent, false);
        return new ExerciseViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        Bitmap bmp = null;

        final int a = viewHolder.getAdapterPosition();

        //Setting values to each recylerView Element
        String[] imageDir = mExercises.get(a).getExercise_image().split("exercises");


        Log.v("Image Array", Arrays.toString(imageDir));

        if (imageDir.length < 2){

            bmp = loadImageFromCache(mExercises.get(a).getExercise_image());
            viewHolder.imagen.setImageBitmap(bmp);

        }else{
            String Parsedurl = "exercises"+imageDir[1];
            String[] imagesName = Parsedurl.split("/");
            String imgName = imagesName[2];
            bmp = loadImageFromCache(imgName);
            if (bmp != null){
                viewHolder.imagen.setImageBitmap(bmp);
            }
            else{
                DownloadImage(Parsedurl,viewHolder,imgName);
            }
        }

        viewHolder.nombre.setText(mExercises.get(a).getExercise_name());

        //viewHolder.repetitions.setText(String.valueOf(mExercises.get(a)));
    }





    private void DownloadImage(final String url, final ExerciseViewHolder vh, final String imgName) {
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
                            Log.e(TAG, "DownloadImage, Download failed :(");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG,"DownloadImage, onFailure :(",t);
                    }
                });
                return null;
            };
        }.execute();
    }



}