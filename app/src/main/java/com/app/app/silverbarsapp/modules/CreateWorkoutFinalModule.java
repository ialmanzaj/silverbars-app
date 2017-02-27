package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/14/17.
 */

import com.app.app.silverbarsapp.interactors.CreateWorkoutFinalInteractor;
import com.app.app.silverbarsapp.presenters.CreateWorkoutFinalPresenter;
import com.app.app.silverbarsapp.viewsets.CreateWorkoutFinalView;

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
public class CreateWorkoutFinalModule {

    private CreateWorkoutFinalView view;

    public CreateWorkoutFinalModule(CreateWorkoutFinalView view) {
        this.view = view;
    }

    @Provides
    public CreateWorkoutFinalView provideView() {
        return view;
    }

    @Provides
    public CreateWorkoutFinalPresenter providePresenter(CreateWorkoutFinalView view, CreateWorkoutFinalInteractor interactor) {
        return new CreateWorkoutFinalPresenter(view, interactor);
    }



}
