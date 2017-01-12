package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
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
import com.app.proj.silverbars.utils.ItemTouchHelperAdapter;
import com.app.proj.silverbars.utils.ItemTouchHelperViewHolder;
import com.app.proj.silverbars.utils.OnStartDragListener;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.models.ExerciseRep;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;


public class ExercisesSelectedAdapter extends RecyclerView.Adapter<ExercisesSelectedAdapter.selectedExercisesViewHolder>  implements ItemTouchHelperAdapter {

    private static final String TAG = ExercisesSelectedAdapter.class.getSimpleName();

    private List<ExerciseRep> mSelectedExercises;

    private final OnStartDragListener mDragStartListener;

    private Context mContext;

    OnDataChangeListener mOnDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){mOnDataChangeListener = onDataChangeListener;}

    public interface OnDataChangeListener{
        public void onDataChanged(int size);
    }


    public ExercisesSelectedAdapter(Context context, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
    }

    public void setExercises(List<ExerciseRep> exerciseReps){
        mSelectedExercises = exerciseReps;
    }


    public class selectedExercisesViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        // Campos respectivos de un item
        @BindView(R.id.nombre) TextView nombre;

        @BindView(R.id.handle) ImageView img_handle;
        @BindView(R.id.unchecked) ImageView unchecked;
        @BindView(R.id.checked) ImageView checked;
        @BindView(R.id.repetitions) TextView repetitions;
        @BindView(R.id.workout_layout) LinearLayout workout_layout;


        @BindView(R.id.imagen) SimpleDraweeView imagen;


        public selectedExercisesViewHolder(View itemView) {
            super(itemView);


        }
        
        public void setTouchListener(final RecyclerView.ViewHolder viewHolder){
            img_handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(viewHolder);
                    }
                    return false;
                }
            });
        }
        
        public void setListener(){
            workout_layout.setOnClickListener(new View.OnClickListener() {

                private TextView DialogName, reps_dialog;
                private Button plusRep, minusRep;

                private int ActualRepValue = 0;

                @Override
                public void onClick(View view) {

                    int position = (int) view.getTag();

                    
                    View dialog = new MaterialDialog.Builder(view.getContext())
                            .title(R.string.rep_edit)
                            .customView(R.layout.edit_reps_setup, true)
                            .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();

                                   /* repetitions.setText(String.valueOf(NewRepValue(reps_dialog)));
                                    mSelectedExercises.get(position).setRepetition(NewRepValue(reps_dialog));*/
                                }
                            })
                            .show()
                            .getCustomView();

                    if (dialog != null) {

                        DialogName = (TextView) dialog.findViewById(R.id.ExerciseName);
                        DialogName.setText(mSelectedExercises.get(position).getExercise().getExercise_name());

                        ActualRepValue = Integer.valueOf(repetitions.getText().toString());
                        reps_dialog = (TextView) dialog.findViewById(R.id.Reps);


                        mSelectedExercises.get(position).setRepetition(ActualRepValue);
                        reps_dialog.setText(String.valueOf(ActualRepValue));



                        plusRep = (Button) dialog.findViewById(R.id.plusRep);
                        plusRep.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Increasing reps_dialog Button
                                //plusTempo(reps_dialog, plusRep, minusRep);
                            }
                        });

                        minusRep = (Button) dialog.findViewById(R.id.minusRep);
                        minusRep.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Decreasing reps_dialog Button
                                //minusTempo(reps_dialog, minusRep, plusRep);
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
            });
            
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




    @Override
    public int getItemCount() {
        return mSelectedExercises.size();
    }

  

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        
        Collections.swap(mSelectedExercises,fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        notifyDataSetChanged();
        
        return true;
    }
    

    @Override
    public void onItemDismiss(int position) {
        mSelectedExercises.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());


        notifyActivity();
    }
    

    @Override
    public selectedExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dialog = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.exercises, viewGroup, false);
        return new selectedExercisesViewHolder(dialog);
    }



    @Override
    public void onBindViewHolder(selectedExercisesViewHolder viewHolder,int position) {

        viewHolder.nombre.setText(mSelectedExercises.get(position).getExercise().getExercise_name());

        mSelectedExercises.get(position).setRepetition(1);


        Uri uri = Uri.parse(mSelectedExercises.get(position).getExercise().getExercise_image());
        viewHolder.imagen.setImageURI(uri);

        // Start a drag whenever the handle view it touched
        viewHolder.setTouchListener(viewHolder);


        viewHolder.setListener();

    }

    public List<ExerciseRep> getSelectedExercises(){
        return mSelectedExercises;
    }
    

    public ArrayList<String> getSelectedExercisesName(){
        ArrayList<String> exercises = new ArrayList<>();
        for (int a = 0;a<mSelectedExercises.size();a++){
            exercises.add(mSelectedExercises.get(a).getExercise().getExercise_name());
        }
        return exercises;
    }

    public void add(ExerciseRep exerciseRep){
        this.mSelectedExercises.add(exerciseRep);
    }

    private void notifyActivity(){
        mOnDataChangeListener.onDataChanged(mSelectedExercises.size());
    }




}