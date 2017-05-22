package com.app.app.silverbarsapp.handlers;

import android.content.Context;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseProgressionCompared;
import com.app.app.silverbarsapp.models.MuscleActivationCompared;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.utils.Utilities;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by isaacalmanza on 04/18/17.
 */

public class ProgressionAlgoritm {

    private static final String TAG = ProgressionAlgoritm.class.getSimpleName();

    private Filter filter;
    private Utilities utilities;
    private Context context;

    public ProgressionAlgoritm(Context context){
        this.context = context;
        filter =  new Filter();
        utilities = new Utilities();
    }

    public ArrayList<ExerciseProgressionCompared> addFirstProgressions(ArrayList<ExerciseProgression> progressions_newer){
        ArrayList<ExerciseProgressionCompared> progressions = new ArrayList<>();
        for (ExerciseProgression  current_progression: progressions_newer) {

            progressions.add(
                    checkImprovementsWithNoOldProgression(
                            new ExerciseProgressionCompared(
                                    null,
                                    current_progression,
                                    getPeriodDaily()
                            )
                    ));
        }

        return progressions;
    }



    public ArrayList<ExerciseProgressionCompared> getProgressionComparedDaily(ArrayList<ExerciseProgression> last_progressions_old,ArrayList<ExerciseProgression> current_progressions){
        //array of the progression compared
        ArrayList<ExerciseProgressionCompared> progressions = new ArrayList<>();

        for (ExerciseProgression current_progression: current_progressions){

            //get the exercise id by progression
            int exercise_id = current_progression.getExercise().getId();

            ArrayList<ExerciseProgression> last_old_progressions_by_exercise = filter.filterProgressionByExercise(exercise_id,last_progressions_old);

            //progression compared to the list
            ExerciseProgressionCompared compared;

            if (last_old_progressions_by_exercise.size() > 0) {

                ExerciseProgression last_old_progression = last_old_progressions_by_exercise.get(last_old_progressions_by_exercise.size()-1);

                compared = getComparationReady(
                        new ExerciseProgressionCompared(
                                last_old_progression,
                                current_progression,
                                getPeriodDaily(
                                        filter.getDateFormated(last_old_progression.getDate()),
                                        filter.getDateFormated(current_progression.getDate())
                                )
                        )
                 );

            }else {

                compared = checkImprovementsWithNoOldProgression(
                        new ExerciseProgressionCompared(
                                null,
                                current_progression,
                                getPeriodDaily()
                        )
                );
            }

            if (!filter.existExercise(progressions,exercise_id)){
                progressions.add(compared);
            }
        }
        return progressions;
    }


    public ArrayList<ExerciseProgressionCompared> getProgressionComparedWeekly(
            ArrayList<ExerciseProgression> progressions_last_week,ArrayList<ExerciseProgression> progressions_current_week){

        //array of the progression compared
        ArrayList<ExerciseProgressionCompared> progressions = new ArrayList<>();

        for (ExerciseProgression current_week_progression: progressions_current_week){

            //get the exercise id by progression
            int exercise_id = current_week_progression.getExercise().getId();

            //get all the acumulative old progression of this exercise
            ExerciseProgression last_week_progressions_acumulative = getAcumulativeSameExerciseProgressions(
                    filter.filterProgressionByExercise(exercise_id,progressions_last_week));

            //get all the acumulative of the current progressions of this exercise
            ExerciseProgression current_week_progressions_acumulative = getAcumulativeSameExerciseProgressions(
                    filter.filterProgressionByExercise(exercise_id,progressions_current_week));

            //progression compared to the list
            ExerciseProgressionCompared compared;

            if (last_week_progressions_acumulative != null) {

                compared = getComparationReady(
                        new ExerciseProgressionCompared(
                                last_week_progressions_acumulative,
                                current_week_progressions_acumulative,
                                getPeriodWeekly()
                        )
                );

            }else {

                compared = checkImprovementsWithNoOldProgression(
                        new ExerciseProgressionCompared(
                                null,
                                current_week_progressions_acumulative,
                                getPeriodWeekly()
                        )
                );
            }

            if (!filter.existExercise(progressions,exercise_id)){
                progressions.add(compared);
            }
        }
        return progressions;
    }


