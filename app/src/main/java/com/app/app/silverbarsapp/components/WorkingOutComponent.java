package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 01/30/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.WorkingOutActivity;
import com.app.app.silverbarsapp.modules.WorkingOutModule;
import com.app.app.silverbarsapp.presenters.WorkingOutPresenter;

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
        modules =  WorkingOutModule.class
)
public interface WorkingOutComponent {

    void inject(WorkingOutActivity activity);

    WorkingOutPresenter getPresenter();
}
