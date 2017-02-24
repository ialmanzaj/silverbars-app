package com.app.proj.silverbarsapp;

import com.app.proj.silverbarsapp.database_models.Exercise;
import com.app.proj.silverbarsapp.database_models.ExerciseRep;
import com.app.proj.silverbarsapp.database_models.Muscle;
import com.app.proj.silverbarsapp.database_models.TypeExercise;
import com.app.proj.silverbarsapp.database_models.Workout;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Created by isaacalmanza on 02/08/17.
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
            Workout.class,
            ExerciseRep.class,
            Exercise.class,
            Muscle.class,
            TypeExercise.class
    };
    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}