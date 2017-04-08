package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 04/04/17.
 */

import com.app.app.silverbarsapp.interactors.UserPreferencesInteractor;
import com.app.app.silverbarsapp.presenters.UserPreferencesPresenter;
import com.app.app.silverbarsapp.viewsets.UserPreferencesView;

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
public class UserPreferencesModule {

    private UserPreferencesView view;

    public UserPreferencesModule(UserPreferencesView view) {
        this.view = view;
    }

    @Provides
    public UserPreferencesView provideView() {
        return view;
    }

    @Provides
    public UserPreferencesPresenter providePresenter(UserPreferencesView view, UserPreferencesInteractor interactor) {
        return new UserPreferencesPresenter(view, interactor);
    }

}
