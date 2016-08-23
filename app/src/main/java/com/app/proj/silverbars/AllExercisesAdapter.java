package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.loadExerciseImageFromDevice;
import static com.app.proj.silverbars.Utilities.saveExerciseImageInDevice;


public class AllExercisesAdapter extends RecyclerView.Adapter<AllExercisesAdapter.AllExercisesViewHolder> {

    private static final String TAG = "AllExercisesAdapter";
    private Context mContext;
    private List<JsonExercise> mExercises = new ArrayList<>();

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();

    public static class AllExercisesViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public ImageView unchecked, checked;


        public AllExercisesViewHolder(View itemView) {
            super(itemView);
            imagen = (ImageView) itemView.findViewById(R.id.imagen);
            nombre = (TextView) itemView.findViewById(R.id.nombre);

            unchecked = (ImageView) itemView.findViewById(R.id.unchecked);
            checked = (ImageView) itemView.findViewById(R.id.checked);

        }
    }

    public AllExercisesAdapter(Context context,List<JsonExercise> exercises) {
        mContext = context;
        mExercises = exercises;

    }



    @Override
    public int getItemCount() {
        return mExercises.size();
    }


    @Override
    public AllExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_exercises_list, viewGroup, false);
        return new AllExercisesViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final AllExercisesViewHolder viewHolder,int i) {

        final int a = viewHolder.getAdapterPosition();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedItems.get(a, false)) {
                    selectedItems.delete(a);

                    viewHolder.checked.setVisibility(View.GONE);
                    viewHolder.unchecked.setVisibility(View.VISIBLE);
                }
                else {

                    selectedItems.put(a, true);
                    viewHolder.unchecked.setVisibility(View.GONE);
                    viewHolder.checked.setVisibility(View.VISIBLE);
                }
                //for (int item = 0;item < selectedItems.size();item++) Log.v("lista selected", String.valueOf((item)));
            }
        });

        Bitmap bmp;

        String[] imageDir = mExercises.get(a).getExercise_image().split("exercises");

       if (imageDir.length < 2){
            bmp = loadExerciseImageFromDevice(mContext,mExercises.get(a).getExercise_image());
            viewHolder.imagen.setImageBitmap(bmp);

        }else {
            String Parsedurl = "exercises" + imageDir[1];
            String[] imagesName = Parsedurl.split("/");
            String imgName = imagesName[2];
            bmp = loadExerciseImageFromDevice(mContext,imgName);

            if (bmp != null) {
                viewHolder.imagen.setImageBitmap(bmp);
            } else {
                DownloadImage(Parsedurl, viewHolder, imgName);
            }
        }

        viewHolder.nombre.setText(mExercises.get(a).getExercise_name());
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
                            boolean writtenToDisk = saveExerciseImageInDevice(mContext,response.body(),imgName);
                            if(writtenToDisk){bitmap = loadExerciseImageFromDevice(mContext,imgName);}
                            vh.imagen.setImageBitmap(bitmap);
                        }
                        else {
                            Log.v(TAG, "Download server contact failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG,"Failure",t);
                    }
                });
                return null;
            };
        }.execute();
    }


}