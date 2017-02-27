package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/16/17.
 */


import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.fragments.UserWorkoutsFragment;
import com.app.app.silverbarsapp.modules.UserWorkoutsModule;
import com.app.app.silverbarsapp.presenters.UserWorkoutsPresenter;

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
        modules =  UserWorkoutsModule.class
)
public interface UserWorkoutsComponent {

    void inject(UserWorkoutsFragment fragment);

    UserWorkoutsPresenter getPresenter();
}
