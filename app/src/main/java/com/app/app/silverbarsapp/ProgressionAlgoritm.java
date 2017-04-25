package com.app.app.silverbarsapp;

import android.util.Log;

import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.MuscleActivation;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by isaacalmanza on 04/18/17.
 */

public class ProgressionAlgoritm {

    private static final String TAG = ProgressionAlgoritm.class.getSimpleName();

    private Filter filter = new Filter();

    public ProgressionAlgoritm(){}

    public ArrayList<ExerciseProgression> compareExerciseProgression(List<ExerciseProgression> old_progressions, ArrayList<ExerciseProgression> current_exercises){
        Filter filter = new Filter();
        ArrayList<ExerciseProgression> progressions = new ArrayList<>();
        for (ExerciseProgression current_exercise : current_exercises){

            ArrayList<ExerciseProgression> old_progressions_by_exercise = filter.filterProgressionByExercise(current_exercise.getExercise().getId(),old_progressions);

            if (old_progressions_by_exercise.size() > 0){
                progressions.add(
                        getComparationReady(
                                old_progressions_by_exercise.get(old_progressions_by_exercise.size()-1),
                                current_exercise)
                );
            }else {
                progressions.add(checkImprovementsWithNoOldProgression(current_exercise));
            }
        }
        return progressions;
    }

    private ExerciseProgression getComparationReady(ExerciseProgression old_progression,ExerciseProgression current_progression){
          if (old_progression.getTotal_repetition() > 0) {
              ExerciseProgression rep_exerciseProgression = repImprovement(old_progression.getRepetitions_done(), current_progression);

            if (rep_exerciseProgression.isRepImprove() && rep_exerciseProgression.isPositive()){
                return weightImprovement(old_progression,rep_exerciseProgression);
            };

            return rep_exerciseProgression;

        } else {
              ExerciseProgression second_exerciseProgression = secondImprovement(old_progression.getSeconds_done(), current_progression);

              if (second_exerciseProgression.isSecondImprove() && second_exerciseProgression.isPositive() ){
                  return weightImprovement(old_progression,second_exerciseProgression);
              }

              return second_exerciseProgression;
        }
    }

    public ArrayList<ExerciseProgression> addFirstProgressions(ArrayList<ExerciseProgression> current_exercises){
        Log.d(TAG,"there's any progression");
        ArrayList<ExerciseProgression> progressions = new ArrayList<>();
        for (ExerciseProgression  current_exercise: current_exercises) {
            progressions.add(checkImprovementsWithNoOldProgression(current_exercise));
        }
        return progressions;
    }

    private ExerciseProgression checkImprovementsWithNoOldProgression(ExerciseProgression exerciseProgression_ready){
        if (exerciseProgression_ready.getRepetitions_done() > 0 || exerciseProgression_ready.getSeconds_done() > 0) {
            exerciseProgression_ready.setProgress(100);
            exerciseProgression_ready.setPositive(true);
            checkWetherIsRepOrSecond(exerciseProgression_ready);
        }else {
            exerciseProgression_ready.setProgress(0);
            exerciseProgression_ready.setNegative(true);
            checkWetherIsRepOrSecond(exerciseProgression_ready);
        }
        return exerciseProgression_ready;
    }

    private ExerciseProgression checkWetherIsRepOrSecond(ExerciseProgression exerciseProgression_ready){
        if (exerciseProgression_ready.getTotal_repetition() > 0){
            exerciseProgression_ready.setRepImprove(true);
            Log.d(TAG,"setRepImprove ");
        }else {
            exerciseProgression_ready.setSecondImprove(true);
            Log.d(TAG,"setSecondImprove ");
        }
        return exerciseProgression_ready;
    }

    private ExerciseProgression weightImprovement(ExerciseProgression old_progression, ExerciseProgression current_exercise){
        Log.d(TAG,current_exercise.getTotal_weight() + " : "+old_progression.getTotal_weight());

        if (current_exercise.getTotal_weight()  > old_progression.getTotal_weight()){
            current_exercise.setWeightImprove(true);
            Log.d(TAG,"weightImprovement: Positive");

            current_exercise.setProgress(getProgress(current_exercise.getTotal_weight(), old_progression.getTotal_weight()));
            current_exercise.setPositive(true);

        }else if (current_exercise.getTotal_weight() < old_progression.getTotal_weight() ) {
            current_exercise.setWeightImprove(true);
            Log.d(TAG,"weightImprovement: negative");


            current_exercise.setProgress(getProgress(current_exercise.getTotal_weight(), old_progression.getTotal_weight()));
            current_exercise.setNegative(true);
        }
        return current_exercise;
    }


