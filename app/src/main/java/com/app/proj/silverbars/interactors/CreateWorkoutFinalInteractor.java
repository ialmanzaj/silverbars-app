package com.app.proj.silverbars.interactors;

import android.util.Log;

import com.app.proj.silverbars.callbacks.CreateWorkoutFinalCallback;
import com.app.proj.silverbars.database_models.Exercise;
import com.app.proj.silverbars.database_models.ExerciseRep;
import com.app.proj.silverbars.database_models.Muscle;
import com.app.proj.silverbars.database_models.TypeExercise;
import com.app.proj.silverbars.database_models.UserWorkout;
import com.app.proj.silverbars.utils.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public class CreateWorkoutFinalInteractor {

    private static final String TAG = CreateWorkoutFinalInteractor.class.getSimpleName();

    private DatabaseHelper helper;

    public CreateWorkoutFinalInteractor(DatabaseHelper helper){
        this.helper = helper;
    }

    public void insertWorkout(com.app.proj.silverbars.models.Workout workout, CreateWorkoutFinalCallback callback) throws SQLException {

            UserWorkout user_workout = new UserWorkout(
                    workout.getWorkout_name(),
                    workout.getWorkout_image(),
                    workout.getSets(),
                    workout.getLevel(),
                    workout.getMainMuscle()
            );

            //create user workout
            helper.getUserWorkoutDao().create(user_workout);
            //Log.d(TAG,"workouts size: "+helper.getSavedWorkoutDao().queryForAll().size());

            for (com.app.proj.silverbars.models.ExerciseRep exerciseRep: workout.getExercises()){


                Exercise exercise = insertExercise(exerciseRep);
                insertTypesOfExercise(exerciseRep,exercise);
                insertMuscles(exerciseRep,exercise);

                //re create exercise rep model
                helper.getExerciseRepDao().create(new ExerciseRep(exercise, exerciseRep.getRepetition(),user_workout));
            }

            //on workout created
            callback.onWorkoutCreated(true);
    }

    private Exercise insertExercise(com.app.proj.silverbars.models.ExerciseRep exerciseRep) throws SQLException {

        Exercise exercise = new Exercise(
                exerciseRep.getExercise().getExercise_name(),
                exerciseRep.getExercise().getLevel(),
                exerciseRep.getExercise().getExercise_audio(),
                exerciseRep.getExercise().getExercise_image()
        );


        Log.i(TAG,"exercise :"+exerciseRep.getExercise().getId());
        Log.i(TAG,"exercise :"+exerciseRep.getExercise().getExercise_name());
        Log.i(TAG,"exercise :"+exerciseRep.getExercise().getLevel());
        Log.i(TAG,"exercise :"+exerciseRep.getExercise().getExercise_audio());
        Log.i(TAG,"exercise :"+exerciseRep.getExercise().getExercise_image());

        //exercise created in database
        helper.getExerciseDao().create(exercise);

        return exercise;
    }

    private void insertTypesOfExercise(com.app.proj.silverbars.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set types exercises to database
        for (String type: exerciseRep.getExercise().getType_exercise()){
            helper.getTypeDao().create(
                    new TypeExercise(type, exercise));
        }
    }


    private void insertMuscles(com.app.proj.silverbars.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set musles to database
        for (com.app.proj.silverbars.models.Muscle muscle: exerciseRep.getExercise().getMuscles()){
            helper.getMuscleDao().create(
                    new Muscle(muscle.getMuscle(), muscle.getMuscle_activation(), muscle.getClassification(), muscle.getProgression_level(), exercise));
        }
    }



}
