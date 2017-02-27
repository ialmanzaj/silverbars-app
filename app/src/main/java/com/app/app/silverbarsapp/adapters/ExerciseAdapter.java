package com.app.app.silverbarsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.utils.Utilities;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private static final String TAG = ExerciseAdapter.class.getSimpleName();

    private Context context;
    private Utilities utilities = new Utilities();
    private ArrayList<ExerciseRep> exercises;

    public ExerciseAdapter(Context context,ArrayList<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nombre) TextView nombre;

        @BindView(R.id.repetitions) TextView repetitions;
        @BindView(R.id.positive) TextView positive;
        @BindView(R.id.negative) TextView negative;
        @BindView(R.id.isometric) TextView isometric;
        @BindView(R.id.option) TextView option;

        @BindView(R.id.imagen) SimpleDraweeView imagen_cache;
        @BindView(R.id.imagen_local) ImageView imageView_local;


        private TextView DialogName, Reps;
        private Button plusRep, minusRep;
        private int value = 0;
        private int ActualRepValue = 0;


        public ExerciseViewHolder(View view) {
            super(view);
            //binding views
            ButterKnife.bind(this,view);
        }


        public void setListener(){

        }


       /* private void dialogImplementation(View view,int position){

             Button plusPositive;
             Button minusPositive;
             Button plusIsometric;
             Button minusIsometric;
             Button plusNegative;
             Button minusNegative;
             TextView Positive, Negative, Isometric;

            View dialog = new MaterialDialog.Builder(view.getContext())
                    .title(R.string.rep_edit)
                    .customView(R.layout.edit_exercise_dialog, true)
                    .positiveText(R.string.done_text).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();

                            positive.setText(Positive.getText());
                            isometric.setText(Isometric.getText());
                            negative.setText(Negative.getText());

                            //colocar nuevos valores a cada parte del tempo

                            //exercises.get(a).setTempo_isometric(getIntfromTextView(Isometric));
                            //exercises.get(a).setTempo_negative(getIntfromTextView(Negative));
                            //exercises.get(a).setTempo_positive(getIntfromTextView(Positive));
                        }
                    })
                    .show()
                    .getCustomView();

            if (dialog != null) {

                Positive = (TextView) dialog.findViewById(R.id.Positive);
                Isometric = (TextView) dialog.findViewById(R.id.Isometric);
                Negative = (TextView) dialog.findViewById(R.id.Negative);

                Positive.setText(positive.getText());
                Isometric.setText(isometric.getText());
                Negative.setText(negative.getText());


                plusPositive = (Button) dialog.findViewById(R.id.plusPositive);
                plusPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        plusTempo(Positive,plusPositive,minusPositive);
                    }
                });
                minusPositive = (Button) dialog.findViewById(R.id.minusPositive);
                minusPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        minusTempo(Positive,minusPositive,plusPositive);
                    }
                });
                plusIsometric = (Button) dialog.findViewById(R.id.plusIsometric);
                plusIsometric.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        plusTempo(Isometric,plusIsometric,minusIsometric);
                    }
                });
                minusIsometric = (Button) dialog.findViewById(R.id.minusIsometric);
                minusIsometric.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        minusTempo(Isometric,minusIsometric,plusIsometric);
                    }
                });
                plusNegative = (Button) dialog.findViewById(R.id.plusNegative);
                plusNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        plusTempo(Negative,plusNegative,minusNegative);
                    }
                });
                minusNegative = (Button) dialog.findViewById(R.id.minusNegative);
                minusNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        minusTempo(Negative,minusNegative,plusNegative);
                    }
                });

            }

            if (ActualRepValue == 1){
                minusRep.setEnabled(false);
                minusRep.setClickable(false);
            }else if(ActualRepValue == 20){
                plusRep.setEnabled(false);
                plusRep.setClickable(false);
            }

        }*/
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public ArrayList<ExerciseRep> getExercises() {
        return exercises;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ExerciseViewHolder( LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercises_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder,int position) {

        viewHolder.nombre.setText(exercises.get(position).getExercise().getExercise_name());

        //repetitions  or seconds
        if (exercises.get(position).getRepetition() > 0){
            viewHolder.repetitions.setText(String.valueOf(exercises.get(position).getRepetition()));
        }else {
            viewHolder.repetitions.setText(String.valueOf(exercises.get(position).getSeconds()));
            viewHolder.option.setText(context.getString(R.string.exercise_adapter_option));
        }

        //Log.d(TAG,"img "+exercises.get(a).getExercise().getExercise_image());

        try {


            String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");


            if (imageDir.length == 2){
                //Log.d(TAG,"img from json");

                viewHolder.imagen_cache.setVisibility(View.VISIBLE);
                viewHolder.imagen_cache.setImageURI(
                        Uri.parse(exercises.get(position).getExercise().getExercise_image()));

            }else {
                //Log.d(TAG,"img from local");

                viewHolder.imageView_local.setVisibility(View.VISIBLE);
                viewHolder.imageView_local.setImageBitmap(
                        utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image()));
            }


        }catch (NullPointerException e){
            Log.e(TAG,""+e);
        }

    }





}