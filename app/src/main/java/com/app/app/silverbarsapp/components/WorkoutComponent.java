package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/03/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.WorkoutActivity;
import com.app.app.silverbarsapp.modules.WorkoutModule;
import com.app.app.silverbarsapp.presenters.WorkoutPresenter;

import dagger.Component;

/**
 * Created by Pedro Antonio Hernández on 13/06/2015.
 *
 * <p>
 *     Methods to inject dependencies into {@link }
 * </p>
 */

@ActivityScope
@Component(
        dependencies = SilverbarsComponent.class,
        modules =  WorkoutModule.class
)
public interface WorkoutComponent {

    void inject(WorkoutActivity activity);

    WorkoutPresenter getPresenter();
}
