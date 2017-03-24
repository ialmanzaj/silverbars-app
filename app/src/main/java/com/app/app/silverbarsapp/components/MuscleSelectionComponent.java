package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 03/20/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.MuscleSelectionActivity;
import com.app.app.silverbarsapp.modules.MuscleSelectionModule;
import com.app.app.silverbarsapp.presenters.MuscleSelectionPresenter;

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
        modules =  MuscleSelectionModule.class
)
public interface MuscleSelectionComponent {

    void inject(MuscleSelectionActivity activity);

    MuscleSelectionPresenter getPresenter();
}
