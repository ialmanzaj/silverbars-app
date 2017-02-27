package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/14/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.CreateWorkoutFinalActivity;
import com.app.app.silverbarsapp.modules.CreateWorkoutFinalModule;
import com.app.app.silverbarsapp.presenters.CreateWorkoutFinalPresenter;

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
        modules =  CreateWorkoutFinalModule.class
)
public interface CreateWorkoutFinalComponent {

    void inject(CreateWorkoutFinalActivity activity);

    CreateWorkoutFinalPresenter getPresenter();
}
