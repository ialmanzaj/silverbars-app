package com.app.app.silverbarsapp;

import android.util.Log;

import com.app.app.silverbarsapp.database_models.Exercise;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.database_models.ExerciseRep;
import com.app.app.silverbarsapp.database_models.Muscle;
import com.app.app.silverbarsapp.database_models.MySavedWorkout;
import com.app.app.silverbarsapp.database_models.TypeExercise;
import com.app.app.silverbarsapp.database_models.UserWorkout;
import com.app.app.silverbarsapp.database_models.WorkoutDone;
import com.app.app.silverbarsapp.models.Person;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.DatabaseHelper;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 04/04/17.
 */

public class DatabaseQueries {

    private static final String TAG = DatabaseQueries.class.getSimpleName();

    private DatabaseHelper helper;

    public DatabaseQueries(DatabaseHelper helper){
        this.helper = helper;
    }

    public void saveProfile(Person person) throws SQLException {
        helper.getMyProfile().create(
                new com.app.app.silverbarsapp.database_models.Person(
                        person.getId(),
                        person.getUser().getUsername(),
                        person.getUser().getFirst_name(),
                        person.getUser().getLast_name(),
                        person.getUser().getEmail()
                )
        );
    }

    public com.app.app.silverbarsapp.database_models.Person getMyProfile() throws SQLException {
        return helper.getMyProfile().queryForAll().get(0);
    }

    public boolean existExerciseProgressionById(int id) throws SQLException {
        return helper.getExerciseProgressionDao().queryForId(id) != null;
    }

    public void deleteExerciseProgression(int id) throws SQLException {
        DeleteBuilder<ExerciseProgression, Integer> deleteBuilder = helper.getExerciseProgressionDao().deleteBuilder();
        deleteBuilder.where().idEq(id);
        deleteBuilder.delete();
    }

    public void saveExerciseProgression(com.app.app.silverbarsapp.models.ExerciseProgression exerciseProgression) throws SQLException {
        Log.d(TAG,"saveExerciseProgression "+exerciseProgression.getId());
        helper.getExerciseProgressionDao().create(
                new ExerciseProgression(
                    exerciseProgression.getId(),
                        exerciseProgression.getDate(),
                        getMyWorkoutDone(exerciseProgression.getMy_workout_done().getId()),
                        exerciseProgression.getPerson(),
                        exerciseProgression.getTotal_time(),
                        insertExercise(exerciseProgression.getExercise()),
                        exerciseProgression.getTotal_repetition(),
                        exerciseProgression.getRepetitions_done(),
                        exerciseProgression.getTotal_seconds(),
                        exerciseProgression.getSeconds_done(),
                        exerciseProgression.getTotal_weight()
                )
        );
    }


    public WorkoutDone getMyWorkoutDone(int id) throws SQLException {
        return helper.getWorkoutsDone().queryForId(id);
    }

    public ExerciseProgression searchExerciseProgressionByExerciseid(int exercise_id) throws SQLException {
       if (helper.getExerciseProgressionDao().queryForEq("exercise_id",exercise_id).size() <= 0) {
            return null;
        }else {
            return getLastExerciseProgression(exercise_id);
        }
    }

    public List<ExerciseProgression> getExerciseProgressions() throws SQLException {
        return helper.getExerciseProgressionDao().queryForAll();
    }

    private ExerciseProgression getLastExerciseProgression(int exercise_id) throws SQLException {
        return helper.getExerciseProgressionDao().queryForEq("exercise_id",exercise_id).get(
                helper.getExerciseProgressionDao().queryForEq("exercise_id",exercise_id).size() -1);
    }

    public List<ExerciseProgression> getExerciseProgressionsById(int exercise_id) throws SQLException {
        return helper.getExerciseProgressionDao().queryForEq("exercise_id",exercise_id);
    }


    public void insertUserWorkout(com.app.app.silverbarsapp.models.Workout workout) throws SQLException {

        UserWorkout user_workout = new UserWorkout(
                workout.getId(),
                workout.getWorkout_name(),
                workout.getWorkout_image(),
                workout.getSets(),
                workout.getLevel(),
                workout.getMainMuscle()
        );

        //create user workout
        helper.getUserWorkoutDao().create(user_workout);

        for (com.app.app.silverbarsapp.models.ExerciseRep exerciseRep: workout.getExercises()){

            //insert exercise
            Exercise exercise_database = insertExercise(exerciseRep.getExercise());

            //insert type exercise
            insertTypesOfExercise(exerciseRep,exercise_database);

            //insert muscles
            insertMuscles(exerciseRep,exercise_database);

            //insert exercise rep
            helper.getExerciseRepDao().create(
                    new ExerciseRep(
                            exercise_database,
                            exerciseRep.getRepetition(),
                            exerciseRep.getSeconds(),
                            user_workout,
                            exerciseRep.getWeight()
                    )
            );
        }

    }

