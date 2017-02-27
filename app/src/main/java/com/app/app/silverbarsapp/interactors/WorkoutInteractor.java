package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.callbacks.WorkoutCallback;
import com.app.app.silverbarsapp.database_models.Exercise;
import com.app.app.silverbarsapp.database_models.ExerciseRep;
import com.app.app.silverbarsapp.database_models.Muscle;
import com.app.app.silverbarsapp.database_models.MySavedWorkout;
import com.app.app.silverbarsapp.database_models.TypeExercise;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

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

    public boolean isWorkoutSaved(int workout_id) throws SQLException {
        return helper.getSavedWorkoutDao().queryForId(workout_id).getSaved();
    }


    public void setWorkoutOff(int workout_id) throws SQLException {
        helper.updateSavedWorkout(workout_id,false);
    }

    public void setWorkoutOn(int workout_id) throws SQLException {
        helper.updateSavedWorkout(workout_id,true);
    }

    public void saveWorkout(com.app.app.silverbarsapp.models.Workout workout, WorkoutCallback callback) throws SQLException {

        MySavedWorkout my_saved_workout = new MySavedWorkout(
                workout.getId(),
                workout.getWorkout_name(),
                workout.getWorkout_image(),
                workout.getSets(),
                workout.getLevel(),
                workout.getMainMuscle(),
                true
        );


        helper.getSavedWorkoutDao().create(my_saved_workout);
        //Log.d(TAG,"workouts size: "+helper.getSavedWorkoutDao().queryForAll().size());


        for (com.app.app.silverbarsapp.models.ExerciseRep exerciseRep: workout.getExercises()){

            Exercise exercise = insertExercise(exerciseRep);

            insertMuscles(exerciseRep,exercise);
            insertTypes(exerciseRep,exercise);

            //re create exercise rep model
            helper.getExerciseRepDao().create(new ExerciseRep(exercise, exerciseRep.getRepetition(),my_saved_workout));
        }
        
        
        callback.onWorkout(true);
    }


    private Exercise insertExercise(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep) throws SQLException {

        Exercise exercise = new Exercise(
                exerciseRep.getExercise().getExercise_name(),
                exerciseRep.getExercise().getLevel(),
                exerciseRep.getExercise().getExercise_audio(),
                exerciseRep.getExercise().getExercise_image()
        );

        //exercise created in database
        helper.getExerciseDao().create(exercise);



        return exercise;
    }




    private void insertMuscles(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set musles to database
        for (com.app.app.silverbarsapp.models.Muscle muscle: exerciseRep.getExercise().getMuscles()){
            helper.getMuscleDao().create(new Muscle(muscle.getMuscle(), muscle.getMuscle_activation(), muscle.getClassification(), muscle.getProgression_level(), exercise));
        }
    }



    private void insertTypes(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set types exercises to database
        for (String type: exerciseRep.getExercise().getType_exercise()){
            helper.getTypeDao().create(new TypeExercise(type, exercise));
        }
    }




   /* public void onDestroy(){
        if (helper != null) {
            OpenHelperManager.releaseHelper();
            helper = null;
        }
    }*/


}
