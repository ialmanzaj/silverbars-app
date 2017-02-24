package com.app.proj.silverbarsapp.components;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.proj.silverbarsapp.ActivityScope;
import com.app.proj.silverbarsapp.fragments.MainWorkoutsFragment;
import com.app.proj.silverbarsapp.modules.MainWorkoutsModule;
import com.app.proj.silverbarsapp.presenters.MainWorkoutsPresenter;

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
        modules =  MainWorkoutsModule.class
)
public interface MainWorkoutsComponent {

    void inject(MainWorkoutsFragment fragment);

    MainWorkoutsPresenter getPresenter();

}
