package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.database_models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.List;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public interface ResultsView extends BaseView{
    void onWorkoutDone(WorkoutDone workout);

    void isEmptyProgression();
    void onExerciseProgression(List<ExerciseProgression> exercisesProgression);
}
