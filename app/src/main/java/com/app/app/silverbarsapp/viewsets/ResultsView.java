package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.ExerciseProgression;
import com.app.app.silverbarsapp.models.WorkoutDone;

import java.util.List;

/**
 * Created by isaacalmanza on 03/18/17.
 */

public interface ResultsView extends BaseView{
    //api
    void onWorkoutDone(WorkoutDone workout);
    void onExerciseProgressionsSaved();

    //database
    void isEmptyProgression();
    void onExerciseProgression(List<ExerciseProgression> exercisesProgression);
}
