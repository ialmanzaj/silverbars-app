package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.ExerciseProgression;

import java.util.List;

/**
 * Created by isaacalmanza on 02/28/17.
 */

public interface ProgressionCallback extends ServerCallback{
    void emptyProgress();
    void onProgression(List<ExerciseProgression> progressions);
}
