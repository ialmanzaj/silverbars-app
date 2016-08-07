package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RecyclerExerciseSelectedAdapter extends RecyclerView.Adapter<RecyclerExerciseSelectedAdapter.selectedExercisesViewHolder>  implements ItemTouchHelperAdapter {

    private static final String TAG = "ExercisesAdapter";
    private List<JsonExercise> mSelectedExercises;

    private final OnStartDragListener mDragStartListener;



    public static class selectedExercisesViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        // Campos respectivos de un item
        private ImageView imagen_exercise,img_handle;
        private TextView nombre;
        private TextView next;
        private ImageView unchecked, checked;
        private TextView repetitions;
        private LinearLayout workout_layout;


        public selectedExercisesViewHolder(View itemView) {
            super(itemView);
            imagen_exercise = (ImageView) itemView.findViewById(R.id.imagen);
            nombre = (TextView) itemView.findViewById(R.id.nombre);

            img_handle = (ImageView) itemView.findViewById(R.id.handle);
            unchecked = (ImageView) itemView.findViewById(R.id.unchecked);
            checked = (ImageView) itemView.findViewById(R.id.checked);
            repetitions = (TextView) itemView.findViewById(R.id.repetitions);
            workout_layout = (LinearLayout) itemView.findViewById(R.id.workout_layout);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }


    public RecyclerExerciseSelectedAdapter(Context context, List<JsonExercise> exercises,OnStartDragListener dragStartListener) {


        mDragStartListener = dragStartListener;
        Context mContext = context;
        mSelectedExercises = exercises;

    }


    @Override
    public int getItemCount() {

        //Log.v(TAG,"getItemCount, size: "+mSelectedExercises.size());
        return mSelectedExercises.size();

    }

    public List<JsonExercise> getSelectedExercisesJson(){
        return mSelectedExercises;
    }



    public ArrayList<String> getSelectedExercisesArrayList(){
        ArrayList<String> exercises = new ArrayList<>();
        for (int a = 0;a<mSelectedExercises.size();a++){
            exercises.add(mSelectedExercises.get(a).getExercise_name());
        }
        return exercises;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        try {
            //Log.v(TAG,"onItemMove, size: "+mSelectedExercises.size());
            Collections.swap(mSelectedExercises,fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);


            notifyDataSetChanged();
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG,"IndexOutOfBoundsException",e);
        }

        return true;
    }


    @Override
    public void onItemDismiss(int position) {


       try {

            mSelectedExercises.remove(position);
            notifyItemRemoved(position);

           notifyItemRangeChanged(position, getItemCount());


            Log.v(TAG,"item eliminado de lista");
            Log.v(TAG,"size: "+mSelectedExercises.size());

        }catch (IndexOutOfBoundsException e){
           Log.e(TAG,"IndexOutOfBoundsException",e);
       }



    }



    @Override
    public selectedExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.exercises, viewGroup, false);
        return new selectedExercisesViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final selectedExercisesViewHolder viewHolder,int i) {

        final int a = viewHolder.getAdapterPosition();
        //Log.v(TAG,"Exercises selected: "+mSelectedExercises.get(a).getExercise_name());

        String[] imageDir = mSelectedExercises.get(a).getExercise_image().split("exercises");
        String Parsedurl = "exercises" + imageDir[1];
        String[] imagesName = Parsedurl.split("/");
        String imgName = imagesName[2];
        Bitmap bmp = loadImageFromCache(imgName);


        if (bmp != null) {
            viewHolder.imagen_exercise.setImageBitmap(bmp);
        } else {
            DownloadImage(Parsedurl, viewHolder, imgName);
        }

        viewHolder.nombre.setText(mSelectedExercises.get(a).getExercise_name());

        // Start a drag whenever the handle view it touched
        viewHolder.img_handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });


        viewHolder.workout_layout.setOnClickListener(new View.OnClickListener() {

            private TextView DialogName, Reps;
            private Button plusRep, minusRep;
            private int value = 0;
            private int ActualRepValue = 0;

            @Override
            public void onClick(View view) {

                //Log.v(TAG,"value of a: "+a);
                //Log.v(TAG,"item: "+mSelectedExercises.get(a).getExercise_name());

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
                    DialogName.setText(mSelectedExercises.get(a).getExercise_name());

                   /* for (int indice = 0;indice < mSelectedExercises.size();indice++){
                        Log.v(TAG,"mSelectedExercises: "+mSelectedExercises.get(indice).getExercise_name());
                        Log.v(TAG,"position in list: "+indice);
                    }
*/




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
        Log.v(TAG,"DownloadImage()");
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
                            vh.imagen_exercise.setImageBitmap(bitmap);
                        }
                        else {
                            Log.e(TAG, "Download server contact failed");
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