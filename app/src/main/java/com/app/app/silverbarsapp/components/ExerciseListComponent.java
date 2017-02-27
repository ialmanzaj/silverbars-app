package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/13/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.ExerciseListActivity;
import com.app.app.silverbarsapp.modules.ExerciseListModule;
import com.app.app.silverbarsapp.presenters.ExerciseListPresenter;

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
        modules =  ExerciseListModule.class
)
public interface ExerciseListComponent {

    void inject(ExerciseListActivity activity);

    ExerciseListPresenter getPresenter();
}
