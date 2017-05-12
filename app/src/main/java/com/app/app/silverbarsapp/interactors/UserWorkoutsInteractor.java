package com.app.app.silverbarsapp.interactors;

import com.app.app.silverbarsapp.handlers.DatabaseQueries;
import com.app.app.silverbarsapp.MainService;
import com.app.app.silverbarsapp.callbacks.UserWorkoutsCallback;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 01/12/17.
 */
public class UserWorkoutsInteractor {

    private static final String TAG = UserWorkoutsInteractor.class.getSimpleName();

    private MainService mainService;
    private DatabaseQueries queries;

    public UserWorkoutsInteractor(MainService mainService,DatabaseHelper helper){
        this.mainService = mainService;
        queries = new DatabaseQueries(helper);
    }

    public void getMyWorkouts(UserWorkoutsCallback callback) throws SQLException {
        if (queries.checkMyWorkoutsExist()){
            List<Workout> user_workouts = queries.getMyWorkouts();
            //return user_workouts_database list saved
            callback.onWorkouts(user_workouts);
        }else {
            callback.onEmptyWorkouts();
        }
    }

    public void deleteMyWorkout(int my_workout_id) throws SQLException {
        queries.deleteMyWorkout(my_workout_id);
    }

}
