package com.app.app.silverbarsapp.presenters;

import com.app.app.silverbarsapp.callbacks.ExerciseDetailCallback;
import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.interactors.ExerciseDetailInteractor;
import com.app.app.silverbarsapp.viewsets.ExerciseDetailView;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 04/18/17.
 */

public class ExerciseDetailPresenter extends BasePresenter implements ExerciseDetailCallback {

    private static final String TAG = ExerciseDetailPresenter.class.getSimpleName();

    ExerciseDetailInteractor interactor;
    ExerciseDetailView view;

    public ExerciseDetailPresenter(ExerciseDetailView view,ExerciseDetailInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void getProgressions(int exercise_id) throws SQLException {
        interactor.getProgressions(exercise_id,this);
    }


    @Override
    public void onProgressions(List<ExerciseProgression> progressions) {
        view.onProgressions(progressions);
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
