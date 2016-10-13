package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.loadExerciseImageFromDevice;
import static com.app.proj.silverbars.Utilities.saveExerciseImageInDevice;


public class RecyclerExerciseSelectedAdapter extends RecyclerView.Adapter<RecyclerExerciseSelectedAdapter.selectedExercisesViewHolder>  implements ItemTouchHelperAdapter {

    private static final String TAG = "ExercisesAdapter";
    private List<ExerciseRep> mSelectedExercises;

    private final OnStartDragListener mDragStartListener;

    Context mContext;

    public static class selectedExercisesViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        // Campos respectivos de un item
        private ImageView img_handle;
        private TextView nombre;
        private TextView next;
        private ImageView unchecked, checked;
        private TextView repetitions;
        private LinearLayout workout_layout;

        SimpleDraweeView imagen;


        public selectedExercisesViewHolder(View itemView) {
            super(itemView);
            //imagen = (ImageView) itemView.findViewById(R.id.imagen);
            nombre = (TextView) itemView.findViewById(R.id.nombre);

            imagen = (SimpleDraweeView) itemView.findViewById(R.id.imagen);

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


    public RecyclerExerciseSelectedAdapter(Context context, List<ExerciseRep> exercises, OnStartDragListener dragStartListener) {

        mDragStartListener = dragStartListener;
        mContext = context;
        mSelectedExercises = exercises;

    }


    @Override
    public int getItemCount() {
        return mSelectedExercises.size();
    }

    public List<ExerciseRep> getSelectedExercisesJson(){
        return mSelectedExercises;
    }



    public ArrayList<String> getSelectedExercisesName(){
        ArrayList<String> exercises = new ArrayList<>();
        for (int a = 0;a<mSelectedExercises.size();a++){
            exercises.add(mSelectedExercises.get(a).getExercise().getExercise_name());
        }
        return exercises;
    }

    public int[] getExerciseReps() {
        int[] exercises_reps = new int[mSelectedExercises.size()];
        for (int a = 0; a < mSelectedExercises.size(); a++) {
            exercises_reps[a] = mSelectedExercises.get(a).getRepetition();
        }

        return exercises_reps;
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

        viewHolder.nombre.setText(mSelectedExercises.get(a).getExercise().getExercise_name());
        mSelectedExercises.get(a).setRepetition(1);


        Uri uri = Uri.parse(mSelectedExercises.get(a).getExercise().getExercise_image());
        viewHolder.imagen.setImageURI(uri);



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

            private TextView DialogName, reps_dialog;
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
                                viewHolder.repetitions.setText(String.valueOf(NewRepValue(reps_dialog)));
                                mSelectedExercises.get(a).setRepetition(NewRepValue(reps_dialog));
                            }
                        })
                        .show()
                        .getCustomView();

                if (v != null) {

                    DialogName = (TextView) v.findViewById(R.id.ExerciseName);
                    DialogName.setText(mSelectedExercises.get(a).getExercise().getExercise_name());

                   /* for (int indice = 0;indice < mSelectedExercises.size();indice++){
                        Log.v(TAG,"mSelectedExercises: "+mSelectedExercises.get(indice).getExercise_name());
                        Log.v(TAG,"position in list: "+indice);
                    }
*/
                    ActualRepValue = Integer.valueOf(viewHolder.repetitions.getText().toString());
                    reps_dialog = (TextView) v.findViewById(R.id.Reps);
                    mSelectedExercises.get(a).setRepetition(ActualRepValue);
                    reps_dialog.setText(String.valueOf(ActualRepValue));



                    plusRep = (Button) v.findViewById(R.id.plusRep);
                    plusRep.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Increasing reps_dialog Button
                            plusTempo(reps_dialog, plusRep, minusRep);
                        }
                    });

                    minusRep = (Button) v.findViewById(R.id.minusRep);
                    minusRep.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Decreasing reps_dialog Button
                            minusTempo(reps_dialog, minusRep, plusRep);
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

            public int NewRepValue(TextView textView) {
                return Integer.valueOf(textView.getText().toString());
            }

            public void plusTempo(TextView view, Button button, Button button2) {
                value = Integer.parseInt(view.getText().toString());
                view.setText(String.valueOf(value + 1));
                value++;
                if (view == reps_dialog) {
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
                if (view == reps_dialog) {
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




}