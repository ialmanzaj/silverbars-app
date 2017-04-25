package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 04/09/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.fragments.ProgressMonthlyFragment;
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
public interface TotalProgressionComponent {

    void inject(ProgressMonthlyFragment fragment);

    ProgressionPresenter getPresenter();
}
