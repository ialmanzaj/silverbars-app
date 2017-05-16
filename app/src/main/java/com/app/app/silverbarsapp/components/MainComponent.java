package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 05/16/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.MainActivity;
import com.app.app.silverbarsapp.modules.MainModule;
import com.app.app.silverbarsapp.presenters.MainPresenter;

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
        modules =  MainModule.class
)
public interface MainComponent {

    void inject(MainActivity activity);

    MainPresenter getPresenter();
}
