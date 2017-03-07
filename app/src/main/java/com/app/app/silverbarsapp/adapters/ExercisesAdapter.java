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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = ExercisesAdapter.class.getSimpleName();

    private Context mContext;
    private List<Exercise> mExercises = new ArrayList<>();
    private SparseBooleanArray mExercisesSelected = new SparseBooleanArray();


    public ExercisesAdapter(Context context, List<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemSelected) LinearLayout item;
        @BindView(R.id.nombre) TextView nombre;
        @BindView(R.id.checkbox) CheckBox checkBox;
        @BindView(R.id.imagen) SimpleDraweeView imagen;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnCheckedChanged(R.id.checkbox)
        public void selectedItem(CompoundButton button){

            int exercise_id = (Integer) button.getTag();

            if (!mExercisesSelected.get(exercise_id)) {
                Log.i(TAG,"checked");
                mExercisesSelected.put(exercise_id, true);


            } else {
                Log.i(TAG,"unchecked");
                mExercisesSelected.put(exercise_id, false);
            }

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

            viewHolder.nombre.setText(mExercises.get(position).getExercise_name());

            //checkbox settings
            viewHolder.checkBox.setTag(mExercises.get(position).getId());
            mExercisesSelected.put(mExercises.get(position).getId(),false);

            //img
            Uri uri = Uri.parse(mExercises.get(position).getExercise_image());
            viewHolder.imagen.setImageURI(uri);


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