package com.app.proj.silverbars;

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
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.app.proj.silverbars.Utilities.loadExerciseImageFromDevice;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private static final String TAG = "EXERCISE ADAPTER";

    private ArrayList<ExerciseRep> exercises;

    private Button plusPositive;
    private Button minusPositive;
    private Button plusIsometric;
    private Button minusIsometric;
    private Button plusNegative;
    private Button minusNegative;
    private TextView Positive, Negative, Isometric;

    Context context;

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        //public ImageView imagen;
        TextView nombre;
        TextView next;
        TextView repetitions;
        TextView positive;
        TextView negative;
        TextView isometric;

        SimpleDraweeView imagen_cache;
        ImageView imageView_local;


        public ExerciseViewHolder(View v) {
            super(v);

            nombre = (TextView) v.findViewById(R.id.nombre);
            repetitions = (TextView) v.findViewById(R.id.repetitions);

            imagen_cache = (SimpleDraweeView) v.findViewById(R.id.imagen);
            imageView_local = (ImageView) v.findViewById(R.id.imagen_local);


            positive = (TextView) v.findViewById(R.id.positive);
            isometric = (TextView) v.findViewById(R.id.isometric);
            negative = (TextView) v.findViewById(R.id.negative);
        }
    }

    public ExerciseAdapter(Context context,ArrayList<ExerciseRep> exercises) {
        this.context = context;
        this.exercises = exercises;
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
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_exercise_cards, viewGroup, false);
        return new ExerciseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExerciseViewHolder viewHolder,int i) {

         final int a = viewHolder.getAdapterPosition();

        viewHolder.nombre.setText(exercises.get(a).getExercise().getExercise_name());
        viewHolder.repetitions.setText(String.valueOf(exercises.get(a).getRepetition()));


        String[] imageDir = exercises.get(a).getExercise().getExercise_image().split("exercises");

        if (imageDir.length == 2){
            Log.v(TAG,"img from json");


            viewHolder.imagen_cache.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(exercises.get(a).getExercise().getExercise_image());
            viewHolder.imagen_cache.setImageURI(uri);

        }else {

            Bitmap bmp;
            viewHolder.imageView_local.setVisibility(View.VISIBLE);
            Log.v(TAG,"image: " +exercises.get(a).getExercise().getExercise_image());



            bmp = loadExerciseImageFromDevice(context,exercises.get(a).getExercise().getExercise_image());
            viewHolder.imageView_local.setImageBitmap(bmp);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            private TextView DialogName, Reps;
            private Button plusRep, minusRep;
            private int value = 0;
            private int ActualRepValue = 0;

            @Override
            public void onClick(View view) {

                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.rep_edit)
                        .customView(R.layout.edit_exercise_dialog, true)
                        .positiveText("Done").onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                                viewHolder.positive.setText(Positive.getText());
                                viewHolder.isometric.setText(Isometric.getText());
                                viewHolder.negative.setText(Negative.getText());

                                //colocar nuevos valores a cada parte del tempo

                                exercises.get(a).setTempo_isometric(getIntfromTextView(Isometric));
                                exercises.get(a).setTempo_negative(getIntfromTextView(Negative));
                                exercises.get(a).setTempo_positive(getIntfromTextView(Positive));


                            }
                        })
                        .show()
                        .getCustomView();

                if (v != null) {

                        Positive = (TextView) v.findViewById(R.id.Positive);
                        Isometric = (TextView) v.findViewById(R.id.Isometric);
                        Negative = (TextView) v.findViewById(R.id.Negative);

                        Positive.setText(viewHolder.positive.getText());
                        Isometric.setText(viewHolder.isometric.getText());
                        Negative.setText(viewHolder.negative.getText());




                        plusPositive = (Button) v.findViewById(R.id.plusPositive);
                        plusPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                plusTempo(Positive,plusPositive,minusPositive);
                            }
                        });
                        minusPositive = (Button) v.findViewById(R.id.minusPositive);
                        minusPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                minusTempo(Positive,minusPositive,plusPositive);
                            }
                        });
                        plusIsometric = (Button) v.findViewById(R.id.plusIsometric);
                        plusIsometric.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                plusTempo(Isometric,plusIsometric,minusIsometric);
                            }
                        });
                        minusIsometric = (Button) v.findViewById(R.id.minusIsometric);
                        minusIsometric.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                minusTempo(Isometric,minusIsometric,plusIsometric);
                            }
                        });
                        plusNegative = (Button) v.findViewById(R.id.plusNegative);
                        plusNegative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                plusTempo(Negative,plusNegative,minusNegative);
                            }
                        });
                        minusNegative = (Button) v.findViewById(R.id.minusNegative);
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
                Log.v(TAG,"value:"+value);
                if (value == 0){
                        button.setEnabled(false);
                        button.setClickable(false);
                }else {
                    button.setEnabled(true);
                    button.setClickable(true);
                    value--;
                }


            }


        });
    }


}