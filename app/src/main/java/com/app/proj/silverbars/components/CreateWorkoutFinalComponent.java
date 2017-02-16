package com.app.proj.silverbars.components;

/**
 * Created by isaacalmanza on 02/14/17.
 */

import com.app.proj.silverbars.ActivityScope;
import com.app.proj.silverbars.activities.CreateWorkoutFinalActivity;
import com.app.proj.silverbars.modules.CreateWorkoutFinalModule;
import com.app.proj.silverbars.presenters.CreateWorkoutFinalPresenter;

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
