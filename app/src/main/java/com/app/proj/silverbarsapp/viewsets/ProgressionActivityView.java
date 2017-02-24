package com.app.proj.silverbarsapp.viewsets;

import com.app.proj.silverbarsapp.models.MuscleProgression;

import java.util.List;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public interface ProgressionActivityView extends BaseView {

    void getMusclesProgresions(List<MuscleProgression> progressions);
}
