package com.app.proj.silverbars.interactors;

import android.util.Log;

import com.app.proj.silverbars.callbacks.SavedWorkoutsCallback;
import com.app.proj.silverbars.database_models.Exercise;
import com.app.proj.silverbars.database_models.ExerciseRep;
import com.app.proj.silverbars.database_models.Muscle;
import com.app.proj.silverbars.database_models.TypeExercise;
import com.app.proj.silverbars.database_models.Workout;
import com.app.proj.silverbars.utils.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        if (helper.getWorkoutDao().queryForAll() == null || helper.getWorkoutDao().queryForAll().isEmpty()){
            callback.onEmptyWorkouts();
        }else
            workoutsReady(callback);
    }


    private void workoutsReady(SavedWorkoutsCallback callback) throws SQLException {


        List<com.app.proj.silverbars.models.Workout> workoutList = new ArrayList<>();


        for (Workout workout:  helper.getWorkoutDao().queryForAll()){
            Log.d(TAG,"workouts in for size: "+workoutList.size());

            ArrayList<com.app.proj.silverbars.models.ExerciseRep> exerciseReps = new ArrayList<>();

            for (ExerciseRep exerciseRep: workout.getExercises()){

                Exercise exercise = helper.getExerciseDao().queryForId(exerciseRep.getExercise().getId());

                //types and muscles list
                List<String> types_exercise = new ArrayList<>();
                List<com.app.proj.silverbars.models.Muscle> muscles = new ArrayList<>();

                for (TypeExercise types:  exercise.getType_exercise()){types_exercise.add(types.getType());}

                for (Muscle muscle: exercise.getMuscles()){
                    muscles.add(
                            new com.app.proj.silverbars.models.Muscle(
                                    muscle.getMuscle(),muscle.getMuscle_activation(),
                                    muscle.getClassification(),
                                    muscle.getProgression_level()));
                }

                com.app.proj.silverbars.models.Exercise exercise1 =
                        new com.app.proj.silverbars.models.Exercise(
                                exercise.getId(),exercise.getExercise_name(),
                                exercise.getLevel(),
                                types_exercise,
                                exercise.getExercise_audio(),
                                exercise.getExercise_image(),
                                muscles
                                );

                exerciseReps.add(
                        new com.app.proj.silverbars.models.ExerciseRep(
                                exerciseRep.getId(),
                                exercise1,
                                exerciseRep.getRepetition(),
                                exerciseRep.getSeconds(),
                                exerciseRep.getTempo_positive(),
                                exerciseRep.getTempo_isometric(),
                                exerciseRep.getTempo_negative()
                        ));
            }



            //add workout completed to list
            workoutList.add(new com.app.proj.silverbars.models.Workout(
                workout.getId(),
                    workout.getWorkout_name(),
                    workout.getWorkout_image(),
                    workout.getSets(),
                    workout.getLevel(),
                    workout.getMain_muscle(),
                    exerciseReps
            ));
        }


        //return workouts_database list saved
        callback.onWorkouts(workoutList);
    }



    public void onDestroy(){
        if (helper != null) {
            OpenHelperManager.releaseHelper();
            helper = null;
        }
    }

}
