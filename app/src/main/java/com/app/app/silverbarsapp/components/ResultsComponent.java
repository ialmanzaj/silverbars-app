package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 03/18/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.ResultsActivity;
import com.app.app.silverbarsapp.modules.ResultsModule;
import com.app.app.silverbarsapp.presenters.ResultsPresenter;

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
        modules =  ResultsModule.class
)
public interface ResultsComponent {

    void inject(ResultsActivity activity);

    ResultsPresenter getPresenter();
}


