package com.app.app.silverbarsapp;

import android.util.Log;

import com.app.app.silverbarsapp.models.ExerciseProgression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 04/18/17.
 */

public class ProgressionAlgoritm {

    private static final String TAG = ProgressionAlgoritm.class.getSimpleName();


    public ProgressionAlgoritm(){}


    public ArrayList<ExerciseProgression> compareExerciseProgression(
            List<com.app.app.silverbarsapp.database_models.ExerciseProgression> old_progressions,
            ArrayList<ExerciseProgression> current_exercises){

        ArrayList<ExerciseProgression> progressions = new ArrayList<>();


        for (ExerciseProgression current_exercise : current_exercises){


            com.app.app.silverbarsapp.database_models.ExerciseProgression
                    old_progression = IsThisExerciseProgressionExist(old_progressions,current_exercise.getExercise().getId());


            if (old_progression != null){
                progressions.add(getComparationReady(old_progression,current_exercise));
            }else {
                progressions.add(checkImprovementsWithNoOldProgression(current_exercise));
            }


        }
        return progressions;
    }

    private ExerciseProgression getComparationReady(
            com.app.app.silverbarsapp.database_models.ExerciseProgression old_progression,
            ExerciseProgression exerciseProgression_ready){

        if (old_progression.getTotal_repetition() > 0) {
            return(repImprovement(old_progression.getRepetitions_done(), exerciseProgression_ready));
        } else {
            return(secondImprovement(old_progression.getSeconds_done(), exerciseProgression_ready));
        }

    }

    public ExerciseProgression getComparationReady(ExerciseProgression old_progression,ExerciseProgression current_exercise){

        ExerciseProgression exerciseProgression_ready = getExerciseProgressionReady(current_exercise);


        if (old_progression.getTotal_repetition() > 0) {
            return(repImprovement(old_progression.getRepetitions_done(), exerciseProgression_ready));
        } else {
            return(secondImprovement(old_progression.getSeconds_done(), exerciseProgression_ready));
        }
    }


    private com.app.app.silverbarsapp.database_models.ExerciseProgression IsThisExerciseProgressionExist(
            List<com.app.app.silverbarsapp.database_models.ExerciseProgression> old_progressions_exercises,
            int exercise_id){
        for (com.app.app.silverbarsapp.database_models.ExerciseProgression old_last_progression : old_progressions_exercises) {
            if ((old_last_progression.getExercise().getId() == exercise_id)){
                return old_last_progression;
            }
        }
        return null;
    }

    public ArrayList<ExerciseProgression> addFirstProgressions(ArrayList<ExerciseProgression> current_exercises){
        Log.d(TAG,"there's any progression");
        ArrayList<ExerciseProgression> progressions = new ArrayList<>();
        for (ExerciseProgression  current_exercise: current_exercises) {
            progressions.add(checkImprovementsWithNoOldProgression(current_exercise));
        }
        return progressions;
    }

    public ExerciseProgression checkImprovementsWithNoOldProgression(ExerciseProgression current_exercise){
        ExerciseProgression exerciseProgression_ready = getExerciseProgressionReady(current_exercise);

        if (exerciseProgression_ready.getRepetitions_done() > 0 || exerciseProgression_ready.getSeconds_done() > 0) {
            current_exercise.setProgress(100);
            exerciseProgression_ready.setPositive(true);
        }else {
            current_exercise.setProgress(getProgress(0,0));
            exerciseProgression_ready.setPositive(false);
        }
        return exerciseProgression_ready;
    }

    private ExerciseProgression getExerciseProgressionReady(ExerciseProgression current_exercise){
        current_exercise.setExercise(current_exercise.getExercise());
        current_exercise.setTotal_repetition(current_exercise.getTotal_repetition());
        current_exercise.setRepetitions_done(current_exercise.getRepetitions_done());
        current_exercise.setTotal_seconds(current_exercise.getTotal_seconds());
        current_exercise.setSeconds_done(current_exercise.getSeconds_done());
        return current_exercise;
    }

    private ExerciseProgression secondImprovement(int old_progression_seconds_done, ExerciseProgression current_exercise){
        Log.d(TAG,"secondImprovement ");
        if (current_exercise.getSeconds_done()  > old_progression_seconds_done){

            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),old_progression_seconds_done));
            current_exercise.setPositive(true);

        }else if(current_exercise.getSeconds_done() == old_progression_seconds_done) {
            current_exercise.setEqual(true);
        }else {
            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),old_progression_seconds_done));
            current_exercise.setPositive(false);
        }
        return current_exercise;
    }

    private ExerciseProgression repImprovement(int old_progression_reps_done, ExerciseProgression current_exercise){
        Log.d(TAG,"repImprovement ");

        if (current_exercise.getRepetitions_done()  > old_progression_reps_done){
            Log.d(TAG,current_exercise.getExercise().getExercise_name()+" rep mejoro ");

            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),old_progression_reps_done));
            current_exercise.setPositive(true);

        }else if(current_exercise.getRepetitions_done() == old_progression_reps_done) {
            current_exercise.setProgress(0);
            current_exercise.setEqual(true);
        }else {
            Log.d(TAG,current_exercise.getExercise().getExercise_name()+" rep bajo ");

            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),old_progression_reps_done));
            current_exercise.setPositive(false);
        }
        return current_exercise;
    }


    private double getProgress(int new_rep,int old_rep){
        Log.d(TAG,"new_rep "+new_rep+" "+"old_rep "+old_rep);
        double progress = (( new_rep - old_rep)  / (double) old_rep)*100;
        Log.d(TAG,"progress "+progress);
        return progress;
    }



}
