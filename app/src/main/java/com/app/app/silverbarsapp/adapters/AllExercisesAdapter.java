package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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

public class AllExercisesAdapter extends RecyclerView.Adapter<AllExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = AllExercisesAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Exercise> mExercises = new ArrayList<>();

    private SparseBooleanArray mExercisesSelected = new SparseBooleanArray();

    public AllExercisesAdapter(Context context, ArrayList<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }

    public SparseBooleanArray getExercisesSelected(){
        return mExercisesSelected;
    }


    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemSelected) LinearLayout item;
        @BindView(R.id.nombre) TextView exercise_name;
        @BindView(R.id.imagen) SimpleDraweeView exercise_img;
        @BindView(R.id.checkbox) CheckBox selection;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnCheckedChanged(R.id.checkbox)
        public void selectedItem(CompoundButton button){
            int exercise_id = (Integer) button.getTag();
            if (!mExercisesSelected.get(exercise_id)) {
                mExercisesSelected.put(exercise_id, true);
            } else
                mExercisesSelected.put(exercise_id, false);
        }
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_exercises_list, viewGroup, false);
        return new ExerciseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        try {

            viewHolder.exercise_name.setText(mExercises.get(position).getExercise_name());

            //checkbox settings
            viewHolder.selection.setTag(mExercises.get(position).getId());
            mExercisesSelected.put(mExercises.get(position).getId(),false);

            //img
            viewHolder.exercise_img.setImageURI(Uri.parse(mExercises.get(position).getExercise_image()));



        }catch (NullPointerException e){Log.e(TAG,"NullPointerException");}
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }



}