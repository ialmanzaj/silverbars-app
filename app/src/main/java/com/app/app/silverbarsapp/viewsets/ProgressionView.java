package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.ExerciseProgression;

import java.util.List;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public interface ProgressionView extends BaseView {
    void emptyProgress();
    void displayProgressions(List<ExerciseProgression> progressions);
}
