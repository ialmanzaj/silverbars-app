package com.app.app.silverbarsapp.interactors;

import android.util.Log;

import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.CreateWorkoutFinalCallback;
import com.app.app.silverbarsapp.database_models.Exercise;
import com.app.app.silverbarsapp.database_models.ExerciseRep;
import com.app.app.silverbarsapp.database_models.Muscle;
import com.app.app.silverbarsapp.database_models.TypeExercise;
import com.app.app.silverbarsapp.database_models.UserWorkout;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.sql.SQLException;

/**
 * Created by isaacalmanza on 01/12/17.
 */

public class CreateWorkoutFinalInteractor {

    private static final String TAG = CreateWorkoutFinalInteractor.class.getSimpleName();

    private DatabaseHelper helper;
    private MainService mainService;

    public CreateWorkoutFinalInteractor(DatabaseHelper helper, MainService mainService){
        this.helper = helper;
        this.mainService = mainService;
    }

    public void insertWorkout(com.app.app.silverbarsapp.models.Workout workout, CreateWorkoutFinalCallback callback) throws SQLException {

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

        for (com.app.app.silverbarsapp.models.ExerciseRep exerciseRep: workout.getExercises()){

            Exercise exercise = insertExercise(exerciseRep);
            insertTypesOfExercise(exerciseRep,exercise);
            insertMuscles(exerciseRep,exercise);

            //re create exercise rep model
            Log.d(TAG,"state: "+exerciseRep.getExercise_state());

            switch (exerciseRep.getExercise_state()){
                case REP:
                    Log.d(TAG,"rep:"+exerciseRep.getNumber());
                    helper.getExerciseRepDao().create(new ExerciseRep(exercise, exerciseRep.getNumber(),0,user_workout));
                    break;
                case SECOND:
                    Log.d(TAG,"second:"+exerciseRep.getNumber());
                    helper.getExerciseRepDao().create(new ExerciseRep(exercise,0,exerciseRep.getNumber(),user_workout));
                    break;
            }
        }

        //on workout created
        callback.onWorkoutCreated(true);
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

        //ruturn exercise created
        return exercise;
    }

    private void insertTypesOfExercise(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set types exercises to database
        for (String type: exerciseRep.getExercise().getType_exercise()){
            helper.getTypeDao().create(new TypeExercise(type, exercise));
        }
    }


    private void insertMuscles(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set musles to database
        for (com.app.app.silverbarsapp.models.MuscleExercise muscle: exerciseRep.getExercise().getMuscles()){
            helper.getMuscleDao().create(
                    new Muscle(muscle.getMuscle(), muscle.getMuscle_activation(), muscle.getClassification(), muscle.getProgression_level(), exercise));
        }
    }



}
