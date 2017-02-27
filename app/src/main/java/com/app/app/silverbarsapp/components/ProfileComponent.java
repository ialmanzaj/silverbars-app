package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/26/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.fragments.ProfileFragment;
import com.app.app.silverbarsapp.modules.ProfileModule;
import com.app.app.silverbarsapp.presenters.ProfilePresenter;

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
        modules =  ProfileModule.class
)
public interface ProfileComponent {

    void inject(ProfileFragment fragment);

    ProfilePresenter getPresenter();
}
