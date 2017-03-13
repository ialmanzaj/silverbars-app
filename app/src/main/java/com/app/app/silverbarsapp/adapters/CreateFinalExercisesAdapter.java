package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.app.silverbarsapp.MutableWatcher;
import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;
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
    private Utilities utilities = new Utilities();

    public CreateFinalExercisesAdapter(Context context, ArrayList<ExerciseRep> exerciseReps) {
        this.context = context;
        mExercises = exerciseReps;
    }

    public ArrayList<ExerciseRep> getExercises(){
        return mExercises;
    }


    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        MutableWatcher mWatcher;

        @BindView(R.id.nombre) TextView exercise_name;
        @BindView(R.id.repsOrSeconds) AutoCompleteTextView repsOrSeconds;
        @BindView(R.id.imagen) SimpleDraweeView exercise_img;
        @BindView(R.id.spinner_rep) Spinner mSelectTypeWorkoutSpinner;

        private int positionSpinner = 0;

        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
            initSpinner();
        }


        private void initSpinner(){
            //Inicialize classification adapter
            ArrayAdapter<String> mTypeAdapter  = new ArrayAdapter<>(context, R.layout.simple_spinner_item);
            mTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mTypeAdapter.add("Rep");
            mTypeAdapter.add("Seconds");
            mSelectTypeWorkoutSpinner.setAdapter(mTypeAdapter);
            mSelectTypeWorkoutSpinner.setSelection(0);
        }

        @OnItemSelected(R.id.spinner_rep)
        public void spinnerItemSelected(Spinner spinner, int position) {
            positionSpinner = position;

            int index = mExercises.indexOf(utilities.getExerciseRepById(mExercises, mWatcher.getPosition()));
            mExercises.get(index).setExerciseState(position);
        }

        @OnTextChanged(value = R.id.repsOrSeconds, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
        public void repChanged(Editable editable){

            if (!Objects.equals(editable.toString(), "")){

                int index = mExercises.indexOf(utilities.getExerciseRepById(mExercises, mWatcher.getPosition()));

                mExercises.get(index).setNumber(Integer.parseInt(editable.toString()));
            }
        }


    }
    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_create_final, parent, false);
        return new ExerciseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder, int position) {

        try {

            viewHolder.mWatcher = new MutableWatcher();
            viewHolder.mWatcher.setPosition(mExercises.get(position).getExercise().getId());

            //asignar nombre y repeticiones a cada elemento del recycler.
            viewHolder.exercise_name.setText(mExercises.get(position).getExercise().getExercise_name());

            viewHolder.repsOrSeconds.setTag(mExercises.get(position).getExercise().getId());
            viewHolder.repsOrSeconds.setText("0");

            viewHolder.repsOrSeconds.addTextChangedListener(viewHolder.mWatcher);

            //set image in SimpleDraweeView
            viewHolder.exercise_img.setImageURI(Uri.parse((mExercises.get(position).getExercise().getExercise_image())));



        }catch (NullPointerException e){Log.e(TAG,"NullPointerException ");}
    }

}