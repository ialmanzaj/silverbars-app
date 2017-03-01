package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.Muscle;
import com.app.app.silverbarsapp.models.MuscleProgression;

import java.util.List;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public interface ProgressionView extends BaseView {
    void displayProgressions(List<MuscleProgression> progressions);
    void onMuscle(Muscle muscle);
}
