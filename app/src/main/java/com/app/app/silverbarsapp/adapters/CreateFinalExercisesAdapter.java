package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class CreateFinalExercisesAdapter extends RecyclerView.Adapter<CreateFinalExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = CreateFinalExercisesAdapter.class.getSimpleName();

    private ArrayList<ExerciseRep> mExercises = new ArrayList<>();
    private Context context;

    private Utilities utilities = new Utilities();
    private ExerciseListener exerciseListener;

    public CreateFinalExercisesAdapter(Context context, ArrayList<ExerciseRep> exercises) {
        this.context = context;
        mExercises = exercises;
    }

    public void addListener(ExerciseListener exerciseListener){
        this.exerciseListener = exerciseListener;
    }

    public ArrayList<ExerciseRep> getExercises(){
        return mExercises;
    }

    public void set(int position,ExerciseRep exercise){
        mExercises.set(position,exercise);
        notifyItemChanged(position);
    }


    class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item)LinearLayout item;
        @BindView(R.id.exercise_name) TextView mExerciseName;
        @BindView(R.id.imagen) SimpleDraweeView mExerciseImg;

        @BindView(R.id.exercise_number) TextView mExerciseNumber;
        @BindView(R.id.exercise_option) TextView mExerciseOption;

        @BindView(R.id.weight) TextView mExerciseWeight;
        @BindView(R.id.weight_layout)LinearLayout mExerciseLayoutWeight;


        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }

        @OnClick(R.id.item)
        public void onExerciseSelected(View view){
            int position = (int) view.getTag();
            exerciseListener.onExerciseSelected(mExercises.get(position),position);
        }
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExerciseViewHolder(
                LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.adapter_exercises_create_final, parent, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        try {

            viewHolder.item.setTag(position);

            //asignar nombre y repeticiones a cada elemento del recycler.
            viewHolder.mExerciseName.setText(mExercises.get(position).getExercise().getExercise_name());
            //set image in SimpleDraweeView
            viewHolder.mExerciseImg.setImageURI(Uri.parse((mExercises.get(position).getExercise().getExercise_image())));

            viewHolder.mExerciseNumber.setText(String.valueOf(mExercises.get(position).getNumber()));


            //exercise option ( rep or second )
            if(Objects.equals(mExercises.get(position).getExercise_state(), "REP")){
                viewHolder.mExerciseOption.setText(context.getString(R.string.activity_exercise_selected_reps));
            }else {
                viewHolder.mExerciseOption.setText(context.getString(R.string.activity_exercise_selected_secs));
            }

            //exercise weight
            viewHolder.mExerciseWeight.setText(
                    utilities.formaterDecimal(
                            String.valueOf(
                                    mExercises.get(position).getWeight()
                            )
                    )
            );

            if (mExercises.get(position).getWeight() > 0){
                viewHolder.mExerciseLayoutWeight.setVisibility(View.VISIBLE);
            }else {
                viewHolder.mExerciseLayoutWeight.setVisibility(View.INVISIBLE);
            }

        }catch (NullPointerException e){Log.e(TAG,"NullPointerException ");}
    }

    public interface ExerciseListener{
        void onExerciseSelected(ExerciseRep exercise,int position);
    }

}