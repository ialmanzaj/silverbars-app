package com.app.app.silverbarsapp;

import android.util.Log;

import com.app.app.silverbarsapp.models.Exercise;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.models.Workout;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by isaacalmanza on 04/14/17.
 */

public class Filter {

    private static final String TAG = Filter.class.getSimpleName();


    public Filter(){}


    public List<Exercise> getExercisesById(List<Exercise> all_exercises_list, List<Integer> exercises_id){
        List<Exercise> exerciseList = new ArrayList<>();
        for (Integer exercise_id: exercises_id){
            exerciseList.add(getExerciseById(all_exercises_list,exercise_id));
        }
        return exerciseList;
    }



    public List<String> getMusclesFromExercises(List<Exercise> exercises){
        List<String> muscles_names = new ArrayList<>();
        for(Exercise exercise:exercises){
            //add muscles array
            for (MuscleExercise muscle: exercise.getMuscles()){
                muscles_names.add(muscle.getMuscle());
            }
        }
        return muscles_names;
    }

    public ArrayList<Integer> getExercisesIds(ArrayList<Exercise> exercises){
        ArrayList<Integer> exercises_ids = new ArrayList<>();

        for (int a = 0;a<exercises.size();a++){
            exercises_ids.add(exercises.get(a).getId());
        }
        return exercises_ids;
    }

    public ArrayList<Integer> getExercisesRepsIds(ArrayList<ExerciseRep> exercises){
        ArrayList<Integer> exercises_ids = new ArrayList<>();

        for (int a = 0;a<exercises.size();a++){
            exercises_ids.add(exercises.get(a).getExercise().getId());
        }
        return exercises_ids;
    }

    public Workout getWorkoutById(List<Workout> workouts, int workout_id){
        for (Workout workout: workouts){
            if (workout.getId() == workout_id){
                return workout;
            }
        }
        return null;
    }


    public Exercise getExerciseById(List<Exercise> exercises, int exercise_id){
        for (Exercise exercise: exercises){
            if (exercise.getId() == exercise_id){
                return exercise;
            }
        }
        return null;
    }


    public ExerciseRep getExerciseRepById(List<ExerciseRep> exercises, int exercise_id){
        for (ExerciseRep exercise: exercises){
            if (exercise.getExercise().getId() == exercise_id){
                return exercise;
            }
        }
        return null;
    }


    public ArrayList<Exercise> getExercisesNoSelected(ArrayList<Integer> exercises_ids,ArrayList<Exercise> exercises){
        for (Integer exercise_id: exercises_ids){
            exercises.remove(getExerciseById(exercises,exercise_id));
        }
        return exercises;
    }


    public List<ExerciseProgression> filterProgressionByExercise(int exercise_id,List<ExerciseProgression> old_progressions_exercises){
        List<ExerciseProgression> progressions = new ArrayList<>();
        for (ExerciseProgression old_last_progression : old_progressions_exercises) {
            if (old_last_progression.getExercise().getId() == exercise_id){
                progressions.add(old_last_progression); ;
            }
        }
        return progressions;
    }




    public ArrayList<Exercise> filterExerciseByMuscle(ArrayList<String> muscles_selected, ArrayList<Exercise> exercises){
        Log.d(TAG,"muscles_selected: "+muscles_selected);
        ArrayList<Exercise> exercises_filtered_muscles = new ArrayList<>();
        for (Exercise exercise: exercises){
            for (String muscle_name: muscles_selected){
                for (MuscleExercise muscle: exercise.getMuscles()){
                    if (Objects.equals(muscle_name, muscle.getMuscle())){
                        if (!exercises_filtered_muscles.contains(exercise)) {
                            exercises_filtered_muscles.add(exercise);
                        }
                    }
                }
            }
        }

        return exercises_filtered_muscles;
    }

    public ArrayList<ExerciseProgression> getProgressionFiltered(List<ExerciseProgression> progressions, Interval interval_days){

        ArrayList<ExerciseProgression> progressions_filted = new ArrayList<>();

        for (ExerciseProgression exerciseProgression: progressions){

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTime progress_date = formatter.parseDateTime(exerciseProgression.getDate());

            if (filterByDate(progress_date,interval_days)){
                progressions_filted.add(exerciseProgression);
            }
        }

        return progressions_filted;
    }

    public ArrayList<ExerciseProgression> getProgressionFilteredByMuscle(List<ExerciseProgression> progressions,String muscle){

        ArrayList<ExerciseProgression> progressions_filted = new ArrayList<>();

        for (ExerciseProgression exerciseProgression: progressions){
            for (MuscleExercise muscle_exercise: exerciseProgression.getExercise().getMuscles()) {
                if (Objects.equals(muscle_exercise.getMuscle(), muscle)){
                    progressions_filted.add(exerciseProgression);
                }
            }


        }
        return progressions_filted;
    }

    public boolean filterByDate(DateTime day, Interval date_interval){
        return date_interval.contains(day);
    }
}
