package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.ResultsCallback;
import com.app.app.silverbarsapp.interactors.ResultsInteractor;
import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;
import com.app.app.silverbarsapp.viewsets.ResultsView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public class ResultsPresenter extends BasePresenter  implements ResultsCallback {

    private static final String TAG = ResultsPresenter.class.getSimpleName();

    private ResultsView view;
    private ResultsInteractor interactor;

    private ArrayList<com.app.app.silverbarsapp.models.ExerciseProgression> mExercises;

    public ResultsPresenter(ResultsView view,ResultsInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void createWorkoutDone(int workout_id,int sets,String total_time,boolean isUserWorkout) throws SQLException {
        interactor.createWorkoutDone(workout_id,sets,total_time,isUserWorkout,this);
    }

    public void saveExerciseProgressions(int workout_id,int sets, ArrayList<com.app.app.silverbarsapp.models.ExerciseProgression> exercises) throws SQLException {
        mExercises = exercises;
        interactor.saveExerciseProgressions(workout_id,sets,exercises,this);
    }

    public void getExercisesProgression(List<ExerciseProgression> exercises) throws SQLException {
        interactor.getProgressions(exercises,this);
    }

    @Override
    public void isEmptyProgression() {
        view.isEmptyProgression();
    }

    @Override
    public void onWorkoutDone(WorkoutDone workout) {
        view.onWorkoutDone(workout);
    }

    @Override
    public void onSavedExerciseProgress(com.app.app.silverbarsapp.models.ExerciseProgression exerciseProgression) {
        if (exerciseProgression.getExercise().getId() == mExercises.get(mExercises.size() -1).getExercise().getId()){
            view.onExerciseProgressionsSaved();
        }
    }

    @Override
    public void onExerciseProgression(ArrayList<ExerciseProgression> exerciseProgression) {
        view.onExerciseProgression(exerciseProgression);
    }

    @Override
    public void onServerError() {
        view.displayServerError();
    }
    @Override
    public void onNetworkError() {
        view.displayNetworkError();
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

}
