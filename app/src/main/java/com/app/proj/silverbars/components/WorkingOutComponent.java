package com.app.proj.silverbars.components;

/**
 * Created by isaacalmanza on 01/30/17.
 */

import com.app.proj.silverbars.ActivityScope;
import com.app.proj.silverbars.activities.WorkingOutActivity;
import com.app.proj.silverbars.modules.WorkingOutModule;
import com.app.proj.silverbars.presenters.WorkingOutPresenter;

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
