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


    public void insertWorkout(com.app.proj.silverbars.models.Workout workout, CreateWorkoutFinalCallback callback)  {

        try {


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

                Exercise exercise = new Exercise(
                        exerciseRep.getExercise().getId(),
                        exerciseRep.getExercise().getExercise_name(),
                        exerciseRep.getExercise().getLevel(),
                        exerciseRep.getExercise().getExercise_audio(),
                        exerciseRep.getExercise().getExercise_image()
                );

                //exercise created in database
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
                helper.getExerciseRepDao().create(new ExerciseRep(exercise, exerciseRep.getRepetition(),user_workout));
            }


            callback.onWorkoutCreated(true);




        }catch (SQLException e){
            Log.e(TAG,"SQLException",e);
            callback.onWorkoutError();
        }



    }



}