    private Exercise insertExercise(com.app.app.silverbarsapp.models.Exercise exercise) throws SQLException {
        Exercise exercise_database = getExercise(exercise.getId());

        if (exercise_database == null) {
            exercise_database = new Exercise(
                    exercise.getId(),
                    exercise.getExercise_name(),
                    exercise.getLevel(),
                    exercise.getExercise_audio(),
                    exercise.getExercise_image()
            );
            //exercise created in database
            helper.getExerciseDao().create(exercise_database);
        }

        //ruturn exercise created
        return exercise_database;
    }

    private Exercise getExercise(int id) throws SQLException {
        return helper.getExerciseDao().queryForId(id);
    }

    private void insertTypesOfExercise(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set types exercises to database
        for (String type: exerciseRep.getExercise().getType_exercise()){
            helper.getTypeDao().create(new TypeExercise(type, exercise));
        }
    }


    private void insertMuscles(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        for (com.app.app.silverbarsapp.models.MuscleExercise muscle: exerciseRep.getExercise().getMuscles()){
            helper.getMuscleDao().create(
                    new Muscle(muscle.getMuscle(),
                            muscle.getMuscle_activation(),
                            muscle.getClassification(),
                            muscle.getProgression_level(),
                            exercise
                    ));
        }
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

    public void saveWorkout(com.app.app.silverbarsapp.models.Workout workout) throws SQLException {

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

        for (com.app.app.silverbarsapp.models.ExerciseRep exerciseRep: workout.getExercises()){

            Exercise exercise = insertExercise(exerciseRep.getExercise());

            insertMuscles(exerciseRep,exercise);
            insertTypesOfExercise(exerciseRep,exercise);

            //re create exercise rep model
            helper.getExerciseRepDao().create(new ExerciseRep(exercise,
                    exerciseRep.getRepetition(),
                    exerciseRep.getSeconds(),
                    my_saved_workout,
                    exerciseRep.getWeight())
            );
        }
    }


    public List<Workout> getMyWorkouts() throws SQLException {
        List<Workout> my_workouts  = new ArrayList<>();

        for (UserWorkout my_workout:  helper.getUserWorkoutDao().queryForAll()){
            //add user_workout completed to list
            my_workouts.add(new com.app.app.silverbarsapp.models.Workout(
                    my_workout.getId(),
                    my_workout.getWorkout_name(),
                    my_workout.getWorkout_image(),
                    my_workout.getSets(),
                    my_workout.getLevel(),
                    my_workout.getMain_muscle(),
                    getExercisesReps(my_workout)
            ));
        }
        return my_workouts;
    }


    private  ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> getExercisesReps(UserWorkout my_workout) throws SQLException {
        ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> exerciseReps = new ArrayList<>();

        for (ExerciseRep exerciseRep: my_workout.getExercises()){

            Exercise exercise_database = helper.getExerciseDao().queryForId(exerciseRep.getExercise().getId());

            //add exercise rep to list
            exerciseReps.add(new com.app.app.silverbarsapp.models.ExerciseRep(
                    exerciseRep.getId(),
                    getExercise(exercise_database),
                    exerciseRep.getRepetition(),
                    exerciseRep.getSeconds(),
                    exerciseRep.getWeight()
            ));
        }

        return exerciseReps;
    }


    private com.app.app.silverbarsapp.models.Exercise getExercise(Exercise exercise_database){
        //re-create exercise
        return new com.app.app.silverbarsapp.models.Exercise(
                exercise_database.getId(),
                exercise_database.getExercise_name(),
                exercise_database.getLevel(),
                getTypeExercises(exercise_database),
                exercise_database.getExercise_audio(),
                exercise_database.getExercise_image(),
                getMuscles(exercise_database)
        );
    }

    private List<String> getTypeExercises(Exercise exercise_database){
        List<String> types_exercise = new ArrayList<>();
        //get the type exercise from table to new object in json
        for (TypeExercise types:  exercise_database.getType_exercise()){types_exercise.add(types.getType());}
        return types_exercise;
    }

    private List<com.app.app.silverbarsapp.models.MuscleExercise> getMuscles(Exercise exercise_database){
        List<com.app.app.silverbarsapp.models.MuscleExercise> muscles = new ArrayList<>();

        //get the muscles
        for (Muscle muscle: exercise_database.getMuscles()){

            com.app.app.silverbarsapp.models.MuscleExercise muscleExercise = new com.app.app.silverbarsapp.models.MuscleExercise();

            muscleExercise.setMuscle(muscle.getMuscle());
            muscleExercise.setClassification( muscle.getClassification());
            muscleExercise.setMuscle_activation(muscle.getMuscle_activation());
            muscleExercise.setProgression_level( muscle.getProgression_level());

            muscles.add(muscleExercise);
        }
        return muscles;
    }



}
