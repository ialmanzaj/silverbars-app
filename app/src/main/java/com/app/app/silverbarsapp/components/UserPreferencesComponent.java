package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 04/04/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.UserPreferencesActivity;
import com.app.app.silverbarsapp.modules.UserPreferencesModule;
import com.app.app.silverbarsapp.presenters.UserPreferencesPresenter;

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
        modules =  UserPreferencesModule.class
)
public interface UserPreferencesComponent {

    void inject(UserPreferencesActivity activity);

    UserPreferencesPresenter getPresenter();
}
