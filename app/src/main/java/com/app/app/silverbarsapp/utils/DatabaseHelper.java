package com.app.app.silverbarsapp.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.database_models.Exercise;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.database_models.ExerciseRep;
import com.app.app.silverbarsapp.database_models.Muscle;
import com.app.app.silverbarsapp.database_models.MySavedWorkout;
import com.app.app.silverbarsapp.database_models.Person;
import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.database_models.TypeExercise;
import com.app.app.silverbarsapp.database_models.UserWorkout;
import com.app.app.silverbarsapp.database_models.WorkoutDone;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import static com.app.app.silverbarsapp.Constants.DATABASE_NAME;
import static com.app.app.silverbarsapp.Constants.DATABASE_VERSION;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Dao<MySavedWorkout, Integer> mSavedWorkoutDao;
    private Dao<UserWorkout, Integer> mUserWorkoutDao;
    private Dao<ExerciseRep, Integer> mExerciseRepDao;
    private Dao<Exercise, Integer> mExerciseDao;
    private Dao<Muscle, Integer> sMuscleDao;
    private Dao<TypeExercise, Integer> mTypeDao;
    private Dao<ProfileFacebook, Integer> mProfileFacebook;
    private Dao<Person, Integer> mPersonDao;

    private Dao<WorkoutDone, Integer> mWorkoutDoneDao;
    private Dao<ExerciseProgression, Integer> mExerciseProgressionDao;

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
            TableUtils.createTable(connectionSource, MySavedWorkout.class);
            TableUtils.createTable(connectionSource, UserWorkout.class);
            TableUtils.createTable(connectionSource, ProfileFacebook.class);
            TableUtils.createTable(connectionSource, Person.class);
            TableUtils.createTable(connectionSource, WorkoutDone.class);
            TableUtils.createTable(connectionSource, ExerciseProgression.class);


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
            TableUtils.dropTable(connectionSource, MySavedWorkout.class, false);
            TableUtils.dropTable(connectionSource, UserWorkout.class,false);
            TableUtils.dropTable(connectionSource, ProfileFacebook.class,false);
            TableUtils.dropTable(connectionSource, Person.class,false);
            TableUtils.dropTable(connectionSource, WorkoutDone.class,false);
            TableUtils.dropTable(connectionSource, ExerciseProgression.class,false);


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
        if(mTypeDao == null) {
            mTypeDao = getDao(TypeExercise.class);
        }
        return mTypeDao;
    }


    public Dao<Muscle, Integer> getMuscleDao() throws java.sql.SQLException {
        if(sMuscleDao == null) {
            sMuscleDao = getDao(Muscle.class);
        }
        return sMuscleDao;
    }

    public Dao<Exercise, Integer> getExerciseDao() throws java.sql.SQLException {
        if(mExerciseDao == null) {
            mExerciseDao = getDao(Exercise.class);
        }
        return mExerciseDao;
    }

    public Dao<ExerciseRep, Integer> getExerciseRepDao() throws java.sql.SQLException {
        if(mExerciseRepDao == null) {
            mExerciseRepDao = getDao(ExerciseRep.class);
        }
        return mExerciseRepDao;
    }


    public Dao<MySavedWorkout, Integer> getSavedWorkoutDao() throws java.sql.SQLException {
        if(mSavedWorkoutDao == null) {
            mSavedWorkoutDao = getDao(MySavedWorkout.class);
        }
        return mSavedWorkoutDao;
    }

    public Dao<UserWorkout, Integer> getUserWorkoutDao() throws java.sql.SQLException {
        if(mUserWorkoutDao == null) {
            mUserWorkoutDao = getDao(UserWorkout.class);
        }
        return mUserWorkoutDao;
    }

    public Dao<ProfileFacebook, Integer> getProfileFacebook() throws java.sql.SQLException {
        if(mProfileFacebook == null) {
            mProfileFacebook = getDao(ProfileFacebook.class);
        }
        return mProfileFacebook;
    }

    public Dao<Person, Integer> getMyProfile() throws java.sql.SQLException {
        if(mPersonDao == null) {
            mPersonDao = getDao(Person.class);
        }
        return mPersonDao;
    }

    public Dao<WorkoutDone, Integer> getWorkoutsDone() throws java.sql.SQLException {
        if(mWorkoutDoneDao == null) {
            mWorkoutDoneDao = getDao(WorkoutDone.class);
        }
        return mWorkoutDoneDao;
    }

    public Dao<ExerciseProgression, Integer> getExerciseProgressionDao() throws java.sql.SQLException {
        if(mExerciseProgressionDao == null) {
            mExerciseProgressionDao = getDao(ExerciseProgression.class);
        }
        return mExerciseProgressionDao;
    }


}
