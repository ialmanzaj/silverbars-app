package com.app.proj.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/10/17.
 */

import com.app.proj.silverbarsapp.ActivityScope;
import com.app.proj.silverbarsapp.fragments.SavedWorkoutsFragment;
import com.app.proj.silverbarsapp.modules.SavedWorkoutsModule;
import com.app.proj.silverbarsapp.presenters.SavedWorkoutsPresenter;

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
        modules =  SavedWorkoutsModule.class
)
public interface SavedWorkoutsComponent {

    void inject(SavedWorkoutsFragment fragment);

    SavedWorkoutsPresenter getPresenter();
}
