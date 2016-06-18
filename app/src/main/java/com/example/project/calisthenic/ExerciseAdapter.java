package com.example.project.calisthenic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<WorkoutInfo> items;

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public TextView repetitions;

        public ExerciseViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
//            next = (TextView) v.findViewById(R.id.next);
            repetitions = (TextView) v.findViewById(R.id.repetitions);
        }
    }

    public ExerciseAdapter(List<WorkoutInfo> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_exercise_cards, viewGroup, false);
        return new ExerciseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExerciseViewHolder viewHolder, int i) {
        final int a = i;
        WorkoutActivity workout = new WorkoutActivity();
        Bitmap bmp = null;
        //Setting values to each recylerView Element
        URL url = null;
        try {
            url = new URL(workout.ParsedExercises.get(a).getExercise_image());
            Log.v("Url",url.toString());
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewHolder.imagen.setImageBitmap(bmp);
        viewHolder.nombre.setText(workout.ParsedExercises.get(a).getExercise_name());
//        viewHolder.next.setText("Visitas:"+String.valueOf(items.get(i).getVisitas()));
//        viewHolder.repetitions.setText(String.valueOf(workout.ParsedExercises.get(i).get);
        //OnLongClickListener for each recylclerView element
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            private TextView DialogName, Reps;
            private Button plusRep, minusRep;
            private int value = 0;
            private int ActualRepValue = 0;

            @Override
            public boolean onLongClick(View view) {
                boolean wrapInScrollView = true;
                //Dialog when LongClick on element
                View v = new MaterialDialog.Builder(view.getContext())
                        .title(R.string.title)
                        .customView(R.layout.edit_reps_setup, wrapInScrollView)
                        .positiveText("Done").onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                //On Dialog "Done" ClickListener
                                viewHolder.repetitions.setText(String.valueOf(NewRepValue()));
                                WorkoutActivity workout = new WorkoutActivity();
                                workout.Exercises_reps[a] = NewRepValue();
                            }
                        })
                        .show()
                        .getCustomView();
                //Dialog elements
                DialogName = (TextView) v.findViewById(R.id.ExerciseName);
                DialogName.setText(items.get(a).getNombre());
                Reps = (TextView) v.findViewById(R.id.Reps);
                ActualRepValue = Integer.valueOf(viewHolder.repetitions.getText().toString());
                Reps.setText(String.valueOf(ActualRepValue));
                plusRep = (Button) v.findViewById(R.id.plusRep);
                plusRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Increasing Reps Button
                        plusTempo(Reps,plusRep,minusRep);
                    }
                });

                minusRep = (Button) v.findViewById(R.id.minusRep);
                minusRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Decreasing Reps Button
                        minusTempo(Reps,minusRep,plusRep);
                    }
                });
                //Check if actual rep value is 1 or 20 on Dialog open
                if (ActualRepValue == 1){
                    minusRep.setEnabled(false);
                    minusRep.setClickable(false);
                }else if(ActualRepValue == 20){
                    plusRep.setEnabled(false);
                    plusRep.setClickable(false);
                }

                return false;
            }

            public int NewRepValue(){
                return Integer.valueOf(Reps.getText().toString());
            }

            public void plusTempo(TextView view, Button button, Button button2){
                value = Integer.parseInt(view.getText().toString());
                view.setText(String.valueOf(value+1));
                value++;
                if (view == Reps){
                    if (value == 20){
                        button.setEnabled(false);
                        button.setClickable(false);
                    }else{
                        if(value > 1){
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                }else{
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

            }

            public void minusTempo(TextView view, Button button, Button button2){
                value = Integer.parseInt(view.getText().toString());
                view.setText(String.valueOf(value-1));
                value--;
                if (view == Reps){
                    if ((value)==1){
                        button.setEnabled(false);
                        button.setClickable(false);
                    }else{
                        if(value < 20){
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                }else{
                    if ((value)==1){
                        button.setEnabled(false);
                        button.setClickable(false);
                    }else{
                        if(value < 10){
                            button2.setEnabled(true);
                            button2.setClickable(true);
                        }
                    }
                }
            }
        });
    }

}