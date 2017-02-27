package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.app.silverbarsapp.interactors.CreateWorkoutInteractor;
import com.app.app.silverbarsapp.presenters.CreateWorkoutPresenter;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutView;

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
public class CreateWorkoutModule {

    private CreateWorkoutView view;

    public CreateWorkoutModule(CreateWorkoutView view) {
        this.view = view;
    }

    @Provides
    public CreateWorkoutView provideView() {
        return view;
    }

    @Provides
    public CreateWorkoutPresenter providePresenter(CreateWorkoutView view, CreateWorkoutInteractor interactor) {
        return new CreateWorkoutPresenter(view, interactor);
    }



}
