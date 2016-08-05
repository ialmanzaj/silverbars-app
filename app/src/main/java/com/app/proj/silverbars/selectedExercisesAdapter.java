package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class selectedExercisesAdapter extends RecyclerView.Adapter<selectedExercisesAdapter.selectedExercisesViewHolder>  implements ItemTouchHelperAdapter {

    private static final String TAG = "ExercisesAdapter";
    private List<JsonExercise> mSelectedExercises;


    public static class selectedExercisesViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public ImageView unchecked, checked;
        public TextView repetitions;

        public selectedExercisesViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
//            next = (TextView) v.findViewById(R.id.next);
            unchecked = (ImageView) v.findViewById(R.id.unchecked);
            checked = (ImageView) v.findViewById(R.id.checked);
            repetitions = (TextView) v.findViewById(R.id.repetitions);

        }
    }


    public selectedExercisesAdapter(Context context,List<JsonExercise> exercises) {
        Context mContext = context;
        mSelectedExercises = exercises;
    }


    @Override
    public int getItemCount() {
        return mSelectedExercises.size();
    }

    @Override
    public selectedExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.exercises, viewGroup, false);
        //Reset();
        return new selectedExercisesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final selectedExercisesViewHolder viewHolder, int i) {

        final int a = viewHolder.getAdapterPosition();


        //Setting values to each recylerView Element
        Log.v(TAG,"mSelectedExercises: "+mSelectedExercises.get(a).getExercise_name());


        String[] imageDir = mSelectedExercises.get(a).getExercise_image().split("exercises");


        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        String imgName = imagesName[2];
        Bitmap bmp = loadImageFromCache(imgName);
        if (bmp != null) {
            viewHolder.imagen.setImageBitmap(bmp);
        } else {
            DownloadImage(Parsedurl, viewHolder, imgName);
        }

        viewHolder.nombre.setText(mSelectedExercises.get(a).getExercise_name());
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            private TextView DialogName, Reps;
            private Button plusRep, minusRep;
            private int value = 0;
            private int ActualRepValue = 0;

            @Override
            public boolean onLongClick(View view) {


                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rep_edit)
                        .customView(R.layout.edit_reps_setup, true)
                        .positiveText("Done").onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                viewHolder.repetitions.setText(String.valueOf(NewRepValue()));
//                                WorkoutActivity workout = new WorkoutActivity();
//                                workout.Exercises_reps[a] = NewRepValue();
                            }
                        })
                        .show()
                        .getCustomView();

                if (v != null) {

                    DialogName = (TextView) v.findViewById(R.id.ExerciseName);
                    DialogName.setText( mSelectedExercises.get(a).getExercise_name() );



                    Reps = (TextView) v.findViewById(R.id.Sets);
                    ActualRepValue = Integer.valueOf(viewHolder.repetitions.getText().toString());
                    Reps.setText(String.valueOf(ActualRepValue));
                    plusRep = (Button) v.findViewById(R.id.plusRep);
                    plusRep.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Increasing Reps Button
                            plusTempo(Reps, plusRep, minusRep);
                        }
                    });

                    minusRep = (Button) v.findViewById(R.id.minusRep);
                    minusRep.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Decreasing Reps Button
                            minusTempo(Reps, minusRep, plusRep);
                        }
                    });

                }

                //Check if actual rep value is 1 or 20 on Dialog open
                if (ActualRepValue == 1) {
                    minusRep.setEnabled(false);
                    minusRep.setClickable(false);
                } else if (ActualRepValue == 20) {
                    plusRep.setEnabled(false);
                    plusRep.setClickable(false);
                }

                return false;
            }

            public int NewRepValue() {
                return Integer.valueOf(Reps.getText().toString());
            }

            public void plusTempo(TextView view, Button button, Button button2) {
                value = Integer.parseInt(view.getText().toString());
                view.setText(String.valueOf(value + 1));
                value++;
                if (view == Reps) {
                    if (value == 20) {
                        button.setEnabled(false);
                        button.setClickable(false);
                    } else {
                        if (value > 1) {
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                } else {
                    if (value == 10) {
                        button.setEnabled(false);
                        button.setClickable(false);
                    } else {
                        if (value > 1) {
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                }
            }

            public void minusTempo(TextView view, Button button, Button button2) {
                value = Integer.parseInt(view.getText().toString());
                view.setText(String.valueOf(value - 1));
                value--;
                if (view == Reps) {
                    if ((value) == 1) {
                        button.setEnabled(false);
                        button.setClickable(false);
                    } else {
                        if (value < 20) {
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                } else {
                    if ((value) == 1) {
                        button.setEnabled(false);
                        button.setClickable(false);
                    } else {
                        if (value < 10) {
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                }
            }
        });

    }

    public void DownloadImage(final String url, final selectedExercisesViewHolder vh, final String imgName) {
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

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }


}