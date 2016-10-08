package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FinalExercisesAdapter extends RecyclerView.Adapter<FinalExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = "FinalExercisesAdapter";
    private List<Exercise> mExercises = new ArrayList<>();
    private int[] exercise_reps;
    private InputStream bmpInput;

    private Button plusPositive,plusNegative,minusPositive,plusIsometric,minusIsometric,minusNegative;
    private TextView Positive, Negative, Isometric;

    Context mContext;

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_handle;
        public TextView nombre;
        public TextView next;
        public TextView repetitions;

        SimpleDraweeView imagen;

        public ExerciseViewHolder(View v) {
            super(v);
            //imagen = (ImageView) itemView.findViewById(R.id.imagen);
            nombre = (TextView) itemView.findViewById(R.id.nombre);

            imagen = (SimpleDraweeView) v.findViewById(R.id.imagen);

            repetitions = (TextView) itemView.findViewById(R.id.repetitions);
            img_handle = (ImageView) itemView.findViewById(R.id.handle);
            img_handle.setVisibility(View.GONE);
        }

    }

    public FinalExercisesAdapter(Context context, List<Exercise> mExercises, int[] exercise_reps) {
        mContext = context;
        this.mExercises = mExercises;
        this.exercise_reps = exercise_reps;

    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }


    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercises, parent, false);
        return new ExerciseViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        final int a = viewHolder.getAdapterPosition();

        //asignar nombre y repeticiones a cada elemento del recycler.
        viewHolder.nombre.setText(mExercises.get(a).getExercise_name());
        viewHolder.repetitions.setText(String.valueOf(exercise_reps[a]));




        Uri uri = Uri.parse((mExercises.get(a).getExercise_image()));
        viewHolder.imagen.setImageURI(uri);



        Bitmap bmp;

       /* //Setting values to each recylerView Element
        String[] imageDir = mExercises.get(a).getExercise_image().split("exercises");


        Log.v("Image Array", Arrays.toString(imageDir));

        if (imageDir.length < 2){

            bmp = loadExerciseImageFromDevice(mContext,mExercises.get(a).getExercise_image());
            viewHolder.imagen.setImageBitmap(bmp);

        }else{
            String Parsedurl = "exercises"+imageDir[1];
            String[] imagesName = Parsedurl.split("/");
            String imgName = imagesName[2];
            bmp = loadExerciseImageFromDevice(mContext,imgName);

            if (bmp != null){
                viewHolder.imagen.setImageBitmap(bmp);
            }
            else{
                DownloadImage(Parsedurl,viewHolder,imgName);
            }
        }*/


    }




/*

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
                            boolean writtenToDisk = saveExerciseImageInDevice(mContext,response.body(),imgName);
                            if(writtenToDisk){bitmap = loadExerciseImageFromDevice(mContext,imgName);}
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
*/



}