    public ArrayList<ExerciseProgressionCompared> getProgressionComparedMonthly
            (ArrayList<ExerciseProgression> progressions_last_month,ArrayList<ExerciseProgression> progressions_current_month){
        ArrayList<ExerciseProgressionCompared> progressions = new ArrayList<>();

        for (ExerciseProgression current_month_progression: progressions_current_month){

            //get the exercise id by progression
            int exercise_id = current_month_progression.getExercise().getId();

            ExerciseProgression last_month_progressions_acumulative = getAcumulativeSameExerciseProgressions(
                    filter.filterProgressionByExercise(exercise_id,progressions_last_month));

            ExerciseProgression current_month_progressions_acumulative = getAcumulativeSameExerciseProgressions(
                    filter.filterProgressionByExercise(exercise_id,progressions_current_month));

            //progression compared to the list
            ExerciseProgressionCompared compared;


            if (last_month_progressions_acumulative != null) {

                compared = getComparationReady(
                        new ExerciseProgressionCompared(
                                last_month_progressions_acumulative,
                                current_month_progressions_acumulative,
                                getPeriodMonthly()
                        )
                );

            }else {

                compared = checkImprovementsWithNoOldProgression(
                        new ExerciseProgressionCompared(
                                null,
                                current_month_progressions_acumulative,
                                getPeriodMonthly()
                        )
                );
            }

            if (!filter.existExercise(progressions,exercise_id)){
                progressions.add(compared);
            }
        }
        return progressions;
    }

    private String getPeriodDaily(DateTime date_progression_old,DateTime date_progression_newer){
        Period period = new Period(date_progression_old, date_progression_newer);
        return String.valueOf(period.getDays())+context.getString(R.string.progression_algoritm_days);
    }

    private String getPeriodDaily(){
        return context.getString(R.string.progression_algoritm_last_time);
    }

    private String getPeriodWeekly(){
        return context.getString(R.string.progression_algoritm_week)
                +" "+
                context.getString(R.string.progression_algoritm_before);
    }

    private String getPeriodMonthly(){
        return context.getString(R.string.progression_algoritm_month)
                +" " +
                context.getString(R.string.progression_algoritm_before);
    }


    private ExerciseProgressionCompared getComparationReady(ExerciseProgressionCompared progressionCompared){
          if (progressionCompared.getExerciseProgression_old().getTotal_repetition() > 0) {

              ExerciseProgressionCompared rep_exerciseProgression = repImprovement(progressionCompared);

            if (progressionCompared.isRepImprove() && progressionCompared.isPositive()){
                return weightImprovement(progressionCompared);
            }

            return rep_exerciseProgression;

        } else {

              ExerciseProgressionCompared second_exerciseProgression = secondImprovement(progressionCompared);

              if (progressionCompared.isSecondImprove() && progressionCompared.isPositive() ){
                  return weightImprovement(progressionCompared);
              }

              return second_exerciseProgression;
        }
    }


    private ExerciseProgressionCompared checkImprovementsWithNoOldProgression(ExerciseProgressionCompared progressionCompared){

        checkWetherIsRepOrSecondBetter(progressionCompared);

        if (progressionCompared.getExerciseProgression_newer().getRepetitions_done() > 0 ||
                progressionCompared.getExerciseProgression_newer().getSeconds_done() > 0) {

            progressionCompared.setProgress(100);
            progressionCompared.setPositive(true);

        }else {
            progressionCompared.setProgress(0);
            progressionCompared.setNegative(true);

        }

        return progressionCompared;
    }


    private ExerciseProgressionCompared checkWetherIsRepOrSecondBetter(ExerciseProgressionCompared progressionCompared){
        if (progressionCompared.getExerciseProgression_newer().getTotal_repetition() > 0){
            //Log.d(TAG,"setRepImprove ");
            progressionCompared.setRepImprove(true);
        }else {
            //Log.d(TAG,"setSecondImprove ");
            progressionCompared.setSecondImprove(true);
        }
        return progressionCompared;
    }



    private ExerciseProgressionCompared weightImprovement(ExerciseProgressionCompared progressionCompared){
        //Log.d(TAG,current_exercise.getTotal_weight() + " : "+old_progression.getTotal_weight());

        ExerciseProgression exerciseProgression_newer = progressionCompared.getExerciseProgression_newer();
        ExerciseProgression exerciseProgression_old = progressionCompared.getExerciseProgression_old();

        double progress = getProgress(exerciseProgression_newer.getTotal_weight(),exerciseProgression_old.getTotal_weight());


        if (exerciseProgression_newer.getTotal_weight()  > exerciseProgression_old.getTotal_weight()){
            progressionCompared.setWeightImprove(true);
            //Log.d(TAG,"weightImprovement: Positive");

            progressionCompared.setProgress(progress);
            progressionCompared.setPositive(true);

        }else if (exerciseProgression_newer.getTotal_weight() < exerciseProgression_old.getTotal_weight() ) {
            progressionCompared.setWeightImprove(true);
            //Log.d(TAG,"weightImprovement: negative");


            progressionCompared.setProgress(progress);
            progressionCompared.setNegative(true);
        }

        return progressionCompared;
    }


