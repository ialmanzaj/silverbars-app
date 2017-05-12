package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.callbacks.SavedWorkoutsCallback;
import com.app.app.silverbarsapp.database_models.MySavedWorkout;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by isaacalmanza on 02/10/17.
 */

public class SavedWorkoutsInteractor {

    private static final String TAG = SavedWorkoutsInteractor.class.getSimpleName();

    private DatabaseHelper helper;

    public SavedWorkoutsInteractor(DatabaseHelper helper){
        this.helper = helper;
    }

    public void getWorkout(SavedWorkoutsCallback callback) throws SQLException {
        if (helper.getSavedWorkoutDao().queryForAll() == null || helper.getSavedWorkoutDao().queryForAll().isEmpty()){
            callback.onEmptyWorkouts();

        }else {
            if (checkIsAnySavedWorkout()){
                getSavedWorkouts(callback);
            }else
                callback.onEmptyWorkouts();
        }
    }

    private void getSavedWorkouts(SavedWorkoutsCallback callback) throws SQLException {

       /* List<Workout> saved_workouts = new ArrayList<>();

        for (MySavedWorkout my_saved_workout:  helper.getSavedWorkoutDao().queryForAll()){

            ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> exerciseReps = new ArrayList<>();

            for (ExerciseRep exerciseRep_database: my_saved_workout.getExercises()){


                Exercise exercise_database = helper.getExerciseDao().queryForId(exerciseRep_database.getExercise().getId());

                //types and muscles list
                List<String> types_exercise = new ArrayList<>();
                List<com.app.app.silverbarsapp.models.MuscleExercise> muscles = new ArrayList<>();


                //get the type exercise_database from table to new object in json
                for (TypeExercise types:  exercise_database.getType_exercise()){types_exercise.add(types.getType());}


                //get the muscles
                for (Muscle muscle: exercise_database.getMusclesFromExerciseProgression()){
                    muscles.add(new com.app.app.silverbarsapp.models.MuscleExercise(
                            muscle.getMuscle(),
                            muscle.getMuscle_activation(), muscle.getClassification(),
                            muscle.getProgression_level()));
                }

                //re-create exercise object from database
                com.app.app.silverbarsapp.models.Exercise exercise = new com.app.app.silverbarsapp.models.Exercise(
                                exercise_database.getId(),
                                exercise_database.getExercise_name(),
                                exercise_database.getLevel(),
                                types_exercise,
                                exercise_database.getExercise_audio(),
                                exercise_database.getExercise_image(),
                                muscles
                                );

                //exercise rep - seconds
                exerciseReps.add(
                        new com.app.app.silverbarsapp.models.ExerciseRep(
                                exerciseRep_database.getId(),
                                exercise,
                                exerciseRep_database.getRepetition(),
                                exerciseRep_database.getSeconds()
                        ));
            }


            //add my_saved_workout completed to list
            if (my_saved_workout.getSaved()){
                saved_workouts.add(new com.app.app.silverbarsapp.models.Workout(
                        my_saved_workout.getId(),
                        my_saved_workout.getWorkout_name(),
                        my_saved_workout.getWorkout_image(),
                        my_saved_workout.getSets(),
                        my_saved_workout.getLevel(),
                        my_saved_workout.getMain_muscle(),
                        exerciseReps
                ));
            }
        }

        //return saved_workouts_database list saved
        callback.onWorkouts(saved_workouts);*/
    }



    private boolean checkIsAnySavedWorkout() throws SQLException {
        for (MySavedWorkout my_saved_workout: helper.getSavedWorkoutDao().queryForAll()){
            if (my_saved_workout.getSaved()){
                return true;
            }
        }
        return false;
    }


}
