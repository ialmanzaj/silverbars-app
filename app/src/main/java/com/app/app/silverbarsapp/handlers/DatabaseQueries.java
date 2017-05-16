package com.app.app.silverbarsapp.handlers;

import android.util.Log;

import com.app.app.silverbarsapp.database_models.Exercise;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.database_models.ExerciseRep;
import com.app.app.silverbarsapp.database_models.Muscle;
import com.app.app.silverbarsapp.database_models.MySavedWorkout;
import com.app.app.silverbarsapp.database_models.Person;
import com.app.app.silverbarsapp.database_models.FbProfile;
import com.app.app.silverbarsapp.database_models.TypeExercise;
import com.app.app.silverbarsapp.database_models.UserWorkout;
import com.app.app.silverbarsapp.database_models.WorkoutDone;
import com.app.app.silverbarsapp.models.Workout;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

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



    /**
     *
     *
     *
     * GET methods
     *
     *
     *
     *
     *
     */

    public boolean checkMyWorkoutsExist() throws SQLException {
        return !helper.getUserWorkoutDao().queryForAll().isEmpty();
    }

    public List<Workout> getMyWorkouts() throws SQLException {
        List<Workout> my_workouts  = new ArrayList<>();

        for (UserWorkout my_workout:  helper.getUserWorkoutDao().queryForAll()){
            //add user_workout completed to list
            my_workouts.add(getMyWorkoutModel(my_workout));

        }
        return my_workouts;
    }

    public boolean isWorkoutExist(int workout_id) throws SQLException {
        return helper.getSavedWorkoutDao().queryForId(workout_id) != null;
    }

    public MySavedWorkout getSavedtWorkout(int saved_workout_id) throws SQLException {
        return helper.getSavedWorkoutDao().queryForId(saved_workout_id);
    }

    public boolean isWorkoutSaved(int workout_id) throws SQLException {
        return helper.getSavedWorkoutDao().queryForId(workout_id).getSaved();
    }

    public void setWorkoutOff(int workout_id) throws SQLException {
        updateSavedWorkout(workout_id,false);
    }

    public void setWorkoutOn(int workout_id) throws SQLException {
        updateSavedWorkout(workout_id,true);
    }

    public com.app.app.silverbarsapp.database_models.Person getMyProfile() throws SQLException {
        return helper.getMyProfile().queryForAll().get(0);
    }

    public ExerciseProgression getExerciseProgressionById(int exercise_progression_id) throws SQLException {
        return helper.getExerciseProgressionDao().queryForId(exercise_progression_id);
    }

    public Person getPersonById(int id) throws SQLException {
       return helper.getMyProfile().queryForId(id);
    }

    public FbProfile getFaceProfile(FbProfile profile) throws SQLException {
        return helper.getProfileFacebook().queryForSameId(profile);
    }

    public FbProfile getMyFaceProfile() throws SQLException {
        return helper.getProfileFacebook().queryForAll().get(0);
    }

    private Exercise getExercise(int id) throws SQLException {
        return helper.getExerciseDao().queryForId(id);
    }

    public com.app.app.silverbarsapp.models.ExerciseProgression getLastProgressionByExerciseId(int exercise_id) throws SQLException {
        if (helper.getExerciseProgressionDao().queryForEq("exercise_id",exercise_id).size() < 1) {
            return null;
        }else {

            return getProgressionModel(
                    getExerciseProgressionsById(exercise_id)
                            .get(getExerciseProgressionsById(exercise_id).size() -1));
        }
    }

    public List<ExerciseProgression> getExerciseProgressionsById(int exercise_id) throws SQLException {
        return helper.getExerciseProgressionDao().queryForEq("exercise_id",exercise_id);
    }

    public List<com.app.app.silverbarsapp.models.ExerciseProgression> getExerciseProgressions() throws SQLException {
        List<com.app.app.silverbarsapp.models.ExerciseProgression> progressions = new ArrayList<>();
        for (ExerciseProgression exerciseProgression: helper.getExerciseProgressionDao().queryForAll()){
            progressions.add(getProgressionModel(exerciseProgression));
        }
        return progressions;
    }

    private UserWorkout getMyWorkout(int id) throws SQLException {
        return helper.getUserWorkoutDao().queryForId(id);
    }

    private WorkoutDone getWorkoutDone(int workout_done_id) throws SQLException {
        return helper.getWorkoutsDone().queryForId(workout_done_id);
    }

    /**
     *
     *
     *
     *
     *
     *
     * SAVE methods
     *
     *
     *
     *
     *
     */

    public FbProfile saveFaceProfile(FbProfile profile) throws SQLException {
        FbProfile fbProfile_database = getFaceProfile(profile);

        if (fbProfile_database == null){
            fbProfile_database = profile;
            helper.getProfileFacebook().create(fbProfile_database);
        }

        return fbProfile_database;
    }

    public Person saveProfile(com.app.app.silverbarsapp.models.Person person) throws SQLException {
        Person person_database = getPersonById(person.getId());
        if (person_database == null) {
            person_database = new Person(
                    person.getId(),
                    person.getUser().getUsername(),
                    person.getUser().getFirst_name(),
                    person.getUser().getLast_name(),
                    person.getUser().getEmail()
            );
            helper.getMyProfile().create(person_database);
        }

        return person_database;
    }

    public ExerciseProgression saveExerciseProgression(com.app.app.silverbarsapp.models.ExerciseProgression exerciseProgression) throws SQLException {
        ExerciseProgression exerciseProgression_database = getExerciseProgressionById(exerciseProgression.getId());
        if (exerciseProgression_database == null) {
            exerciseProgression_database = new ExerciseProgression(
                    exerciseProgression.getId(),
                    exerciseProgression.getDate(),
                    null,
                    exerciseProgression.getPerson(),
                    exerciseProgression.getTotal_time(),
                    saveExercise(exerciseProgression.getExercise()),
                    exerciseProgression.getTotal_repetition(),
                    exerciseProgression.getRepetitions_done(),
                    exerciseProgression.getTotal_seconds(),
                    exerciseProgression.getSeconds_done(),
                    exerciseProgression.getTotal_weight()
            );

            //saving exercise progression
            Log.d(TAG,"saveExerciseProgression: "+exerciseProgression.getId());
            helper.getExerciseProgressionDao().create(exerciseProgression_database);
        }
        return exerciseProgression_database;
    }


    private WorkoutDone saveWorkoutDone(com.app.app.silverbarsapp.models.WorkoutDone workoutDone) throws SQLException {
        WorkoutDone workoutDone_database = getWorkoutDone(workoutDone.getId());
        if (workoutDone_database == null) {
            workoutDone_database = new WorkoutDone(
                    workoutDone.getId(),
                    workoutDone.getDate(),
                    saveMyWorkout(workoutDone.getMy_workout()),
                    workoutDone.getPerson(),
                    workoutDone.getTotal_time(),
                    workoutDone.getSets_completed()
            );

            //workout created in database
            Log.d(TAG,"saveWorkoutDone: "+workoutDone_database.getId());
            helper.getWorkoutsDone().create(workoutDone_database);
        }
        return workoutDone_database;
    }


    public UserWorkout saveMyWorkout(com.app.app.silverbarsapp.models.Workout workout) throws SQLException {
        UserWorkout my_workout = getMyWorkout(workout.getId());
        if (my_workout == null) {
            my_workout = new UserWorkout(
                    workout.getId(),
                    workout.getWorkout_name(),
                    workout.getWorkout_image(),
                    workout.getSets(),
                    workout.getLevel(),
                    workout.getMainMuscle()
            );

            //create user workout
            Log.d(TAG,"saveMyWorkout: "+my_workout.getWorkout_name());
            helper.getUserWorkoutDao().create(my_workout);

            for (com.app.app.silverbarsapp.models.ExerciseRep exerciseRep : workout.getExercises()) {

                Exercise exercise_database = saveExercise(exerciseRep.getExercise());
                saveTypesOfExercise(exerciseRep, exercise_database);
                saveMuscles(exerciseRep, exercise_database);
                saveMyWorkoutExercisesReps(exercise_database,exerciseRep,my_workout);
            }

        }

        return my_workout;
    }

    private void saveMyWorkoutExercisesReps(Exercise exercise_database,com.app.app.silverbarsapp.models.ExerciseRep exerciseRep,UserWorkout my_workout) throws SQLException {
        //insert exercise rep
        helper.getExerciseRepDao().create(
                new ExerciseRep(
                        exercise_database,
                        exerciseRep.getRepetition(),
                        exerciseRep.getSeconds(),
                        my_workout,
                        exerciseRep.getWeight()
                )
        );
    }

    public MySavedWorkout saveWorkout(com.app.app.silverbarsapp.models.Workout workout) throws SQLException {
        MySavedWorkout my_saved_workout = getSavedtWorkout(workout.getId());
        if (my_saved_workout == null) {
            my_saved_workout = new MySavedWorkout(
                    workout.getId(),
                    workout.getWorkout_name(),
                    workout.getWorkout_image(),
                    workout.getSets(),
                    workout.getLevel(),
                    workout.getMainMuscle(),
                    true
            );

            helper.getSavedWorkoutDao().create(my_saved_workout);

            for (com.app.app.silverbarsapp.models.ExerciseRep exerciseRep : workout.getExercises()) {
                Exercise exercise = saveExercise(exerciseRep.getExercise());
                saveMuscles(exerciseRep, exercise);
                saveTypesOfExercise(exerciseRep, exercise);
                saveExerciseReps(exercise, exerciseRep, my_saved_workout);
            }

        }
        return my_saved_workout;
    }

    private void saveExerciseReps(Exercise exercise,com.app.app.silverbarsapp.models.ExerciseRep exerciseRep,MySavedWorkout my_saved_workout) throws SQLException {
        //re create exercise rep model
        helper.getExerciseRepDao().create(new ExerciseRep(
                exercise,
                exerciseRep.getRepetition(),
                exerciseRep.getSeconds(),
                my_saved_workout,
                exerciseRep.getWeight())
        );
    }


    private Exercise saveExercise(com.app.app.silverbarsapp.models.Exercise exercise) throws SQLException {
        Exercise exercise_database = getExercise(exercise.getId());
        if (exercise_database == null) {
            exercise_database = new Exercise(
                    exercise.getId(),
                    exercise.getExercise_name(),
                    exercise.getLevel(),
                    exercise.getExercise_audio(),
                    exercise.getExercise_image()
            );

            Log.d(TAG,"saveExercise: "+exercise.getExercise_name());
            //exercise created in database
            helper.getExerciseDao().create(exercise_database);
        }

        //ruturn exercise created
        return exercise_database;
    }

    private void saveTypesOfExercise(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
        //set types exercises to database
        for (String type: exerciseRep.getExercise().getType_exercise()){
            helper.getTypeDao().create(new TypeExercise(type, exercise));
        }
    }

    private void saveMuscles(com.app.app.silverbarsapp.models.ExerciseRep exerciseRep, Exercise exercise) throws SQLException {
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


    /**
     *
     *
     *
     *
     * Transform Models of the database to the Models of the API
     *
     *
     *
     *
     */

    private com.app.app.silverbarsapp.models.ExerciseProgression getProgressionModel(ExerciseProgression exerciseProgression_database) throws SQLException {

        //WorkoutDone workoutDone = getWorkoutDone(exerciseProgression_database.getMy_workout_done().getId());
        //Log.d(TAG,"workoutDone "+workoutDone);

        Exercise exercise = getExercise(exerciseProgression_database.getExercise().getId());

        return new com.app.app.silverbarsapp.models.ExerciseProgression(
                exerciseProgression_database.getId(),
                exerciseProgression_database.getDate(),
                null,
                exerciseProgression_database.getPerson(),
                exerciseProgression_database.getTotal_time(),
                getExerciseModel(exercise),
                exerciseProgression_database.getTotal_repetition(),
                exerciseProgression_database.getRepetitions_done(),
                exerciseProgression_database.getTotal_seconds(),
                exerciseProgression_database.getSeconds_done(),
                exerciseProgression_database.getTotal_weight()
        );
    }

    private com.app.app.silverbarsapp.models.WorkoutDone getWorkoutDoneModel(WorkoutDone workout_done_database) throws SQLException {
        UserWorkout myWorkout_database = getMyWorkout(workout_done_database.getMy_workout().getId());

        return new com.app.app.silverbarsapp.models.WorkoutDone(
                workout_done_database.getId(),
                workout_done_database.getDate(),
                null,
                workout_done_database.getPerson(),
                workout_done_database.getTotal_time(),
                workout_done_database.getSets_completed()
        );
    }

    private com.app.app.silverbarsapp.models.Workout getMyWorkoutModel(UserWorkout my_workout_database) throws SQLException {
        return new com.app.app.silverbarsapp.models.Workout(
                my_workout_database.getId(),
                my_workout_database.getWorkout_name(),
                my_workout_database.getWorkout_image(),
                my_workout_database.getSets(),
                my_workout_database.getLevel(),
                my_workout_database.getMain_muscle(),
                getExercisesRepsModel(my_workout_database)
        );
    }

    private  ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> getExercisesRepsModel(UserWorkout my_workout_database) throws SQLException {
        ArrayList<com.app.app.silverbarsapp.models.ExerciseRep> exerciseReps = new ArrayList<>();

        for (ExerciseRep exerciseRep: my_workout_database.getExercises()){

            Exercise exercise_database = getExercise(exerciseRep.getExercise().getId());

            //add exercise rep to list
            exerciseReps.add(new com.app.app.silverbarsapp.models.ExerciseRep(
                    exerciseRep.getId(),
                    getExerciseModel(exercise_database),
                    exerciseRep.getRepetition(),
                    exerciseRep.getSeconds(),
                    exerciseRep.getWeight()
            ));
        }

        return exerciseReps;
    }

    private com.app.app.silverbarsapp.models.Exercise getExerciseModel(Exercise exercise_database){
        //re-create exercise
        return new com.app.app.silverbarsapp.models.Exercise(
                exercise_database.getId(),
                exercise_database.getExercise_name(),
                exercise_database.getLevel(),
                getTypeExercisesModel(exercise_database),
                exercise_database.getExercise_audio(),
                exercise_database.getExercise_image(),
                getMusclesExercisesModel(exercise_database)
        );
    }

    private List<String> getTypeExercisesModel(Exercise exercise_database){
        List<String> types_exercise = new ArrayList<>();
        //get the type exercise from table to new object in json
        for (TypeExercise types:  exercise_database.getType_exercise()){types_exercise.add(types.getType());}
        return types_exercise;
    }

    private List<com.app.app.silverbarsapp.models.MuscleExercise> getMusclesExercisesModel(Exercise exercise_database){
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



    /**
     *
     *
     *
     * DELETE methods
     *
     *
     *
     *
     */


    public void deleteMyWorkout(int my_workout_id) throws java.sql.SQLException {
        DeleteBuilder<UserWorkout, Integer> deleteUserWorkoutBuilder = helper.getUserWorkoutDao().deleteBuilder();
        deleteUserWorkoutBuilder.where().idEq(my_workout_id);
        deleteUserWorkoutBuilder.delete();
    }


    public void deleteExerciseProgression(int id) throws SQLException {
        DeleteBuilder<ExerciseProgression, Integer> deleteBuilder = helper.getExerciseProgressionDao().deleteBuilder();
        deleteBuilder.where().idEq(id);
        deleteBuilder.delete();
    }



    /**
     *
     *
     *
     * UPDATE methods
     *
     *
     *
     *
     */

    private void updateSavedWorkout(int workout_id,boolean saved) throws java.sql.SQLException {
        UpdateBuilder<MySavedWorkout, Integer> updateSavedWorkoutBuilder = helper.getSavedWorkoutDao().updateBuilder();
        updateSavedWorkoutBuilder.where().idEq(workout_id);
        updateSavedWorkoutBuilder.updateColumnValue("saved",saved);
        updateSavedWorkoutBuilder.update();
    }


}
