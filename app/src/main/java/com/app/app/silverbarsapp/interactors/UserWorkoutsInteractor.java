package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.UserWorkoutsCallback;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 01/12/17.
 */
public class UserWorkoutsInteractor {

    private static final String TAG = UserWorkoutsInteractor.class.getSimpleName();

    private DatabaseHelper helper;
    private MainService mainService;
    private DatabaseQueries queries;

    public UserWorkoutsInteractor(MainService mainService,DatabaseHelper helper){
        this.mainService = mainService;
        this.helper = helper;
        queries = new DatabaseQueries(helper);

    }

    public void deleteWorkout(int workout_id) throws SQLException {
        helper.deleteUserWorkout(workout_id);
    }

    public void getWorkout(UserWorkoutsCallback callback) throws SQLException {
        if (helper.getUserWorkoutDao().queryForAll().isEmpty()){
            callback.onEmptyWorkouts();
        }else {
            getMyWorkouts(callback);
        }
    }

    private void getMyWorkouts(UserWorkoutsCallback callback) throws SQLException {
        List<Workout> user_workouts = queries.getMyWorkouts();
        //return user_workouts_database list saved
        callback.onWorkouts(user_workouts);
    }


}
