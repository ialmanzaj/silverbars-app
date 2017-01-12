package com.app.proj.silverbars.viewsets;

import com.app.proj.silverbars.models.MuscleProgression;

import java.util.List;

/**
 * Created by isaacalmanza on 01/09/17.
 */

public interface ProgressionActivityView extends BaseView {

    void getMusclesProgresions(List<MuscleProgression> progressions);
}
