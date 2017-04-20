package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.database_models.ExerciseProgression;

import java.util.List;

/**
 * Created by isaacalmanza on 04/18/17.
 */

public interface ExerciseDetailView {
    void onProgressions(List<ExerciseProgression> progressions);
}
