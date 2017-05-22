package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 04/07/17.
 */


import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.WorkoutsDoneActivity;
import com.app.app.silverbarsapp.modules.WorkoutsDoneModule;
import com.app.app.silverbarsapp.presenters.WorkoutsDonePresenter;

import dagger.Component;

/**
 *
 * <p>
 *     Methods to inject dependencies into {@link }
 * </p>
 */

@ActivityScope
@Component(
        dependencies = SilverbarsComponent.class,
        modules =  WorkoutsDoneModule.class
)
public interface WorkoutsDoneComponent {

    void inject(WorkoutsDoneActivity activity);

    WorkoutsDonePresenter getPresenter();

}
