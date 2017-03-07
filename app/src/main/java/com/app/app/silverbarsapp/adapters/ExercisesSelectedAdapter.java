package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.utils.ItemTouchHelperAdapter;
import com.app.app.silverbarsapp.utils.ItemTouchHelperViewHolder;
import com.app.app.silverbarsapp.utils.OnStartDragListener;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExercisesSelectedAdapter extends RecyclerView.Adapter<ExercisesSelectedAdapter.SelectedExercisesHolder>  implements ItemTouchHelperAdapter {

    private static final String TAG = ExercisesSelectedAdapter.class.getSimpleName();

    private final OnStartDragListener mDragStartListener;

    private Context mContext;
    private ArrayList<Exercise> mSelectedExercises;
    private OnExerciseListener listener;


    public ExercisesSelectedAdapter(Context context, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
        mSelectedExercises = new ArrayList<>();
    }


    public void setExercises(List<Exercise> exerciseReps){
        mSelectedExercises.addAll(exerciseReps);
        notifyDataSetChanged();
    }

    public class SelectedExercisesHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @BindView(R.id.nombre) TextView nombre;
        @BindView(R.id.handle) ImageView img_handle;
        @BindView(R.id.workout_layout) LinearLayout workout_layout;
        @BindView(R.id.imagen) SimpleDraweeView imagen;

        public SelectedExercisesHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }
        
        public void setTouchListener(final RecyclerView.ViewHolder viewHolder){
            img_handle.setOnTouchListener((view, event) -> {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
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

        //notify exercise deleted exercise and update muscle view
        notifyListener();
    }

    @Override
    public SelectedExercisesHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dialog = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercises, viewGroup, false);
        return new SelectedExercisesHolder(dialog);
    }

    @Override
    public void onBindViewHolder(SelectedExercisesHolder viewHolder, int position) {

        try {

            viewHolder.nombre.setText(mSelectedExercises.get(position).getExercise_name());


            viewHolder.setTouchListener(viewHolder);


            //img
            Uri uri = Uri.parse(mSelectedExercises.get(position).getExercise_image());
            viewHolder.imagen.setImageURI(uri);


        }catch (NullPointerException e){Log.e(TAG,"NullPointerException");}
    }



    public void add(Exercise exercise){
        mSelectedExercises.add(0,exercise);
        notifyItemInserted(0);
    }


    public ArrayList<Exercise> getSelectedExercises(){
        return mSelectedExercises;
    }


    public ArrayList<String> getSelectedExercisesName(){
        ArrayList<String> exercises_names = new ArrayList<>();

        for (int a = 0;a<mSelectedExercises.size();a++){
            exercises_names.add(mSelectedExercises.get(a).getExercise_name());
        }

        return exercises_names;
    }

    private void notifyListener(){
        //listener.onSizeChanged(mSelectedExercises.size());
    }

    public void setOnDataChangeListener(OnExerciseListener onDataChangeListener){listener = onDataChangeListener;}


    public interface OnExerciseListener {
        void onSizeChanged(int size);
    }

}