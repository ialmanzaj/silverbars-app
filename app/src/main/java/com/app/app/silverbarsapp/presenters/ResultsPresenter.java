package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.ResultsCallback;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.interactors.ResultsInteractor;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.viewsets.ResultsView;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public class ResultsPresenter extends BasePresenter  implements ResultsCallback {

    private static final String TAG = ResultsPresenter.class.getSimpleName();

    private ResultsView view;
    private ResultsInteractor interactor;

    public ResultsPresenter(ResultsView view,ResultsInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void createWorkoutDone(int workout_id,int sets,String total_time) throws SQLException {
        interactor.createWorkoutDone(workout_id,sets,total_time,this);
    }

    public void getExercisesProgression(List<ExerciseRep> exercises) throws SQLException {
        interactor.getProgressions(exercises,this);
    }

    @Override
    public void onWorkoutDone(WorkoutDone workout) {
        view.onWorkoutDone(workout);
    }

    @Override
    public void isEmptyProgression() {
        view.isEmptyProgression();
    }

    @Override
    public void onExerciseProgression(List<ExerciseProgression> exerciseProgression) {
        view.onExerciseProgression(exerciseProgression);
    }



    @Override
    public void onStart() {}

    @Override
    public void onStop() {}

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onRestart() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onServerError() {
        view.displayServerError();
    }
    @Override
    public void onNetworkError() {
        view.displayNetworkError();
    }

}