    private ExerciseProgressionCompared secondImprovement(ExerciseProgressionCompared progressionCompared){
        //Log.d(TAG,"secondImprovement ");
        progressionCompared.setSecondImprove(true);

        int  seconds_exerciseProgression_newer = progressionCompared.getExerciseProgression_newer().getSeconds_done();
        int  seconds_done_exerciseProgression_old = progressionCompared.getExerciseProgression_old().getSeconds_done();

        double progress = getProgress(seconds_exerciseProgression_newer,seconds_done_exerciseProgression_old);


        if (seconds_exerciseProgression_newer  > seconds_done_exerciseProgression_old ){

            progressionCompared.setProgress(progress);
            progressionCompared.setPositive(true);

        }else if(seconds_exerciseProgression_newer == seconds_done_exerciseProgression_old ) {
            progressionCompared.setEqual(true);
        }else {
            progressionCompared.setProgress(progress);
            progressionCompared.setNegative(true);
        }

        return progressionCompared;
    }

    private ExerciseProgressionCompared repImprovement(ExerciseProgressionCompared progressionCompared){
        progressionCompared.setRepImprove(true);

        ExerciseProgression exerciseProgression_newer = progressionCompared.getExerciseProgression_newer();
        ExerciseProgression exerciseProgression_old = progressionCompared.getExerciseProgression_old();

        double progress = getProgress(exerciseProgression_newer.getRepetitions_done(),exerciseProgression_old.getRepetitions_done());


        if (exerciseProgression_newer.getRepetitions_done()  > exerciseProgression_old.getRepetitions_done()){

            progressionCompared.setProgress(progress);

            progressionCompared.setPositive(true);

        }else if(exerciseProgression_newer.getRepetitions_done() == exerciseProgression_old.getRepetitions_done()) {

            progressionCompared.setProgress(0);
            progressionCompared.setEqual(true);

        }else {

            progressionCompared.setProgress(progress);
            progressionCompared.setNegative(true);
        }

        return progressionCompared;
    }


    public double getProgress(int new_rep,int old_rep){
        if (old_rep == 0) return 100;
        //Log.d(TAG,"new: "+new_rep+" "+"old:  "+old_rep);
        double progress = (( new_rep - old_rep)  / (double) old_rep)*100;
        //Log.d(TAG,"progress "+progress);
        return progress;
    }


    private double getProgress(double new_weight,double old_weight){
        //Log.d(TAG,"new_weight "+new_weight+" "+"old_weight "+old_weight);
        double progress = (( new_weight - old_weight)  / old_weight)*100;
        //Log.d(TAG,"progress "+progress);
        return progress;
    }




