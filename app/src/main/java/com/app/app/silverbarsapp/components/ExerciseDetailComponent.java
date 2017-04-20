package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 04/18/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.ExerciseDetailActivity;
import com.app.app.silverbarsapp.modules.ExerciseDetailModule;
import com.app.app.silverbarsapp.presenters.ExerciseDetailPresenter;

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
        modules =  ExerciseDetailModule.class
)
public interface ExerciseDetailComponent {

    void inject(ExerciseDetailActivity activity);

    ExerciseDetailPresenter getPresenter();
}
