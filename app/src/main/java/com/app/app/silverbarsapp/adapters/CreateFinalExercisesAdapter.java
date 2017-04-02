package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.MutableWatcher;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

/**
 * Created by isaacalmanza on 10/04/16.
 */
public class CreateFinalExercisesAdapter extends RecyclerView.Adapter<CreateFinalExercisesAdapter.ExerciseViewHolder> {

    private static final String TAG = CreateFinalExercisesAdapter.class.getSimpleName();

    private ArrayList<ExerciseRep> mExercises = new ArrayList<>();
    private Context context;

    public CreateFinalExercisesAdapter(Context context, ArrayList<ExerciseRep> exercises) {
        this.context = context;
        mExercises = exercises;
    }

    public ArrayList<ExerciseRep> getExercises(){
        return mExercises;
    }

    public void set(int index,ExerciseRep exerciseRep){
        mExercises.set(index,exerciseRep);
        notifyDataSetChanged();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        MutableWatcher mTextWatcher;

        @BindView(R.id.exercise_name) TextView mExerciseName;
        @BindView(R.id.repsOrSeconds) AutoCompleteTextView mOptionrepsOrseconds;
        @BindView(R.id.imagen) SimpleDraweeView mExerciseImg;
        @BindView(R.id.spinner_rep) Spinner mSelectTypeWorkoutSpinner;
        @BindView(R.id.weight) AutoCompleteTextView weightTextView;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
            initSpinner();
        }

        private void initSpinner(){
            //Inicialize classification adapter
            ArrayAdapter<String> mTypeAdapter  = new ArrayAdapter<>(context, R.layout.simple_spinner_item);
            mTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            mTypeAdapter.add("reps");
            mTypeAdapter.add("secs");
            mSelectTypeWorkoutSpinner.setAdapter(mTypeAdapter);
            mSelectTypeWorkoutSpinner.setSelection(0);
        }

        @OnItemSelected(R.id.spinner_rep)
        void spinnerItemSelected(Spinner spinner, int position) {
            mExercises.get( mTextWatcher.getPosition() ).setExerciseState(position);
        }

        @OnTextChanged(value = R.id.repsOrSeconds)
         void repChanged(CharSequence charSequence){
            if (!Objects.equals(charSequence.toString(), "")){
                mExercises.get(mTextWatcher.getPosition()).setNumber(Integer.parseInt(charSequence.toString()));
            }
        }

        @OnTextChanged(value = R.id.weight)
        void weightChanged(CharSequence charSequence){
            if (!Objects.equals(charSequence.toString(), "")){
                Log.d(TAG,"position "+mTextWatcher.getPosition());
                mExercises.get(mTextWatcher.getPosition()).setWeight(Double.valueOf(charSequence.toString()));
            }
        }


    }
    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExerciseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_create_final, parent, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        try {

            viewHolder.mTextWatcher = new MutableWatcher();
            viewHolder.mTextWatcher.updatePosition(position);

            viewHolder.mOptionrepsOrseconds.setTag(mExercises.get(position).getExercise().getId());
            viewHolder.mOptionrepsOrseconds.setText(String.valueOf(mExercises.get(position).getNumber()));
            viewHolder.mOptionrepsOrseconds.addTextChangedListener(viewHolder.mTextWatcher);

            viewHolder.weightTextView.setText(String.valueOf(mExercises.get(position).getWeight()));


            //asignar nombre y repeticiones a cada elemento del recycler.
            viewHolder.mExerciseName.setText(mExercises.get(position).getExercise().getExercise_name());

            //set image in SimpleDraweeView
            viewHolder.mExerciseImg.setImageURI(Uri.parse((mExercises.get(position).getExercise().getExercise_image())));

        }catch (NullPointerException e){Log.e(TAG,"NullPointerException ");}
    }

}