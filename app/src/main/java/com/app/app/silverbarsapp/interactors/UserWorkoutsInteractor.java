package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.callbacks.UserWorkoutsCallback;
import com.app.app.silverbarsapp.database_models.Exercise;
import com.app.app.silverbarsapp.database_models.ExerciseRep;
import com.app.app.silverbarsapp.database_models.Muscle;
import com.app.app.silverbarsapp.database_models.TypeExercise;
import com.app.app.silverbarsapp.database_models.UserWorkout;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 01/12/17.
 */
public class UserWorkoutsInteractor {

    private static final String TAG = UserWorkoutsInteractor.class.getSimpleName();

    private DatabaseHelper helper;

    public UserWorkoutsInteractor(DatabaseHelper helper){
        this.helper = helper;
    }

    public void deleteWorkout(int workout_id) throws SQLException {
        helper.deleteUserWorkout(workout_id);
    }

    public void getWorkout(UserWorkoutsCallback callback) throws SQLException {
        if (helper.getUserWorkoutDao().queryForAll() == null || helper.getUserWorkoutDao().queryForAll().isEmpty()){
            callback.onEmptyWorkouts();
        }else {
            getUserWorkouts(callback);
        }
    }


    private void getUserWorkouts(UserWorkoutsCallback callback) throws SQLException {

            List<Workout> user_workouts = new ArrayList<>();

            for (UserWorkout user_workout:  helper.getUserWorkoutDao().queryForAll()){

                //Log.d(TAG,"user_workouts in for size: "+workoutList.size());

                ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> exerciseReps = new ArrayList<>();

                for (ExerciseRep exerciseRep: user_workout.getExercises()){

                    Exercise exercise_database = helper.getExerciseDao().queryForId(exerciseRep.getExercise().getId());

                    //types and muscles list
                    List<String> types_exercise = new ArrayList<>();
                    List<com.app.app.silverbarsapp.models.MuscleExercise> muscles = new ArrayList<>();

                    //get the type exercise from table to new object in json
                    for (TypeExercise types:  exercise_database.getType_exercise()){types_exercise.add(types.getType());}


                    //get the muscles
                    for (Muscle muscle: exercise_database.getMuscles()){
                        muscles.add(
                                new com.app.app.silverbarsapp.models.MuscleExercise(
                                        muscle.getMuscle(),
                                        muscle.getMuscle_activation(),
                                        muscle.getClassification(),
                                        muscle.getProgression_level()));
                    }

                    //re-create exercise
                    com.app.app.silverbarsapp.models.Exercise exercise =
                            new com.app.app.silverbarsapp.models.Exercise(
                                    exercise_database.getId(),
                                    exercise_database.getExercise_name(),
                                    exercise_database.getLevel(),
                                    types_exercise,
                                    exercise_database.getExercise_audio(),
                                    exercise_database.getExercise_image(),
                                    muscles
                            );


                    //add exercise rep to list
                    exerciseReps.add(
                            new com.app.app.silverbarsapp.models.ExerciseRep(
                                    exerciseRep.getId(),
                                    exercise,
                                    exerciseRep.getRepetition(),
                                    exerciseRep.getSeconds()
                            ));
                }


                //add user_workout completed to list
                user_workouts.add(new com.app.app.silverbarsapp.models.Workout(
                        user_workout.getId(),
                        user_workout.getWorkout_name(),
                        user_workout.getWorkout_image(),
                        user_workout.getSets(),
                        user_workout.getLevel(),
                        user_workout.getMain_muscle(),
                        exerciseReps
                ));
            }

        //return user_workouts_database list saved
        callback.onWorkouts(user_workouts);
    }


}
