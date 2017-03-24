package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.models.Muscle;

import java.util.List;

/**
 * Created by isaacalmanza on 03/20/17.
 */

public interface MuscleSelectionCallback extends ServerCallback {
    void onMuscles(List<Muscle> muscles);
}
