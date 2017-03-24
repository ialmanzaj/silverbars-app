package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.models.Muscle;

import java.util.List;

/**
 * Created by isaacalmanza on 03/20/17.
 */

public interface MuscleSelectionView extends BaseView{
    void getMuscles(List<Muscle> muscles);

}
