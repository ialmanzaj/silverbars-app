package com.app.proj.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/03/17.
 */

import com.app.proj.silverbarsapp.ActivityScope;
import com.app.proj.silverbarsapp.activities.WorkoutActivity;
import com.app.proj.silverbarsapp.modules.WorkoutModule;
import com.app.proj.silverbarsapp.presenters.WorkoutPresenter;

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
