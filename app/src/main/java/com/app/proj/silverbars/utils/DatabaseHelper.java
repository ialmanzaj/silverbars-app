package com.app.proj.silverbars.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.app.proj.silverbars.R;
import com.app.proj.silverbars.database_models.Exercise;
import com.app.proj.silverbars.database_models.ExerciseRep;
import com.app.proj.silverbars.database_models.Muscle;
import com.app.proj.silverbars.database_models.TypeExercise;
import com.app.proj.silverbars.database_models.Workout;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import static com.app.proj.silverbars.Constants.DATABASE_NAME;
import static com.app.proj.silverbars.Constants.DATABASE_VERSION;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class DatabaseHelper  extends OrmLiteSqliteOpenHelper {


    private Dao<Workout, Integer> workoutDao;
    private Dao<ExerciseRep, Integer> exerciserepDao;
    private Dao<Exercise, Integer> exerciseDao;
    private Dao<Muscle, Integer> muscleDao;
    private Dao<TypeExercise, Integer> typeDao;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION,
                /**
                 * R.raw.ormlite_config is a reference to the ormlite_config.txt file in the
                 * /res/raw/ directory of this project
                 * */
                R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            /**
             * creates the Todo database table
             */
            TableUtils.createTable(connectionSource, Muscle.class);
            TableUtils.createTable(connectionSource, TypeExercise.class);
            TableUtils.createTable(connectionSource, Exercise.class);
            TableUtils.createTable(connectionSource, ExerciseRep.class);
            TableUtils.createTable(connectionSource, Workout.class);

        }  catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            /**
             * Recreates the database when onUpgrade is called by the framework
             */
            TableUtils.dropTable(connectionSource, Muscle.class,false);
            TableUtils.dropTable(connectionSource, TypeExercise.class,false);
            TableUtils.dropTable(connectionSource, Exercise.class,false);
            TableUtils.dropTable(connectionSource, ExerciseRep.class,false);
            TableUtils.dropTable(connectionSource, Workout.class, false);

            onCreate(database, connectionSource);

        }  catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Returns an instance of the data access object
     * @return
     * @throws SQLException
     */

    public Dao<TypeExercise, Integer> getTypeDao()throws java.sql.SQLException {
        if(typeDao == null) {
            typeDao = getDao(TypeExercise.class);
        }
        return typeDao;
    }


    public Dao<Muscle, Integer> getMuscleDao() throws java.sql.SQLException {
        if(muscleDao == null) {
            muscleDao = getDao(Muscle.class);
        }
        return muscleDao;
    }

    public Dao<Exercise, Integer> getExerciseDao() throws java.sql.SQLException {
        if(exerciseDao == null) {
            exerciseDao = getDao(Exercise.class);
        }
        return exerciseDao;
    }

    public Dao<ExerciseRep, Integer> getExerciseRepDao() throws java.sql.SQLException {
        if(exerciserepDao == null) {
            exerciserepDao = getDao(ExerciseRep.class);
        }
        return exerciserepDao;
    }


    public Dao<Workout, Integer> getWorkoutDao() throws java.sql.SQLException {
        if(workoutDao == null) {
            workoutDao = getDao(Workout.class);
        }
        return workoutDao;
    }

}