    public ArrayList<ExerciseProgression> getListOfAllBestProgression(ArrayList<ExerciseProgression> progressions){
        ArrayList<ExerciseProgression> progressions_best = new ArrayList<>();

        ExerciseProgression exerciseProgression_list = new ExerciseProgression();

        for (ExerciseProgression exerciseProgression: progressions){

            List<ExerciseProgression> same_exercises = filter.filterProgressionByExercise(
                    exerciseProgression.getExercise().getId(),progressions);

            if (!progressions_best.contains(exerciseProgression_list)) {
                if (same_exercises.size() > 1) {
                    //Log.d(TAG, "same_exercises: " +exerciseProgression.getExercise().getExercise_name() +" : "+ same_exercises.size() );

                    //Log.d(TAG, "name: " + exerciseProgression_best.getExercise().getExercise_name());
                    //Log.d(TAG, "weight: " + exerciseProgression_best.getTotal_weight());
                    //Log.d(TAG, "rep: " + exerciseProgression_best.getTotal_repetition());
                    //Log.d(TAG, "second: " + exerciseProgression_best.getTotal_seconds());

                    exerciseProgression_list = getBestProgressions(same_exercises);
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


    public ExerciseProgression getAcumulativeSameExerciseProgressions(List<ExerciseProgression> progressions){
        if (progressions.size() > 0) {
            ExerciseProgression exerciseProgression = progressions.get(0);
            int acumulative = 0;
            double weight_acumulative = 0;

            for (ExerciseProgression progression: progressions){
                 if (progression.getTotal_repetition() > 0){

                     acumulative = acumulative+progression.getRepetitions_done();
                     exerciseProgression.setRepetitions_done(acumulative);

                 }else {

                     acumulative = acumulative+progression.getSeconds_done();
                     exerciseProgression.setRepetitions_done(acumulative);
                 }

                weight_acumulative = weight_acumulative+progression.getTotal_weight();
                exerciseProgression.setTotal_weight(weight_acumulative);
            }

            return exerciseProgression;
        }
        return null;
    }

    private ExerciseProgression bestProgression(ExerciseProgression progression1,ExerciseProgression progression2){
        if (progression1.getExercise().getId() == progression2.getExercise().getId()){

            if (progression1.getTotal_weight() > progression2.getTotal_weight()){

                //Log.d(TAG,"weight better: "+ progression1.getTotal_weight()+ ":"+ progression2.getTotal_weight());
                return progression1;
            }else {

                if (useRepOrSecond(progression1)) {

                    //Log.d(TAG,"rep better: "+ progression1.getRepetitions_done() + ":"+ progression2.getRepetitions_done() );
                    if (progression1.getRepetitions_done() > progression2.getRepetitions_done()) return progression1;else return progression2;

                } else {

                    //Log.d(TAG,"second better: "+ progression1.getSeconds_done() + ":"+ progression2.getSeconds_done() );
                    if (progression1.getSeconds_done() > progression2.getSeconds_done() ) return progression1;else return progression2;
                }
            }
        }
        return null;
    }

    public int getMuscleActivationAverage(String current_muscle, ArrayList<ExerciseProgression> exercises){
        int muscle_activation = getActivationTotal(current_muscle,exercises);
        int counter = utilities.counter(filter.getMusclesStringFromExerciseProgression(exercises),current_muscle);

        //Log.d(TAG,"muscle_activation: "+muscle_activation);
        //Log.d(TAG,"counter "+counter);
        return counter > 0 ? muscle_activation / counter: 0;
    }

    public int getMuscleActivationAverage(String current_muscle, ArrayList<ExerciseProgression> exercises,int muscle_activation){
        int counter = utilities.counter(filter.getMusclesStringFromExerciseProgression(exercises),current_muscle);

        //Log.d(TAG,"muscle_activation: "+muscle_activation);
        //Log.d(TAG,"counter "+counter);
        return counter > 0 ? muscle_activation / counter: 0;
    }

    private int getActivationTotal(String current_muscle, ArrayList<ExerciseProgression> exercises){
        int muscle_activation = 0;
        for (MuscleExercise muscle_exercise: filter.getMusclesFromExerciseProgression(exercises)) {
            if (Objects.equals(muscle_exercise.getMuscle(), current_muscle)) {
                muscle_activation = muscle_activation + muscle_exercise.getMuscle_activation();
            }
        }
        return muscle_activation;
    }


    public MuscleActivationCompared getMuscleActivationProgression(
            String current_muscle, ArrayList<ExerciseProgression> old_progressions, ArrayList<ExerciseProgression> progressions_newer){

        int old_muscle_activation = getActivationTotal(current_muscle,old_progressions);
        int current_muscle_activation = getActivationTotal(current_muscle,progressions_newer);

        MuscleActivationCompared muscleActivation =
                new MuscleActivationCompared(old_muscle_activation,current_muscle_activation,current_muscle);

        if (current_muscle_activation > old_muscle_activation){
            muscleActivation.setPositive(true);
        }else if (current_muscle_activation == old_muscle_activation){
            muscleActivation.setEqual(true);
        } else {
            muscleActivation.setNegative(true);
        }

        muscleActivation.setMuscle_activation_average(
                getMuscleActivationAverage(current_muscle,progressions_newer,current_muscle_activation));

        muscleActivation.setProgress(
                getProgress(current_muscle_activation,old_muscle_activation));

        return muscleActivation;
    }

    public ArrayList<MuscleActivationCompared> getMusclesActivationByExercisesProgressions(ArrayList<ExerciseProgression> current_exercises){
        ArrayList<MuscleActivationCompared> muscleActivations = new ArrayList<>();
        for ( String muscle: utilities.deleteCopiesofList(filter.getMusclesStringFromExerciseProgression(current_exercises))){

            MuscleActivationCompared muscleActivation = new MuscleActivationCompared();
            muscleActivation.setMuscle_name(muscle);
            muscleActivation.setMuscle_activation_average(filter.getMuscleExercise(muscle, current_exercises).getMuscle_activation());

            //Log.d(TAG,"muscleActivation "+muscleActivation.getMuscle_activation());
            muscleActivations.add(muscleActivation);
        }
        return muscleActivations;
    }


  /*  public MuscleActivation getFinalMuscleActivation(MuscleActivation muscleActivation, ArrayList<ExerciseProgression> progressions_compared, ){
        for (ExerciseProgression progression: progressions_compared){
        }

        return muscleActivation;
    }
*/





}
