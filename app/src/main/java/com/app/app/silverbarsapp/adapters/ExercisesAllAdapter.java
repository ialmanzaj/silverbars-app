package com.app.app.silverbarsapp.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.Exercise;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class ExercisesAllAdapter extends RecyclerView.Adapter<ExercisesAllAdapter.ExerciseViewHolder> {

    private static final String TAG = ExercisesAllAdapter.class.getSimpleName();

    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private SparseBooleanArray mExercisesSelected = new SparseBooleanArray();

    public ExercisesAllAdapter(ArrayList<Exercise> exercises) {
        mExercises = exercises;
    }


    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.exercise_name) TextView mExerciseName;
        @BindView(R.id.img_cache) SimpleDraweeView mExerciseImgCache;
        @BindView(R.id.checkbox) CheckBox mSelectedBox;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnCheckedChanged(R.id.checkbox)
        void setOnCheckedChangeListener(CompoundButton buttonView,boolean isChecked){
            int id = (Integer)  buttonView.getTag();
            mExercisesSelected.put(id,isChecked);
        }

        boolean isChecked(CompoundButton buttonView){
            int id = (Integer)  buttonView.getTag();
            return mExercisesSelected.get(id);
        }


    }
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ExerciseViewHolder(
                LayoutInflater.from(
                        viewGroup.getContext()).inflate(
                                R.layout.all_exercises_list, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {
        try {

            viewHolder.mExerciseName.setText(mExercises.get(position).getExercise_name());
            viewHolder.mExerciseImgCache.setImageURI(Uri.parse(mExercises.get(position).getExercise_image()));

            viewHolder.mSelectedBox.setTag(mExercises.get(position).getId());
            viewHolder.mSelectedBox.setChecked(viewHolder.isChecked(viewHolder.mSelectedBox));

        }catch (NullPointerException e){Log.e(TAG,"NullPointerException");}
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public SparseBooleanArray getExercisesSelected(){
        return mExercisesSelected;
    }

}