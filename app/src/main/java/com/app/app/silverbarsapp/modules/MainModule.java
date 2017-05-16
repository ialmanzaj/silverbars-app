package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 05/16/17.
 */

import com.app.app.silverbarsapp.interactors.MainInteractor;
import com.app.app.silverbarsapp.presenters.MainPresenter;
import com.app.app.silverbarsapp.viewsets.MainView;

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
public class MainModule {

    private MainView view;

    public MainModule(MainView view) {
        this.view = view;
    }

    @Provides
    public MainView provideView() {
        return view;
    }

    @Provides
    public MainPresenter providePresenter(MainView view, MainInteractor interactor) {
        return new MainPresenter(view, interactor);
    }
}
