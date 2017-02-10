package com.app.proj.silverbars.components;

/**
 * Created by isaacalmanza on 02/10/17.
 */

import com.app.proj.silverbars.ActivityScope;
import com.app.proj.silverbars.fragments.SavedWorkoutsFragment;
import com.app.proj.silverbars.modules.SavedWorkoutsModule;
import com.app.proj.silverbars.presenters.SavedWorkoutsPresenter;

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
