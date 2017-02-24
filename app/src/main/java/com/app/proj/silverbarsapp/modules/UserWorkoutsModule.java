package com.app.proj.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/16/17.
 */

import com.app.proj.silverbarsapp.interactors.UserWorkoutsInteractor;
import com.app.proj.silverbarsapp.presenters.UserWorkoutsPresenter;
import com.app.proj.silverbarsapp.viewsets.UserWorkoutsView;

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
public class UserWorkoutsModule {
    
    private UserWorkoutsView view;

    public UserWorkoutsModule(UserWorkoutsView view) {
        this.view = view;
    }

    @Provides
    public UserWorkoutsView provideView() {
        return view;
    }

    @Provides
    public UserWorkoutsPresenter providePresenter(UserWorkoutsView view, UserWorkoutsInteractor interactor) {
        return new UserWorkoutsPresenter(view, interactor);
    }

}
