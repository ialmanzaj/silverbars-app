package com.app.proj.silverbars.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.proj.silverbars.R;
import com.app.proj.silverbars.utils.Utilities;
import com.app.proj.silverbars.models.ExerciseRep;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


/**
 * Created by isaacalmanza on 10/04/16.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private static final String TAG = ExerciseAdapter.class.getSimpleName();

    
    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private TextView Positive, Negative, Isometric;

    Context context;
    private Utilities utilities;


    private ArrayList<ExerciseRep> exercises;

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView next;
        TextView repetitions;
        TextView positive;
        TextView negative;
        TextView isometric;


        SimpleDraweeView imagen_cache;
        ImageView imageView_local;


        private TextView DialogName, Reps;
        private Button plusRep, minusRep;
        private int value = 0;
        private int ActualRepValue = 0;

        public ExerciseViewHolder(View dialog) {
            super(dialog);

            nombre = (TextView) dialog.findViewById(R.id.nombre);
            repetitions = (TextView) dialog.findViewById(R.id.repetitions);

            imagen_cache = (SimpleDraweeView) dialog.findViewById(R.id.imagen);
            imageView_local = (ImageView) dialog.findViewById(R.id.imagen_local);


            positive = (TextView) dialog.findViewById(R.id.positive);
            isometric = (TextView) dialog.findViewById(R.id.isometric);
            negative = (TextView) dialog.findViewById(R.id.negative);
        }


        public void setListener(){
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                }
            });

        }

        public int getIntfromTextView(TextView textView){
            return Integer.valueOf(textView.getText().toString());
        }

        public void plusTempo(TextView view, Button button, Button button2){

            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value+1));
            value++;
            if (value == 10){
                button.setEnabled(false);
                button.setClickable(false);
            }else{
                if(value > 1){
                    button2.setEnabled(true);
                    button2.setClickable(true);
                }
            }


        }

        public void minusTempo(TextView view, Button button, Button button2){
            value = Integer.parseInt(view.getText().toString());
            view.setText(String.valueOf(value = value-1));
            Log.d(TAG,"value:"+value);
            if (value == 0){
                button.setEnabled(false);
                button.setClickable(false);
            }else {
                button.setEnabled(true);
                button.setClickable(true);
                value--;
            }


        }

        private void dialogImplementation(View view,int position){
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

        }
    }

    public ExerciseAdapter(Context context,ArrayList<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
        utilities = new Utilities();
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
        View dialog = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_exercise_cards, viewGroup, false);
        return new ExerciseViewHolder(dialog);
    }
    

    @Override
    public void onBindViewHolder(ExerciseViewHolder viewHolder,int position) {


        viewHolder.nombre.setText(exercises.get(position).getExercise().getExercise_name());
        viewHolder.repetitions.setText(String.valueOf(exercises.get(position).getRepetition()));

        //Log.d(TAG,"img "+exercises.get(a).getExercise().getExercise_image());

        String[] imageDir = exercises.get(position).getExercise().getExercise_image().split("exercises");

        if (imageDir.length == 2){
            //Log.d(TAG,"img from json");


            viewHolder.imagen_cache.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(exercises.get(position).getExercise().getExercise_image());
            viewHolder.imagen_cache.setImageURI(uri);

        }else {
            //Log.d(TAG,"img from local");
            Bitmap bmp;
            viewHolder.imageView_local.setVisibility(View.VISIBLE);
            //Log.d(TAG,"image: " +exercises.get(a).getExercise().getExercise_image());


            bmp = utilities.loadExerciseImageFromDevice(context,exercises.get(position).getExercise().getExercise_image());
            viewHolder.imageView_local.setImageBitmap(bmp);
        }



    }





}