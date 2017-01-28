package com.app.proj.silverbars.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */


import com.app.proj.silverbars.interactors.MainWorkoutsInteractor;
import com.app.proj.silverbars.presenters.MainWorkoutsPresenter;
import com.app.proj.silverbars.viewsets.MainWorkoutsView;

import dagger.Module;
import dagger.Provides;

/**
 * The module due is create objects to solve dependencies
 * trough methods annotated with {@link } annotation.
 *<p>
 * I use a Module based on this
 * <a href="http://frogermcs.github.io/dagger-1-to-2-migration/">tutorial</a>
 *</p>
 */

@Module
public class MainWorkoutsModule {

    private MainWorkoutsView view;

    public MainWorkoutsModule(MainWorkoutsView view) {
        this.view = view;
    }

    @Provides
    public MainWorkoutsView provideView() {
        return view;
    }

    @Provides
    public MainWorkoutsPresenter providePresenter(MainWorkoutsView view, MainWorkoutsInteractor interactor) {
        return new MainWorkoutsPresenter(view, interactor);
    }

}
