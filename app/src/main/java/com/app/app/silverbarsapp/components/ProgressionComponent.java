package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 02/28/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.fragments.MyProgressFragment;
import com.app.app.silverbarsapp.modules.ProgressionModule;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;

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
        modules =  ProgressionModule.class
)
public interface ProgressionComponent {

    void inject(MyProgressFragment fragment);

    ProgressionPresenter getPresenter();
}
