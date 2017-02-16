package com.app.proj.silverbars.interactors;

import com.app.proj.silverbars.callbacks.WorkoutCallback;
import com.app.proj.silverbars.database_models.Exercise;
import com.app.proj.silverbars.database_models.ExerciseRep;
import com.app.proj.silverbars.database_models.Muscle;
import com.app.proj.silverbars.database_models.MySavedWorkout;
import com.app.proj.silverbars.database_models.TypeExercise;
import com.app.proj.silverbars.utils.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkoutInteractor {

    private static final String TAG = WorkoutInteractor.class.getSimpleName();


    private DatabaseHelper helper;


    public WorkoutInteractor(DatabaseHelper helper){
        this.helper = helper;
    }


    public boolean isWorkoutExist(int workout_id) throws SQLException {
        return helper.getSavedWorkoutDao().queryForId(workout_id) != null;
    }


    public void saveWorkout(com.app.proj.silverbars.models.Workout workout, WorkoutCallback callback) throws SQLException {

        MySavedWorkout my_saved_workout = new MySavedWorkout(
                workout.getId(),
                workout.getWorkout_name(),
                workout.getWorkout_image(),
                workout.getSets(),
                workout.getLevel(),
                workout.getMainMuscle()
        );

        helper.getSavedWorkoutDao().create(my_saved_workout);
        //Log.d(TAG,"workouts size: "+helper.getSavedWorkoutDao().queryForAll().size());


        for (com.app.proj.silverbars.models.ExerciseRep exerciseRep: workout.getExercises()){

            Exercise exercise = new Exercise(
                    exerciseRep.getExercise().getId(),
                    exerciseRep.getExercise().getExercise_name(),
                    exerciseRep.getExercise().getLevel(),
                    exerciseRep.getExercise().getExercise_audio(),
                    exerciseRep.getExercise().getExercise_image()
            );

            //exercise created
            helper.getExerciseDao().create(exercise);


            //set types exercises to database
            for (String type: exerciseRep.getExercise().getType_exercise()){
                helper.getTypeDao().create(
                        new TypeExercise(
                                type,
                                exercise
                        ));
            }


            //set musles to database
            for (com.app.proj.silverbars.models.Muscle muscle: exerciseRep.getExercise().getMuscles()){
                helper.getMuscleDao().create(
                        new Muscle(
                                muscle.getMuscle(),
                        muscle.getMuscle_activation(),
                                muscle.getClassification(),
                                muscle.getProgression_level(),
                        exercise
                        ));
            }

            //re create exercise rep model
            helper.getExerciseRepDao().create(new ExerciseRep(exercise, exerciseRep.getRepetition(),my_saved_workout));
        }
        
        
        callback.onWorkout(true);
    }


   /* public void onDestroy(){
        if (helper != null) {
            OpenHelperManager.releaseHelper();
            helper = null;
        }
    }*/


}