    private ExerciseProgression secondImprovement(int seconds_done_old_progression, ExerciseProgression current_exercise){
        Log.d(TAG,"secondImprovement ");
        current_exercise.setSecondImprove(true);

        if (current_exercise.getSeconds_done()  > seconds_done_old_progression){
            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),seconds_done_old_progression));
            current_exercise.setPositive(true);
        }else if(current_exercise.getSeconds_done() == seconds_done_old_progression) {
            current_exercise.setEqual(true);
        }else {
            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),seconds_done_old_progression));
            current_exercise.setNegative(true);
        }
        return current_exercise;
    }

    private ExerciseProgression repImprovement(int reps_done_old_progression, ExerciseProgression current_exercise){
        Log.d(TAG,"repImprovement ");
        current_exercise.setRepImprove(true);

        if (current_exercise.getRepetitions_done()  > reps_done_old_progression){
            Log.d(TAG,current_exercise.getExercise().getExercise_name()+" rep mejoro ");

            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),reps_done_old_progression));
            current_exercise.setPositive(true);

        }else if(current_exercise.getRepetitions_done() == reps_done_old_progression) {
            current_exercise.setProgress(0);
            current_exercise.setEqual(true);
        }else {
            Log.d(TAG,current_exercise.getExercise().getExercise_name()+" rep bajo ");

            current_exercise.setProgress(getProgress(current_exercise.getRepetitions_done(),reps_done_old_progression));
            current_exercise.setNegative(true);
        }

        return current_exercise;
    }


    public double getProgress(int new_rep,int old_rep){
        if (old_rep == 0) return 100;
        Log.d(TAG,"new: "+new_rep+" "+"old:  "+old_rep);
        double progress = (( new_rep - old_rep)  / (double) old_rep)*100;
        Log.d(TAG,"progress "+progress);
        return progress;
    }


    private double getProgress(double new_weight,double old_weight){
        Log.d(TAG,"new_weight "+new_weight+" "+"old_weight "+old_weight);
        double progress = (( new_weight - old_weight)  / old_weight)*100;
        Log.d(TAG,"progress "+progress);
        return progress;
    }

    public ArrayList<ExerciseProgression> compareWithOldProgressions(ArrayList<ExerciseProgression> current_week_progressions, ArrayList<ExerciseProgression> last_week_progressions){
        ArrayList<ExerciseProgression> progressions_results = new ArrayList<>();


        for (ExerciseProgression current_progression: current_week_progressions){

            ExerciseProgression best_old_progression_by_current_exercise = getBestProgressions(
                            filter.filterProgressionByExercise(current_progression.getExercise().getId(),last_week_progressions));

            if (best_old_progression_by_current_exercise != null) {

                progressions_results.add(getComparationReady(best_old_progression_by_current_exercise, current_progression));

            }else {
                progressions_results.add(checkImprovementsWithNoOldProgression(current_progression));
            }

        }

        return progressions_results;
    }



    public ArrayList<ExerciseProgression> getListOfAllBestProgression(ArrayList<ExerciseProgression> progressions){
        ArrayList<ExerciseProgression> progressions_best = new ArrayList<>();

        ExerciseProgression exerciseProgression_list = new ExerciseProgression();

        for (ExerciseProgression exerciseProgression: progressions){

            List<ExerciseProgression> same_exercises = filter.filterProgressionByExercise(
                    exerciseProgression.getExercise().getId(),progressions);

            if (!progressions_best.contains(exerciseProgression_list)) {
                if (same_exercises.size() > 1) {
                    Log.d(TAG, "same_exercises: " +exerciseProgression.getExercise().getExercise_name() +" : "+ same_exercises.size() );

                    ExerciseProgression exerciseProgression_best  = getBestProgressions(same_exercises);

                    Log.d(TAG, "exerciseProgression_best: " + exerciseProgression_best.getExercise().getExercise_name());
                    Log.d(TAG, "exerciseProgression_best: " + exerciseProgression_best.getTotal_weight());
                    Log.d(TAG, "exerciseProgression_best: " + exerciseProgression_best.getTotal_repetition());
                    Log.d(TAG, "exerciseProgression_best: " + exerciseProgression_best.getTotal_seconds());

                    exerciseProgression_list = exerciseProgression_best;
                    progressions_best.add(exerciseProgression);


                }else {
                    exerciseProgression_list = exerciseProgression;
                    progressions_best.add(exerciseProgression);
                }
            }
        }

        //Log.d(TAG,"progressions original "+progressions.size());
        //Log.d(TAG,"progressions best "+progressions_best.size());

        return progressions_best;
    }

    private boolean useRepOrSecond(ExerciseProgression exerciseProgression){
        return exerciseProgression.getTotal_repetition() > 0;
    }

    public ExerciseProgression getBestProgressions(List<ExerciseProgression> progressions){
        if (progressions.size() > 0) {
            ExerciseProgression best = progressions.get(0);
            for (ExerciseProgression old_progression : progressions) {
                best = bestProgression(best, old_progression);
            }
            return best;
        }
        return null;
    }

    private ExerciseProgression bestProgression(ExerciseProgression progression1,ExerciseProgression progression2){
        if (progression1.getExercise().getId() == progression2.getExercise().getId()){

            if (progression1.getTotal_weight() > progression2.getTotal_weight()){

                Log.d(TAG,"weight better: "+ progression1.getTotal_weight()+ ":"+ progression2.getTotal_weight());
                return progression1;
            }else {

                if (useRepOrSecond(progression1)) {

                    Log.d(TAG,"rep better: "+ progression1.getRepetitions_done() + ":"+ progression2.getRepetitions_done() );
                    if (progression1.getRepetitions_done() > progression2.getRepetitions_done()) return progression1;else return progression2;

                } else {

                    Log.d(TAG,"second better: "+ progression1.getSeconds_done() + ":"+ progression2.getSeconds_done() );
                    if (progression1.getSeconds_done() > progression2.getSeconds_done() ) return progression1;else return progression2;
                }
            }
        }
        return null;
    }

    public int getMuscleActivationAverage(String muscle, ArrayList<ExerciseProgression> exercises){
        int muscle_activation = getMuscleActivationTotal(muscle,exercises);
        int counter = new Utilities().counter(filter.getMusclesString(exercises),muscle);

        Log.d(TAG,"muscle_activation: "+muscle_activation);
        Log.d(TAG,"counter "+counter);
        return counter > 0 ? muscle_activation / counter: 0;
    }

    public int getMuscleActivationAverage(String muscle, ArrayList<ExerciseProgression> exercises,int muscle_activation){
        int counter = new Utilities().counter(filter.getMusclesString(exercises),muscle);

        Log.d(TAG,"muscle_activation: "+muscle_activation);
        Log.d(TAG,"counter "+counter);
        return counter > 0 ? muscle_activation / counter: 0;
    }

    private int getMuscleActivationTotal(String muscle, ArrayList<ExerciseProgression> exercises){
        int muscle_activation = 0;
        for (MuscleExercise muscle_exercise: filter.getMuscles(exercises)) {
            if (Objects.equals(muscle_exercise.getMuscle(), muscle)) {
                muscle_activation = muscle_activation + muscle_exercise.getMuscle_activation();
            }
        }
        return muscle_activation;
    }


    public MuscleActivation getMuscleActivation(String muscle, ArrayList<ExerciseProgression> current_progressions, ArrayList<ExerciseProgression> old_progressions){
        int current_muscle_activation = getMuscleActivationTotal(muscle,current_progressions);
        int old_muscle_activation = getMuscleActivationTotal(muscle,old_progressions);

        //Log.d(TAG,"current_muscle_activation: "+current_muscle_activation);
        //Log.d(TAG,"old_muscle_activation:  "+old_muscle_activation);

        MuscleActivation muscleActivation = new MuscleActivation();

        if (current_muscle_activation > old_muscle_activation){
            muscleActivation.setPositive(true);
        }else if (current_muscle_activation == old_muscle_activation){
            muscleActivation.setEqual(true);
        } else {
            muscleActivation.setNegative(true);
        }

        muscleActivation.setMuscle_activation(getMuscleActivationAverage(muscle,current_progressions,current_muscle_activation));
        muscleActivation.setPorcentaje(getProgress(current_muscle_activation,old_muscle_activation));

        return muscleActivation;
    }





}
