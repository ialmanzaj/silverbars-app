package com.app.app.silverbarsapp.handlers;

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

    public MuscleExercise getMuscleExercise(String muscle, ArrayList<ExerciseProgression> current_exercises){
        for (MuscleExercise muscleExercise: getMusclesFromExerciseProgression(current_exercises)){
            if (Objects.equals(muscleExercise.getMuscle(), muscle)){
                return muscleExercise;
            }
        }
        return null;
    }

    public List<MuscleExercise> getMusclesFromExerciseProgression(ArrayList<ExerciseProgression> exercises){
        List<MuscleExercise> muscles = new ArrayList<>();
        for (ExerciseProgression exerciseProgression: exercises){
            for (MuscleExercise muscleExercise: exerciseProgression.getExercise().getMuscles()){
                muscles.add(muscleExercise);
            }
        }

        return muscles;
    }

    public ArrayList<ExerciseProgression> getLastProgressions(List<ExerciseProgression> exercises_selected,List<ExerciseProgression> progressions){
        ArrayList<ExerciseProgression> last_exercisesProgressions = new ArrayList<>();
        for (ExerciseProgression exercise_selected:exercises_selected){
            List<ExerciseProgression> exercises_found = filterProgressionByExercise(exercise_selected.getExercise().getId(),progressions);
            if (exercises_found.size() > 2){
                last_exercisesProgressions.add(exercises_found.get(exercises_found.size()-2));
            }
        }
        return last_exercisesProgressions;
    }

    public List<String> getMusclesStringFromExerciseProgression(ArrayList<ExerciseProgression> exercises){
        List<String> muscles = new ArrayList<>();
        for (ExerciseProgression exerciseProgression: exercises){
            for (MuscleExercise muscleExercise: exerciseProgression.getExercise().getMuscles()){
                muscles.add(muscleExercise.getMuscle());
            }
        }

        return muscles;
    }

    public ArrayList<Exercise> getExercisesById(List<Exercise> all_exercises_list, List<Integer> exercises_id){
        ArrayList<Exercise> exerciseList = new ArrayList<>();
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


    public ArrayList<ExerciseProgression> filterProgressionByExercise(int exercise_id,List<ExerciseProgression> old_progressions_exercises){
        ArrayList<ExerciseProgression> progressions = new ArrayList<>();
        for (ExerciseProgression old_last_progression : old_progressions_exercises) {
            if (old_last_progression.getExercise().getId() == exercise_id){
                progressions.add(old_last_progression); ;
            }
        }
        return progressions;
    }




    public ArrayList<Exercise> filterExerciseByMuscle(ArrayList<String> muscles_selected, ArrayList<Exercise> exercises){
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

            DateTime progress_date = getDateFormated(exerciseProgression.getDate());

            if (filterByDate(progress_date,interval_days)){
                progressions_filted.add(exerciseProgression);
            }
        }

        return progressions_filted;
    }

    public DateTime getDateFormated(String date){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        return formatter.parseDateTime(date);
    }

    public String formatDayTime(DateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        return formatter.print(dateTime);
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
